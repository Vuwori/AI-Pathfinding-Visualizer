package renderer;

import maze.Cell;
import maze.Maze;
import maze.MazeGenerator;

public class MazeRenderer {

    public static void print(Maze maze) {
        System.out.print(toText(maze));
    }

    //The same format MazeParser reads, so a maze can be saved and loaded again
    public static String toText(Maze maze) {
        StringBuilder text = new StringBuilder();

        for (int row = 0; row < maze.getRows(); row++) {

            for (int column = 0; column < maze.getColumns(); column++) {
                text.append(symbol(maze.getCell(row, column)));
            }

            text.append(System.lineSeparator());
        }

        return text.toString();
    }

    public static char symbol(Cell cell) {
        return switch (cell.getType()) {
            case WALL -> '#';
            case START -> 'S';
            case END -> 'E';
            case EMPTY -> emptySymbol(cell);
            case VISITED -> '*';
            case PATH -> 'P';
        };
    }

    private static char emptySymbol(Cell cell) {
        if (!cell.isWeighted()) {
            return '.';
        }

        if (cell.getWeight() == MazeGenerator.MUD_WEIGHT || cell.getWeight() > 9) {
            return '~';
        }

        return (char) ('0' + cell.getWeight());
    }
}
