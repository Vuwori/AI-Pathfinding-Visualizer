package analysis;

import algorithms.Algorithms;
import algorithms.BFS;
import algorithms.PathfindingAlgorithm;
import maze.Maze;
import maze.MazeGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlgorithmComparisonTest {

    private static final List<PathfindingAlgorithm> ALGORITHMS = Algorithms.all();

    private static Map<String, AlgorithmComparison.Result> compare(long seed) {
        return AlgorithmComparison.run(
                        ALGORITHMS,
                        () -> new MazeGenerator(seed).generate(21, 41)
                )
                .stream()
                .collect(Collectors.toMap(AlgorithmComparison.Result::algorithm, Function.identity()));
    }

    @ParameterizedTest(name = "seed {0}")
    @ValueSource(longs = {1, 2, 3, 42, 2026})
    void shortestPathAlgorithmsAgreeOnPathLength(long seed) {
        Map<String, AlgorithmComparison.Result> results = compare(seed);

        int shortest = results.get("BFS").moves();

        assertEquals(shortest, results.get("Dijkstra").moves());
        assertEquals(shortest, results.get("A*").moves());
        assertTrue(results.get("DFS").moves() >= shortest);
    }

    @ParameterizedTest(name = "seed {0}")
    @ValueSource(longs = {1, 2, 3, 42, 2026})
    void aStarNeverExploresMoreThanDijkstra(long seed) {
        Map<String, AlgorithmComparison.Result> results = compare(seed);

        assertTrue(
                results.get("A*").cellsExplored() <= results.get("Dijkstra").cellsExplored(),
                "A* explored more cells than Dijkstra"
        );
    }

    @Test
    void reportsMissingPath() {
        //S # E
        AlgorithmComparison.Result result = AlgorithmComparison.run(
                List.of(new BFS()),
                () -> {
                    Maze maze = new Maze(1, 3);
                    maze.setStart(0, 0);
                    maze.setWall(0, 1);
                    maze.setEnd(0, 2);
                    return maze;
                }
        ).get(0);

        assertFalse(result.foundPath());
        assertTrue(AlgorithmComparison.formatTable(List.of(result)).contains("no path"));
    }

    @Test
    void findPathDoesNotDrawAnimation() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        try {
            new BFS().findPath(new MazeGenerator(9).generate(11, 11));
        } finally {
            System.setOut(originalOut);
        }

        assertEquals(0, output.size());
    }

    @Test
    void tableListsEveryAlgorithm() {
        String table = AlgorithmComparison.formatTable(
                AlgorithmComparison.run(ALGORITHMS, () -> new MazeGenerator(5).generate(11, 21))
        );

        for (PathfindingAlgorithm algorithm : ALGORITHMS) {
            assertTrue(table.contains(algorithm.getName()), "Missing " + algorithm.getName());
        }
    }
}
