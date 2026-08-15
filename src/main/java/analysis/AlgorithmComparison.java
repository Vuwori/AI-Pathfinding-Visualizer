package analysis;

import algorithms.PathfindingAlgorithm;
import maze.Cell;
import maze.Maze;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

//Runs several algorithms on identical copies of a maze and collects
//how long each path is, how many cells were explored and how long it took.
public class AlgorithmComparison {

    //Each algorithm runs this many times; the fastest run is reported,
    //which filters out JIT warm-up and other one-off noise
    private static final int TIMED_RUNS = 5;

    public record Result(String algorithm, int moves, int cost, int cellsExplored, double milliseconds) {

        public boolean foundPath() {
            return moves >= 0;
        }
    }

    public static List<Result> run(
            List<PathfindingAlgorithm> algorithms,
            Supplier<Maze> mazeFactory
    ) {
        List<Result> results = new ArrayList<>();

        for (PathfindingAlgorithm algorithm : algorithms) {
            results.add(measure(algorithm, mazeFactory));
        }

        return results;
    }

    private static Result measure(PathfindingAlgorithm algorithm, Supplier<Maze> mazeFactory) {
        long fastest = Long.MAX_VALUE;
        List<Cell> path = List.of();
        int[] explored = new int[1];

        for (int run = 0; run < TIMED_RUNS; run++) {
            Maze maze = mazeFactory.get();
            explored[0] = 0;

            long started = System.nanoTime();
            path = algorithm.findPath(maze, (searchedMaze, cell) -> explored[0]++);
            fastest = Math.min(fastest, System.nanoTime() - started);
        }

        return new Result(
                algorithm.getName(),
                path.size() - 1,
                Maze.pathCost(path),
                explored[0],
                fastest / 1_000_000.0
        );
    }

    public static String formatTable(List<Result> results) {
        //The first column grows to fit the longest algorithm name
        int nameWidth = "Algorithm".length();
        for (Result result : results) {
            nameWidth = Math.max(nameWidth, result.algorithm().length());
        }

        String format = "| %-" + nameWidth + "s | %11s | %9s | %14s | %9s |%n";
        StringBuilder table = new StringBuilder();

        table.append(String.format(format, "Algorithm", "Path length", "Path cost", "Cells explored", "Time (ms)"));
        table.append(String.format(
                "|%s|-------------|-----------|----------------|-----------|%n",
                "-".repeat(nameWidth + 2)
        ));

        for (Result result : results) {
            table.append(String.format(
                    format,
                    result.algorithm(),
                    result.foundPath() ? String.valueOf(result.moves()) : "no path",
                    result.foundPath() ? String.valueOf(result.cost()) : "-",
                    result.cellsExplored(),
                    String.format(Locale.ROOT, "%.3f", result.milliseconds())
            ));
        }

        return table.toString();
    }
}
