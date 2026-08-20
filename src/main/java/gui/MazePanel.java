package gui;

import maze.Cell;
import maze.Maze;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

//Draws a maze as a grid of colored squares. The cell size follows the
//panel size, so the maze always fills the window.
public class MazePanel extends JPanel {

    static final Color BACKGROUND = new Color(0xF7F7F5);
    static final Color GRID_LINE = new Color(0xE4E4E0);
    static final Color EMPTY = Color.WHITE;
    static final Color WALL = new Color(0x2E3440);
    static final Color MUD = new Color(0xC9A27E);
    static final Color VISITED = new Color(0xA5D8F3);
    static final Color VISITED_MUD = new Color(0x8DB3C2);
    static final Color PATH = new Color(0xFFC93C);
    static final Color PATH_MUD = new Color(0xE0A030);
    static final Color START = new Color(0x2BB673);
    static final Color END = new Color(0xE5484D);

    private static final int PREFERRED_CELL_SIZE = 22;
    private static final int MAX_PREFERRED_SIZE = 900;

    private Maze maze;

    public MazePanel(Maze maze) {
        setBackground(BACKGROUND);
        setMaze(maze);
    }

    public Maze getMaze() {
        return maze;
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        int cellSize = Math.max(
                4,
                Math.min(PREFERRED_CELL_SIZE, MAX_PREFERRED_SIZE / Math.max(maze.getRows(), maze.getColumns()))
        );

        return new Dimension(maze.getColumns() * cellSize, maze.getRows() * cellSize);
    }

    //Size of one cell in pixels at the current panel size
    int cellSize() {
        return Math.max(1, Math.min(getWidth() / maze.getColumns(), getHeight() / maze.getRows()));
    }

    //Left and top margin that centers the maze in the panel
    private int offsetX() {
        return (getWidth() - cellSize() * maze.getColumns()) / 2;
    }

    private int offsetY() {
        return (getHeight() - cellSize() * maze.getRows()) / 2;
    }

    //The cell under a pixel position, or null when the position is outside the maze
    public Cell cellAt(int x, int y) {
        int size = cellSize();
        int column = Math.floorDiv(x - offsetX(), size);
        int row = Math.floorDiv(y - offsetY(), size);

        return maze.isInsideMaze(row, column) ? maze.getCell(row, column) : null;
    }

    public static Color colorOf(Cell cell) {
        return switch (cell.getType()) {
            case WALL -> WALL;
            case START -> START;
            case END -> END;
            case EMPTY -> cell.isWeighted() ? MUD : EMPTY;
            case VISITED -> cell.isWeighted() ? VISITED_MUD : VISITED;
            case PATH -> cell.isWeighted() ? PATH_MUD : PATH;
        };
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = cellSize();
        int left = offsetX();
        int top = offsetY();

        for (int row = 0; row < maze.getRows(); row++) {
            for (int column = 0; column < maze.getColumns(); column++) {
                Cell cell = maze.getCell(row, column);
                int x = left + column * size;
                int y = top + row * size;

                g.setColor(cell.isStart() || cell.isEnd() ? EMPTY : colorOf(cell));
                g.fillRect(x, y, size, size);

                //Start and end are drawn as dots, so they stand out from the path
                if (cell.isStart() || cell.isEnd()) {
                    int inset = Math.max(1, size / 6);
                    g.setColor(colorOf(cell));
                    g.fillOval(x + inset, y + inset, size - 2 * inset, size - 2 * inset);
                }
            }
        }

        //Grid lines only when the cells are big enough for them to help
        if (size >= 8) {
            g.setColor(GRID_LINE);
            g.setStroke(new BasicStroke(1));

            for (int row = 0; row <= maze.getRows(); row++) {
                g.drawLine(left, top + row * size, left + maze.getColumns() * size, top + row * size);
            }

            for (int column = 0; column <= maze.getColumns(); column++) {
                g.drawLine(left + column * size, top, left + column * size, top + maze.getRows() * size);
            }
        }

        g.dispose();
    }
}
