package renderer;

import maze.Cell;
import maze.Maze;
import maze.MazeGenerator;

public class MazeRenderer {

    private static final String RESET = "\033[0m";

    public static void print(Maze maze) {
        print(maze, false);
    }

    public static void print(Maze maze, boolean color) {
        System.out.print(color ? toColoredText(maze) : toText(maze));
    }

    //Color is used only when writing to a terminal, and never when the
    //NO_COLOR environment variable is set (see https://no-color.org)
    public static boolean terminalSupportsColor() {
        String noColor = System.getenv("NO_COLOR");
        return System.console() != null && (noColor == null || noColor.isEmpty());
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

    //Same layout as toText, with ANSI color codes. A code is only written
    //when the color changes, which keeps each animation frame small.
    public static String toColoredText(Maze maze) {
        StringBuilder text = new StringBuilder();

        for (int row = 0; row < maze.getRows(); row++) {
            String currentColor = null;

            for (int column = 0; column < maze.getColumns(); column++) {
                char symbol = symbol(maze.getCell(row, column));
                String color = color(symbol);

                if (!color.equals(currentColor)) {
                    text.append(color);
                    currentColor = color;
                }

                text.append(symbol);
            }

            text.append(RESET).append(System.lineSeparator());
        }

        return text.toString();
    }

    private static String color(char symbol) {
        return switch (symbol) {
            case '#' -> "\033[90m";           //gray
            case 'S' -> "\033[1;32m";         //bold green
            case 'E' -> "\033[1;31m";         //bold red
            case '*' -> "\033[36m";           //cyan
            case 'P' -> "\033[1;33m";         //bold yellow
            case '.' -> "\033[2m";            //dim
            default -> "\033[38;5;130m";      //brown, for mud and other weights
        };
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
