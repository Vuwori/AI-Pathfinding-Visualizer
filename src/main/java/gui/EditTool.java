package gui;

import maze.Cell;
import maze.Maze;
import maze.MazeGenerator;

//What a mouse click does to the maze. Start and end are never painted
//over, and only one of each exists, so moving them just relocates them.
public enum EditTool {

    WALL("Wall") {
        @Override
        public void apply(Maze maze, Cell cell) {
            if (!cell.isStart() && !cell.isEnd()) {
                maze.setWall(cell.getRow(), cell.getColumn());
            }
        }
    },

    MUD("Mud") {
        @Override
        public void apply(Maze maze, Cell cell) {
            if (!cell.isStart() && !cell.isEnd()) {
                maze.removeWall(cell.getRow(), cell.getColumn());
                maze.setWeight(cell.getRow(), cell.getColumn(), MazeGenerator.MUD_WEIGHT);
            }
        }
    },

    ERASE("Erase") {
        @Override
        public void apply(Maze maze, Cell cell) {
            maze.removeWall(cell.getRow(), cell.getColumn());
            maze.setWeight(cell.getRow(), cell.getColumn(), Cell.DEFAULT_WEIGHT);
        }
    },

    START("Move start") {
        @Override
        public void apply(Maze maze, Cell cell) {
            if (!cell.isEnd()) {
                maze.removeWall(cell.getRow(), cell.getColumn());
                maze.setStart(cell.getRow(), cell.getColumn());
            }
        }
    },

    END("Move end") {
        @Override
        public void apply(Maze maze, Cell cell) {
            if (!cell.isStart()) {
                maze.removeWall(cell.getRow(), cell.getColumn());
                maze.setEnd(cell.getRow(), cell.getColumn());
            }
        }
    };

    private final String label;

    EditTool(String label) {
        this.label = label;
    }

    public abstract void apply(Maze maze, Cell cell);

    @Override
    public String toString() {
        return label;
    }
}
