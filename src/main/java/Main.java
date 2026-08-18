import algorithms.Algorithms;
import algorithms.PathfindingAlgorithm;
import analysis.AlgorithmComparison;
import analysis.Benchmark;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import maze.Cell;
import maze.CellType;
import maze.Maze;
import maze.MazeGenerator;
import maze.MazeParser;
import renderer.ConsoleAnimation;
import renderer.MazeRenderer;

public class Main {

    private static final String USAGE = """
            Usage: java Main [bfs|dfs|dijkstra|astar|greedy|bidirectional|compare|benchmark] [options]

            compare   runs every algorithm on the same maze and prints a table.
            benchmark runs every algorithm on many random mazes and averages the results.

            Options:
              --file=PATH     Load a maze from a text file, e.g. mazes/swamp.txt
              --random        Use a randomly generated maze
              --seed=N        Seed for the random maze (implies --random)
              --size=RxC      Size of the random maze, e.g. 21x41 (default 15x31)
              --mud=F         Share of the random maze covered in mud, 0 to 1 (default 0)
              --delay=MS      Milliseconds between animation frames (default 100)
              --runs=N        Number of mazes for benchmark (default 100)
              --no-color      Print without colors""";

    public static void main(String[] args) {

        Options options;

        try {
            options = Options.parse(args);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
            System.out.println(USAGE);
            return;
        }

        if (options.algorithm().equalsIgnoreCase("compare")) {
            compareAlgorithms(options);
            return;
        }

        if (options.algorithm().equalsIgnoreCase("benchmark")) {
            benchmarkAlgorithms(options);
            return;
        }

        PathfindingAlgorithm algorithm = Algorithms.byName(options.algorithm()).orElse(null);

        if (algorithm == null) {
            System.out.println("Unknown algorithm: " + options.algorithm());
            System.out.println(USAGE);
            return;
        }

        Maze maze;

        try {
            maze = createMaze(options);
        } catch (IOException | IllegalArgumentException exception) {
            System.out.println("Could not load maze: " + exception.getMessage());
            return;
        }

        //Run the animated search
        List<Cell> path = algorithm.findPath(maze, new ConsoleAnimation(options.delay(), options.color()));

        //Stop if no path was found
        if (path.isEmpty()) {
            System.out.println("No path found.");
            return;
        }

        //Remove all VISITED cells from the maze
        maze.clearSearchResults();

        //Mark only the final path
        for (Cell cell : path) {
            if (!cell.isStart() && !cell.isEnd()) {
                cell.setType(CellType.PATH);
            }
        }

        //Print the clean final result
        System.out.println();
        System.out.println(algorithm.getName() + " completed:");
        MazeRenderer.print(maze, options.color());

        System.out.println();
        System.out.println("Path cells: " + path.size());
        System.out.println("Moves: " + (path.size() - 1));
        System.out.println("Path cost: " + Maze.pathCost(path));

        if (options.random() && options.file() == null) {
            System.out.println("Maze seed: " + options.seed());
        }
    }

    private static void compareAlgorithms(Options options) {
        Maze maze;

        try {
            maze = createMaze(options);
        } catch (IOException | IllegalArgumentException exception) {
            System.out.println("Could not load maze: " + exception.getMessage());
            return;
        }

        //Every algorithm gets its own fresh copy of the same maze
        String layout = MazeRenderer.toText(maze);
        List<AlgorithmComparison.Result> results =
                AlgorithmComparison.run(Algorithms.all(), () -> MazeParser.parse(layout));

        MazeRenderer.print(maze, options.color());
        System.out.println();
        System.out.print(AlgorithmComparison.formatTable(results));

        if (options.random() && options.file() == null) {
            System.out.println();
            System.out.println("Maze seed: " + options.seed());
        }
    }

