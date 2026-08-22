package maze;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MazeTest {

    @Test
    void newMazeIsAllEmpty() {
        Maze maze = new Maze(3, 4);

        assertEquals(3, maze.getRows());
        assertEquals(4, maze.getColumns());
        assertNull(maze.getStartCell());
        assertNull(maze.getEndCell());

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 4; column++) {
                assertEquals(CellType.EMPTY, maze.getCell(row, column).getType());
            }
        }
    }

    @Test
    void invalidDimensionsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Maze(0, 5));
        assertThrows(IllegalArgumentException.class, () -> new Maze(5, -1));
    }

    @Test
    void positionsOutsideMazeAreRejected() {
        Maze maze = new Maze(3, 3);

        assertFalse(maze.isInsideMaze(-1, 0));
        assertFalse(maze.isInsideMaze(0, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> maze.getCell(3, 0));
    }

    @Test
    void movingStartClearsPreviousStart() {
        Maze maze = new Maze(3, 3);

        maze.setStart(0, 0);
        maze.setStart(1, 1);

        assertEquals(CellType.EMPTY, maze.getCell(0, 0).getType());
        assertEquals(CellType.START, maze.getCell(1, 1).getType());
        assertSame(maze.getCell(1, 1), maze.getStartCell());
    }

    @Test
    void movingEndClearsPreviousEnd() {
        Maze maze = new Maze(3, 3);

        maze.setEnd(0, 0);
        maze.setEnd(2, 2);

        assertEquals(CellType.EMPTY, maze.getCell(0, 0).getType());
        assertSame(maze.getCell(2, 2), maze.getEndCell());
    }

    @Test
    void startAndEndCannotShareACell() {
        Maze maze = new Maze(3, 3);
        maze.setStart(1, 1);

        assertThrows(IllegalArgumentException.class, () -> maze.setEnd(1, 1));

        maze.setEnd(2, 2);
        assertThrows(IllegalArgumentException.class, () -> maze.setStart(2, 2));
    }

    @Test
    void startAndEndCannotBePlacedOnWalls() {
        Maze maze = new Maze(3, 3);
        maze.setWall(1, 1);

        assertThrows(IllegalArgumentException.class, () -> maze.setStart(1, 1));
        assertThrows(IllegalArgumentException.class, () -> maze.setEnd(1, 1));
    }

    @Test
    void startAndEndCannotBecomeWalls() {
        Maze maze = new Maze(3, 3);
        maze.setStart(0, 0);
        maze.setEnd(2, 2);

        assertThrows(IllegalStateException.class, () -> maze.setWall(0, 0));
        assertThrows(IllegalStateException.class, () -> maze.setWall(2, 2));
    }

    @Test
    void removeWallOnlyAffectsWalls() {
        Maze maze = new Maze(3, 3);
        maze.setWall(1, 1);
        maze.setStart(0, 0);

        maze.removeWall(1, 1);
        maze.removeWall(0, 0);

        assertEquals(CellType.EMPTY, maze.getCell(1, 1).getType());
        assertEquals(CellType.START, maze.getCell(0, 0).getType());
    }

    @Test
    void neighborsInMiddleAreAllFourDirections() {
        Maze maze = new Maze(3, 3);

        List<Cell> neighbors = maze.getNeighbors(maze.getCell(1, 1));

        assertEquals(4, neighbors.size());
        assertTrue(neighbors.contains(maze.getCell(0, 1)));
        assertTrue(neighbors.contains(maze.getCell(2, 1)));
        assertTrue(neighbors.contains(maze.getCell(1, 0)));
        assertTrue(neighbors.contains(maze.getCell(1, 2)));
    }

    @Test
    void neighborsExcludeWallsAndCellsOutsideMaze() {
        Maze maze = new Maze(3, 3);
        maze.setWall(0, 1);

        List<Cell> neighbors = maze.getNeighbors(maze.getCell(0, 0));

        assertEquals(List.of(maze.getCell(1, 0)), neighbors);
    }

    @Test
    void clearSearchResultsKeepsMazeLayout() {
        Maze maze = new Maze(1, 5);
        maze.setStart(0, 0);
        maze.setWall(0, 1);
        maze.getCell(0, 2).setType(CellType.VISITED);
        maze.getCell(0, 3).setType(CellType.PATH);
        maze.setEnd(0, 4);

        maze.clearSearchResults();

        assertEquals(CellType.START, maze.getCell(0, 0).getType());
        assertEquals(CellType.WALL, maze.getCell(0, 1).getType());
        assertEquals(CellType.EMPTY, maze.getCell(0, 2).getType());
        assertEquals(CellType.EMPTY, maze.getCell(0, 3).getType());
        assertEquals(CellType.END, maze.getCell(0, 4).getType());
    }

    @Test
    void wallsCannotBeWeighted() {
        Maze maze = new Maze(2, 2);
        maze.setWall(0, 1);

        assertThrows(IllegalStateException.class, () -> maze.setWeight(0, 1, 5));
    }

    @Test
    void turningMudIntoWallResetsWeight() {
        Maze maze = new Maze(2, 2);
        maze.setWeight(0, 1, 5);
        maze.setWall(0, 1);
        maze.removeWall(0, 1);

        assertEquals(Cell.DEFAULT_WEIGHT, maze.getCell(0, 1).getWeight());
    }

    @Test
    void pathCostSumsWeightsOfEnteredCells() {
        Maze maze = new Maze(1, 4);
        maze.setWeight(0, 0, 9);
        maze.setWeight(0, 2, 5);

        List<Cell> path = List.of(
                maze.getCell(0, 0),
                maze.getCell(0, 1),
                maze.getCell(0, 2),
                maze.getCell(0, 3)
        );

        //The start cell is never entered, so its weight does not count
        assertEquals(1 + 5 + 1, Maze.pathCost(path));
        assertEquals(0, Maze.pathCost(List.of(maze.getCell(0, 0))));
        assertEquals(0, Maze.pathCost(List.of()));
    }

    @Test
    void copyKeepsLayoutButNotSearchResults() {
        Maze maze = new Maze(2, 3);
        maze.setStart(0, 0);
        maze.setEnd(1, 2);
        maze.setWall(0, 1);
        maze.setWeight(1, 1, 5);
        maze.getCell(1, 1).setType(CellType.VISITED);

        Maze copy = maze.copy();

        assertEquals(copy.getCell(0, 0), copy.getStartCell());
        assertEquals(copy.getCell(1, 2), copy.getEndCell());
        assertEquals(CellType.WALL, copy.getCell(0, 1).getType());
        assertEquals(CellType.EMPTY, copy.getCell(1, 1).getType());
        assertEquals(5, copy.getCell(1, 1).getWeight());

        //Changing the copy leaves the original alone
        copy.setWall(1, 0);
        assertEquals(CellType.EMPTY, maze.getCell(1, 0).getType());
    }
}
