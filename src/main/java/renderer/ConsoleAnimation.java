package renderer;

import algorithms.SearchListener;
import maze.Cell;
import maze.Maze;

//Redraws the maze in the console after every explored cell
public class ConsoleAnimation implements SearchListener {

    private final long delayMilliseconds;

    public ConsoleAnimation(long delayMilliseconds) {
        if (delayMilliseconds < 0) {
            throw new IllegalArgumentException("Delay cannot be negative");
        }

        this.delayMilliseconds = delayMilliseconds;
    }

    @Override
    public void onVisit(Maze maze, Cell cell) {
        clearConsole();
        MazeRenderer.print(maze);
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

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
