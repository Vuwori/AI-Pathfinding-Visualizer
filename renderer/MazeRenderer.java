package renderer;

import maze.Cell;
import maze.Maze;

public class MazeRenderer {

    public static void print(Maze maze) {
        for (int row = 0; row < maze.getRows(); row++) {
            for (int column = 0; column < maze.getColumns(); column++) {
                Cell cell = maze.getCell(row, column);

                switch (cell.getType()) {
                    case WALL -> System.out.print("#");
                    case START -> System.out.print("S");
                    case END -> System.out.print("E");
                    case EMPTY -> System.out.print(".");
                    case VISITED -> System.out.print("*");
                    case PATH -> System.out.print("P");
                }
            }

            System.out.println();
        }
    }
}
