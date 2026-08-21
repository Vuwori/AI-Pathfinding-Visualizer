package gui;

import maze.Cell;
import maze.CellType;
import maze.Maze;
import maze.MazeGenerator;
import maze.MazeParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EditToolTest {

    //S . #
    //~ . E
    private static Maze maze() {
        return MazeParser.parse("S.#\n~.E");
    }

    @Test
    void wallToolDrawsWalls() {
        Maze maze = maze();

        EditTool.WALL.apply(maze, maze.getCell(0, 1));

        assertEquals(CellType.WALL, maze.getCell(0, 1).getType());
    }

    @Test
    void mudToolTurnsWallsIntoMud() {
        Maze maze = maze();

        EditTool.MUD.apply(maze, maze.getCell(0, 2));

        assertEquals(CellType.EMPTY, maze.getCell(0, 2).getType());
        assertEquals(MazeGenerator.MUD_WEIGHT, maze.getCell(0, 2).getWeight());
    }

    @Test
    void eraseRemovesWallsAndMud() {
        Maze maze = maze();

        EditTool.ERASE.apply(maze, maze.getCell(0, 2));
        EditTool.ERASE.apply(maze, maze.getCell(1, 0));

        assertEquals(CellType.EMPTY, maze.getCell(0, 2).getType());
        assertEquals(Cell.DEFAULT_WEIGHT, maze.getCell(1, 0).getWeight());
    }

    @Test
    void startAndEndCannotBePaintedOver() {
        Maze maze = maze();

        EditTool.WALL.apply(maze, maze.getStartCell());
        EditTool.MUD.apply(maze, maze.getEndCell());

        assertTrue(maze.getCell(0, 0).isStart());
        assertTrue(maze.getCell(1, 2).isEnd());
        assertTrue(!maze.getEndCell().isWeighted());
    }

    @Test
    void startAndEndCanBeMovedEvenOntoWalls() {
        Maze maze = maze();

        EditTool.START.apply(maze, maze.getCell(0, 2));
        EditTool.END.apply(maze, maze.getCell(0, 0));

        assertEquals(maze.getCell(0, 2), maze.getStartCell());
        assertEquals(maze.getCell(0, 0), maze.getEndCell());
        assertEquals(CellType.EMPTY, maze.getCell(1, 2).getType());
    }

    @Test
    void startCannotBeMovedOntoEnd() {
        Maze maze = maze();

        EditTool.START.apply(maze, maze.getEndCell());

        assertEquals(maze.getCell(0, 0), maze.getStartCell());
        assertEquals(maze.getCell(1, 2), maze.getEndCell());
    }
}
