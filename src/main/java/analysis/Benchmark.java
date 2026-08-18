package analysis;

import algorithms.PathfindingAlgorithm;
import maze.Maze;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.LongFunction;

//Runs the comparison on many mazes, one per seed, and averages the results.
//A single maze can flatter or punish an algorithm; hundreds of them show
//how it behaves in general.
public class Benchmark {

    //optimalShare: how often the path cost matched the cheapest path any algorithm found
    public record Summary(
            String algorithm,
            int mazes,
            double averageMoves,
            double averageCost,
            double optimalShare,
            double averageCellsExplored,
            double averageMilliseconds
    ) {
    }

    public static List<Summary> run(
            List<PathfindingAlgorithm> algorithms,
            long firstSeed,
            int mazes,
            LongFunction<Maze> mazeForSeed
    ) {
        if (mazes <= 0) {
            throw new IllegalArgumentException("Benchmark needs at least one maze");
        }

        Map<String, List<AlgorithmComparison.Result>> resultsByAlgorithm = new LinkedHashMap<>();
        Map<String, Integer> optimalCounts = new LinkedHashMap<>();

        for (PathfindingAlgorithm algorithm : algorithms) {
            resultsByAlgorithm.put(algorithm.getName(), new ArrayList<>());
            optimalCounts.put(algorithm.getName(), 0);
        }

        for (long seed = firstSeed; seed < firstSeed + mazes; seed++) {
            long mazeSeed = seed;
            List<AlgorithmComparison.Result> results =
                    AlgorithmComparison.run(algorithms, () -> mazeForSeed.apply(mazeSeed));

            int cheapest = results.stream()
                    .filter(AlgorithmComparison.Result::foundPath)
                    .mapToInt(AlgorithmComparison.Result::cost)
                    .min()
                    .orElse(-1);

            for (AlgorithmComparison.Result result : results) {
                resultsByAlgorithm.get(result.algorithm()).add(result);

                if (result.foundPath() && result.cost() == cheapest) {
                    optimalCounts.merge(result.algorithm(), 1, Integer::sum);
                }
            }
        }

        List<Summary> summaries = new ArrayList<>();

        resultsByAlgorithm.forEach((algorithm, results) -> summaries.add(new Summary(
                algorithm,
                mazes,
                results.stream().mapToInt(AlgorithmComparison.Result::moves).average().orElse(0),
                results.stream().mapToInt(AlgorithmComparison.Result::cost).average().orElse(0),
                (double) optimalCounts.get(algorithm) / mazes,
                results.stream().mapToInt(AlgorithmComparison.Result::cellsExplored).average().orElse(0),
                results.stream().mapToDouble(AlgorithmComparison.Result::milliseconds).average().orElse(0)
        )));

        return summaries;
    }

    public static String formatTable(List<Summary> summaries) {
        int nameWidth = "Algorithm".length();
        for (Summary summary : summaries) {
            nameWidth = Math.max(nameWidth, summary.algorithm().length());
        }

        String format = "| %-" + nameWidth + "s | %10s | %9s | %7s | %13s | %9s |%n";
        StringBuilder table = new StringBuilder();

        table.append(String.format(format, "Algorithm", "Avg length", "Avg cost", "Optimal", "Avg explored", "Avg ms"));
        table.append(String.format(
                "|%s|------------|-----------|---------|---------------|-----------|%n",
                "-".repeat(nameWidth + 2)
        ));

        for (Summary summary : summaries) {
            table.append(String.format(
                    format,
                    summary.algorithm(),
                    String.format(Locale.ROOT, "%.1f", summary.averageMoves()),
                    String.format(Locale.ROOT, "%.1f", summary.averageCost()),
                    String.format(Locale.ROOT, "%.0f%%", summary.optimalShare() * 100),
                    String.format(Locale.ROOT, "%.1f", summary.averageCellsExplored()),
                    String.format(Locale.ROOT, "%.3f", summary.averageMilliseconds())
            ));
        }

        return table.toString();
    }
}
