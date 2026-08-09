package algorithms;

import maze.Cell;
import maze.Maze;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PathfindingAlgorithmTest {

    private PrintStream originalOut;

    static Stream<PathfindingAlgorithm> allAlgorithms() {
        return Stream.of(new BFS(), new DFS(), new Dijkstra());
    }

    static Stream<PathfindingAlgorithm> shortestPathAlgorithms() {
        return Stream.of(new BFS(), new Dijkstra());
    }

    //The algorithms animate to the console; hide that output during tests
    @BeforeEach
    void silenceConsole() {
        originalOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
    }

    @AfterEach
    void restoreConsole() {
        System.setOut(originalOut);
    }

    //The example maze from Main and the README
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

    //A path is valid if it goes from start to end in single steps over walkable cells
    private static void assertValidPath(Maze maze, List<Cell> path) {
        assertTrue(!path.isEmpty(), "Expected a path to be found");
        assertEquals(maze.getStartCell(), path.get(0));
        assertEquals(maze.getEndCell(), path.get(path.size() - 1));

        for (int i = 0; i < path.size(); i++) {
            Cell cell = path.get(i);
            assertTrue(cell.isWalkable(), "Path goes through a wall at " + cell);

            if (i > 0) {
                Cell previous = path.get(i - 1);
                int distance = Math.abs(cell.getRow() - previous.getRow())
                        + Math.abs(cell.getColumn() - previous.getColumn());
                assertEquals(1, distance, "Path jumps from " + previous + " to " + cell);
            }
        }

        assertEquals(path.size(), path.stream().distinct().count(), "Path visits a cell twice");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allAlgorithms")
    void findsValidPathInExampleMaze(PathfindingAlgorithm algorithm) {
        Maze maze = exampleMaze();

        assertValidPath(maze, algorithm.findPathAnimated(maze, 0));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("shortestPathAlgorithms")
    void findsShortestPathInExampleMaze(PathfindingAlgorithm algorithm) {
        Maze maze = exampleMaze();

        List<Cell> path = algorithm.findPathAnimated(maze, 0);

        assertEquals(18, path.size() - 1);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("shortestPathAlgorithms")
    void findsShortestPathAroundWall(PathfindingAlgorithm algorithm) {
        //S . . . E
        //. # # # .
        //. . . . .
        Maze maze = new Maze(3, 5);
        maze.setStart(0, 0);
        maze.setEnd(0, 4);
        maze.setWall(1, 1);
        maze.setWall(1, 2);
        maze.setWall(1, 3);

        List<Cell> path = algorithm.findPathAnimated(maze, 0);

        assertValidPath(maze, path);
        assertEquals(4, path.size() - 1);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allAlgorithms")
    void returnsEmptyPathWhenEndIsUnreachable(PathfindingAlgorithm algorithm) {
        //S # E
        Maze maze = new Maze(1, 3);
        maze.setStart(0, 0);
        maze.setWall(0, 1);
        maze.setEnd(0, 2);

        assertTrue(algorithm.findPathAnimated(maze, 0).isEmpty());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allAlgorithms")
    void findsPathBetweenAdjacentCells(PathfindingAlgorithm algorithm) {
        Maze maze = new Maze(1, 2);
        maze.setStart(0, 0);
        maze.setEnd(0, 1);

        List<Cell> path = algorithm.findPathAnimated(maze, 0);

        assertEquals(List.of(maze.getCell(0, 0), maze.getCell(0, 1)), path);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allAlgorithms")
    void requiresStartAndEnd(PathfindingAlgorithm algorithm) {
        Maze noEnd = new Maze(2, 2);
        noEnd.setStart(0, 0);

        Maze noStart = new Maze(2, 2);
        noStart.setEnd(1, 1);

        assertThrows(IllegalStateException.class, () -> algorithm.findPathAnimated(noEnd, 0));
        assertThrows(IllegalStateException.class, () -> algorithm.findPathAnimated(noStart, 0));
    }

    @Test
    void algorithmsHaveDisplayNames() {
        assertEquals("BFS", new BFS().getName());
        assertEquals("DFS", new DFS().getName());
        assertEquals("Dijkstra", new Dijkstra().getName());
    }
}
