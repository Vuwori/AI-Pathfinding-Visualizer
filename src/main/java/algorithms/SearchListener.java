package algorithms;

import maze.Cell;
import maze.Maze;

//Notified every time an algorithm explores a cell. This keeps the
//algorithms free of any drawing code: the console animation, the GUI
//and the statistics all plug in through this interface.
@FunctionalInterface
public interface SearchListener {

    SearchListener NONE = (maze, cell) -> {
    };

    void onVisit(Maze maze, Cell cell);
}
