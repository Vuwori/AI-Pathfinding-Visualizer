package gui;

import algorithms.Algorithms;
import maze.CellType;
import maze.Maze;
import maze.MazeParser;
import org.junit.jupiter.api.Test;

import javax.swing.table.DefaultTableModel;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComparisonDialogTest {

    @Test
    void tableHasOneRowPerAlgorithm() {
        Maze maze = MazeParser.parse("S..#\n.#.E");

        DefaultTableModel model = ComparisonDialog.createModel(maze);

        assertEquals(Algorithms.all().size(), model.getRowCount());
        assertEquals(ComparisonDialog.COLUMNS.length, model.getColumnCount());
        assertEquals("BFS", model.getValueAt(0, 0));
        assertEquals(4, model.getValueAt(0, 1));
    }

    @Test
    void comparingDoesNotChangeTheMazeOnScreen() {
        Maze maze = MazeParser.parse("S..#\n.#.E");

        ComparisonDialog.createModel(maze);

        assertEquals(0, countVisited(maze));
    }

    private static int countVisited(Maze maze) {
        int visited = 0;

        for (int row = 0; row < maze.getRows(); row++) {
            for (int column = 0; column < maze.getColumns(); column++) {
                if (maze.getCell(row, column).getType() == CellType.VISITED) {
                    visited++;
                }
            }
        }

        return visited;
    }
}
