package gui;

import maze.Cell;
import maze.CellType;
import maze.Maze;
import maze.MazeParser;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MazePanelTest {

    //S . #
    //~ . E
    private static MazePanel panel(int width, int height) {
        Maze maze = MazeParser.parse("S.#\n~.E");
        MazePanel panel = new MazePanel(maze);
        panel.setSize(width, height);
        return panel;
    }

    private static BufferedImage render(MazePanel panel) {
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        panel.paint(graphics);
        graphics.dispose();
        return image;
    }

    private static Color colorAt(BufferedImage image, int x, int y) {
        return new Color(image.getRGB(x, y));
    }

    @Test
    void cellSizeFitsTheSmallerDimension() {
        assertEquals(20, panel(60, 40).cellSize());
        assertEquals(10, panel(30, 100).cellSize());
    }

    @Test
    void findsCellUnderMouse() {
        MazePanel panel = panel(60, 40);
        Maze maze = panel.getMaze();

        assertEquals(maze.getCell(0, 0), panel.cellAt(5, 5));
        assertEquals(maze.getCell(1, 2), panel.cellAt(59, 39));
        assertEquals(maze.getCell(0, 1), panel.cellAt(25, 19));
    }

    @Test
    void positionsOutsideTheMazeHaveNoCell() {
        //The maze is centered: 60x40 pixels inside a 100x40 panel leaves 20 pixels on each side
        MazePanel panel = panel(100, 40);

        assertNull(panel.cellAt(10, 10));
        assertNull(panel.cellAt(95, 10));
        assertEquals(panel.getMaze().getCell(0, 0), panel.cellAt(25, 5));
    }

    @Test
    void paintsEachCellInItsColor() {
        BufferedImage image = render(panel(60, 40));

        assertEquals(MazePanel.EMPTY, colorAt(image, 30, 10));
        assertEquals(MazePanel.WALL, colorAt(image, 50, 10));
        assertEquals(MazePanel.MUD, colorAt(image, 10, 30));
        assertEquals(MazePanel.START, colorAt(image, 10, 10));
        assertEquals(MazePanel.END, colorAt(image, 50, 30));
    }

    @Test
    void searchStatesHaveTheirOwnColors() {
        Cell plain = new Cell(0, 0, CellType.VISITED);
        Cell mud = new Cell(0, 0, CellType.PATH);
        mud.setWeight(5);

        assertEquals(MazePanel.VISITED, MazePanel.colorOf(plain));
        assertEquals(MazePanel.PATH_MUD, MazePanel.colorOf(mud));
    }
}