    private static void benchmarkAlgorithms(Options options) {
        System.out.printf(
                Locale.ROOT,
                "Benchmarking %d random %dx%d mazes (seeds %d to %d, %.0f%% mud)...%n%n",
                options.runs(),
                options.rows(),
                options.columns(),
                options.seed(),
                options.seed() + options.runs() - 1,
                options.mud() * 100
        );

        List<Benchmark.Summary> summaries = Benchmark.run(
                Algorithms.all(),
                options.seed(),
                options.runs(),
                seed -> new MazeGenerator(seed, options.mud()).generate(options.rows(), options.columns())
        );

        System.out.print(Benchmark.formatTable(summaries));
        System.out.println();
        System.out.println("Optimal = how often the path was as cheap as the best path found.");
    }

    private static Maze createMaze(Options options) throws IOException {
        if (options.file() != null) {
            return MazeParser.load(Path.of(options.file()));
        }

        if (options.random()) {
            return new MazeGenerator(options.seed(), options.mud())
                    .generate(options.rows(), options.columns());
        }

        return exampleMaze();
    }

    //The small hand-made maze shown in the README
    private static Maze exampleMaze() {
        Maze maze = new Maze(7, 17);

        maze.setStart(1, 1);
        maze.setEnd(5, 15);

        maze.setWall(1, 6);
        maze.setWall(2, 1);
        maze.setWall(2, 2);
        maze.setWall(2, 3);

        return maze;
    }

    private record Options(
            String algorithm,
            String file,
            boolean random,
            long seed,
            int rows,
            int columns,
            double mud,
            long delay,
            int runs,
            boolean color
    ) {

        static Options parse(String[] args) {
            String algorithm = "bfs";
            String file = null;
            boolean random = false;
            Long seed = null;
            int rows = 15;
            int columns = 31;
            double mud = 0;
            long delay = 100;
            int runs = 100;
            boolean color = MazeRenderer.terminalSupportsColor();

            for (String arg : args) {
                try {
                    if (arg.startsWith("--file=")) {
                        file = value(arg);
                    } else if (arg.equals("--random")) {
                        random = true;
                    } else if (arg.startsWith("--seed=")) {
                        seed = Long.parseLong(value(arg));
                        random = true;
                    } else if (arg.startsWith("--size=")) {
                        String[] size = value(arg).toLowerCase().split("x");
                        if (size.length != 2) {
                            throw new IllegalArgumentException("Size must look like 21x41");
                        }
                        rows = Integer.parseInt(size[0]);
                        columns = Integer.parseInt(size[1]);
                    } else if (arg.startsWith("--mud=")) {
                        mud = Double.parseDouble(value(arg));
                        random = true;
                    } else if (arg.startsWith("--runs=")) {
                        runs = Integer.parseInt(value(arg));
                    } else if (arg.equals("--no-color")) {
                        color = false;
                    } else if (arg.startsWith("--delay=")) {
                        delay = Long.parseLong(value(arg));
                    } else if (arg.startsWith("--")) {
                        throw new IllegalArgumentException("Unknown option: " + arg);
                    } else {
                        algorithm = arg;
                    }
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException("Invalid number in " + arg);
                }
            }

            if (rows < MazeGenerator.MINIMUM_SIZE || columns < MazeGenerator.MINIMUM_SIZE) {
                throw new IllegalArgumentException(
                        "Maze size must be at least "
                                + MazeGenerator.MINIMUM_SIZE + "x" + MazeGenerator.MINIMUM_SIZE
                );
            }

            if (mud < 0 || mud > 1) {
                throw new IllegalArgumentException("Mud must be between 0 and 1");
            }

            if (runs <= 0) {
                throw new IllegalArgumentException("Runs must be at least 1");
            }

            if (delay < 0) {
                throw new IllegalArgumentException("Delay cannot be negative");
            }

            //Pick a seed when none is given, so the maze can be reproduced later
            long finalSeed = seed != null ? seed : new Random().nextInt(1_000_000);

            return new Options(algorithm, file, random, finalSeed, rows, columns, mud, delay, runs, color);
        }

        private static String value(String arg) {
            return arg.substring(arg.indexOf('=') + 1);
        }
    }
}
