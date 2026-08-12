package analysis;

import algorithms.PathfindingAlgorithm;
import maze.Cell;
import maze.CellType;
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

    public record Result(String algorithm, int moves, int cellsExplored, double milliseconds) {

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
        Maze maze = null;
        List<Cell> path = List.of();

        for (int run = 0; run < TIMED_RUNS; run++) {
            maze = mazeFactory.get();

            long started = System.nanoTime();
            path = algorithm.findPath(maze);
            fastest = Math.min(fastest, System.nanoTime() - started);
        }

        return new Result(
                algorithm.getName(),
                path.size() - 1,
                countExplored(maze),
                fastest / 1_000_000.0
        );
    }

    //Explored cells are marked VISITED by the algorithms; start and end never are
    private static int countExplored(Maze maze) {
        int count = 0;

        for (int row = 0; row < maze.getRows(); row++) {
            for (int column = 0; column < maze.getColumns(); column++) {
                if (maze.getCell(row, column).getType() == CellType.VISITED) {
                    count++;
                }
            }
        }

        return count;
    }

    public static String formatTable(List<Result> results) {
        String format = "| %-9s | %11s | %14s | %9s |%n";
        StringBuilder table = new StringBuilder();

        table.append(String.format(format, "Algorithm", "Path length", "Cells explored", "Time (ms)"));
        table.append(String.format("|-----------|-------------|----------------|-----------|%n"));

        for (Result result : results) {
            table.append(String.format(
                    format,
                    result.algorithm(),
                    result.foundPath() ? String.valueOf(result.moves()) : "no path",
                    result.cellsExplored(),
                    String.format(Locale.ROOT, "%.3f", result.milliseconds())
            ));
        }

        return table.toString();
    }
}
