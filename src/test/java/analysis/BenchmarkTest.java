package analysis;

import algorithms.Algorithms;
import maze.MazeGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BenchmarkTest {

    private static Map<String, Benchmark.Summary> benchmark(double mud) {
        return Benchmark.run(
                        Algorithms.all(),
                        100,
                        20,
                        seed -> new MazeGenerator(seed, mud).generate(15, 31)
                )
                .stream()
                .collect(Collectors.toMap(Benchmark.Summary::algorithm, Function.identity()));
    }

    @Test
    void dijkstraAndAStarAreAlwaysOptimal() {
        Map<String, Benchmark.Summary> summaries = benchmark(0.25);

        assertEquals(1.0, summaries.get("Dijkstra").optimalShare());
        assertEquals(1.0, summaries.get("A*").optimalShare());
        assertEquals(summaries.get("Dijkstra").averageCost(), summaries.get("A*").averageCost());
    }

    @Test
    void bfsIsOptimalWithoutMud() {
        Map<String, Benchmark.Summary> summaries = benchmark(0);

        assertEquals(1.0, summaries.get("BFS").optimalShare());
        assertEquals(1.0, summaries.get("Bidirectional BFS").optimalShare());
    }

    @Test
    void aStarExploresLessThanDijkstraOnAverage() {
        Map<String, Benchmark.Summary> summaries = benchmark(0);

        assertTrue(summaries.get("A*").averageCellsExplored()
                < summaries.get("Dijkstra").averageCellsExplored());
    }

    @Test
    void summariesCoverEveryAlgorithmAndMaze() {
        List<Benchmark.Summary> summaries = Benchmark.run(
                Algorithms.all(), 1, 3, seed -> new MazeGenerator(seed).generate(11, 11)
        );

        assertEquals(Algorithms.all().size(), summaries.size());
        summaries.forEach(summary -> assertEquals(3, summary.mazes()));

        String table = Benchmark.formatTable(summaries);
        summaries.forEach(summary -> assertTrue(table.contains(summary.algorithm())));
    }

    @Test
    void needsAtLeastOneMaze() {
        assertThrows(IllegalArgumentException.class, () -> Benchmark.run(
                Algorithms.all(), 1, 0, seed -> new MazeGenerator(seed).generate(11, 11)
        ));
    }
}
