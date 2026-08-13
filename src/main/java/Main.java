import algorithms.Algorithms;
import algorithms.PathfindingAlgorithm;
import analysis.AlgorithmComparison;
import java.util.List;
import java.util.Random;
import maze.Cell;
import maze.CellType;
import maze.Maze;
import maze.MazeGenerator;
import renderer.ConsoleAnimation;
import renderer.MazeRenderer;

public class Main {

    private static final String USAGE = """
            Usage: java Main [bfs|dfs|dijkstra|astar|compare] [options]

            compare runs every algorithm on the same maze and prints a table.

            Options:
              --random        Use a randomly generated maze
              --seed=N        Seed for the random maze (implies --random)
              --size=RxC      Size of the random maze, e.g. 21x41 (default 15x31)
              --delay=MS      Milliseconds between animation frames (default 100)""";

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

        PathfindingAlgorithm algorithm = Algorithms.byName(options.algorithm()).orElse(null);

        if (algorithm == null) {
            System.out.println("Unknown algorithm: " + options.algorithm());
            System.out.println(USAGE);
            return;
        }

        Maze maze = createMaze(options);

        //Run the animated search
        List<Cell> path = algorithm.findPath(maze, new ConsoleAnimation(options.delay()));

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
        MazeRenderer.print(maze);

        System.out.println();
        System.out.println("Path cells: " + path.size());
        System.out.println("Moves: " + (path.size() - 1));

        if (options.random()) {
            System.out.println("Maze seed: " + options.seed());
        }
    }

    private static void compareAlgorithms(Options options) {
        //Every algorithm gets its own fresh copy of the same maze
        List<AlgorithmComparison.Result> results =
                AlgorithmComparison.run(Algorithms.all(), () -> createMaze(options));

        MazeRenderer.print(createMaze(options));
        System.out.println();
        System.out.print(AlgorithmComparison.formatTable(results));

        if (options.random()) {
            System.out.println();
            System.out.println("Maze seed: " + options.seed());
        }
    }

    private static Maze createMaze(Options options) {
        if (options.random()) {
            return new MazeGenerator(options.seed()).generate(options.rows(), options.columns());
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
            boolean random,
            long seed,
            int rows,
            int columns,
            long delay
    ) {

        static Options parse(String[] args) {
            String algorithm = "bfs";
            boolean random = false;
            Long seed = null;
            int rows = 15;
            int columns = 31;
            long delay = 100;

            for (String arg : args) {
                try {
                    if (arg.equals("--random")) {
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

            if (delay < 0) {
                throw new IllegalArgumentException("Delay cannot be negative");
            }

            //Pick a seed when none is given, so the maze can be reproduced later
            long finalSeed = seed != null ? seed : new Random().nextLong();

            return new Options(algorithm, random, finalSeed, rows, columns, delay);
        }

        private static String value(String arg) {
            return arg.substring(arg.indexOf('=') + 1);
        }
    }
}
