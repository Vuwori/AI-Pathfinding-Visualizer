package renderer;

import algorithms.SearchListener;
import maze.Cell;
import maze.Maze;

//Redraws the maze in the console after every explored cell
public class ConsoleAnimation implements SearchListener {

    private static final String CLEAR_SCREEN = "\033[H\033[2J";
    private static final String CURSOR_HOME = "\033[H";

    private final long delayMilliseconds;
    private final boolean color;
    private boolean firstFrame = true;

    public ConsoleAnimation(long delayMilliseconds) {
        this(delayMilliseconds, false);
    }

    public ConsoleAnimation(long delayMilliseconds, boolean color) {
        if (delayMilliseconds < 0) {
            throw new IllegalArgumentException("Delay cannot be negative");
        }

        this.delayMilliseconds = delayMilliseconds;
        this.color = color;
    }

    @Override
    public void onVisit(Maze maze, Cell cell) {
        //Clear once, then draw each frame over the previous one,
        //which flickers much less than clearing every time
        System.out.print(firstFrame ? CLEAR_SCREEN : CURSOR_HOME);
        firstFrame = false;

        MazeRenderer.print(maze, color);
        System.out.flush();
        sleep();
    }

    private void sleep() {
        try {
            Thread.sleep(delayMilliseconds);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Animation was interrupted.", exception);
        }
    }
}
