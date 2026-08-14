package maze;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MazeGeneratorTest {

    //Flood fill from the start to find every cell that can be reached
    private static Set<Cell> reachableCells(Maze maze) {
        Set<Cell> reached = new HashSet<>();
        Queue<Cell> queue = new ArrayDeque<>();

        reached.add(maze.getStartCell());
        queue.offer(maze.getStartCell());

        while (!queue.isEmpty()) {
            for (Cell neighbor : maze.getNeighbors(queue.poll())) {
                if (reached.add(neighbor)) {
                    queue.offer(neighbor);
                }
            }
        }

        return reached;
    }

    private static String layout(Maze maze) {
        StringBuilder builder = new StringBuilder();

        for (int row = 0; row < maze.getRows(); row++) {
            for (int column = 0; column < maze.getColumns(); column++) {
                builder.append(maze.getCell(row, column).getType().ordinal());
            }
        }

        return builder.toString();
    }

    @ParameterizedTest(name = "{0}x{1}")
    @CsvSource({"5, 5", "7, 17", "21, 41", "8, 10"})
    void everyOpenCellIsReachableFromStart(int rows, int columns) {
        Maze maze = new MazeGenerator(42).generate(rows, columns);

        Set<Cell> reached = reachableCells(maze);

        assertTrue(reached.contains(maze.getEndCell()), "End must be reachable");

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                Cell cell = maze.getCell(row, column);

                if (cell.isWalkable()) {
                    assertTrue(reached.contains(cell), "Unreachable open cell " + cell);
                }
            }
        }
    }

    @Test
    void outerBorderIsAlwaysWall() {
        Maze maze = new MazeGenerator(7).generate(9, 13);

        for (int column = 0; column < 13; column++) {
            assertEquals(CellType.WALL, maze.getCell(0, column).getType());
            assertEquals(CellType.WALL, maze.getCell(8, column).getType());
        }

        for (int row = 0; row < 9; row++) {
            assertEquals(CellType.WALL, maze.getCell(row, 0).getType());
            assertEquals(CellType.WALL, maze.getCell(row, 12).getType());
        }
    }

    @Test
    void startAndEndAreInOppositeCorners() {
        Maze maze = new MazeGenerator(1).generate(11, 21);

        assertEquals(maze.getCell(1, 1), maze.getStartCell());
        assertEquals(maze.getCell(9, 19), maze.getEndCell());
    }

    @Test
    void sameSeedGivesSameMaze() {
        Maze first = new MazeGenerator(123).generate(15, 31);
        Maze second = new MazeGenerator(123).generate(15, 31);
        Maze other = new MazeGenerator(124).generate(15, 31);

        assertEquals(layout(first), layout(second));
        assertTrue(!layout(first).equals(layout(other)), "Different seeds should differ");
    }

    @Test
    void withoutLoopsTheMazeIsATree() {
        //A perfect maze on n open cells has exactly n - 1 connections
        Maze maze = new MazeGenerator(new Random(5), 0).generate(15, 15);

        int openCells = 0;
        int connections = 0;

        for (int row = 0; row < 15; row++) {
            for (int column = 0; column < 15; column++) {
                Cell cell = maze.getCell(row, column);

                if (cell.isWalkable()) {
                    openCells++;
                    connections += maze.getNeighbors(cell).size();
                }
            }
        }

        assertEquals(openCells - 1, connections / 2);
    }

    @Test
    void invalidSettingsAreRejected() {
        MazeGenerator generator = new MazeGenerator(0);

        assertThrows(IllegalArgumentException.class, () -> generator.generate(4, 10));
        assertThrows(IllegalArgumentException.class, () -> generator.generate(10, 3));
        assertThrows(IllegalArgumentException.class, () -> new MazeGenerator(new Random(), 1.5));
        assertThrows(IllegalArgumentException.class, () -> new MazeGenerator(1, -0.1));
    }

    @Test
    void mudCoversRoughlyTheRequestedShare() {
        Maze maze = new MazeGenerator(11, 0.25).generate(31, 61);

        int open = 0;
        int mud = 0;

        for (int row = 0; row < 31; row++) {
            for (int column = 0; column < 61; column++) {
                Cell cell = maze.getCell(row, column);

                if (cell.isWalkable()) {
                    open++;
                }

                if (cell.isWeighted()) {
                    mud++;
                    assertEquals(MazeGenerator.MUD_WEIGHT, cell.getWeight());
                    assertTrue(cell.isWalkable(), "Walls cannot be mud");
                }
            }
        }

        assertEquals(0.25, (double) mud / open, 0.02);
        assertTrue(!maze.getStartCell().isWeighted() && !maze.getEndCell().isWeighted());
    }

    @Test
    void noMudByDefault() {
        Maze maze = new MazeGenerator(11).generate(21, 21);

        for (int row = 0; row < 21; row++) {
            for (int column = 0; column < 21; column++) {
                assertTrue(!maze.getCell(row, column).isWeighted());
            }
        }
    }
}
