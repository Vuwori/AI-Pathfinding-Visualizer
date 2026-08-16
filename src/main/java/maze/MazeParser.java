package maze;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

//Reads mazes from plain text, using the same characters the renderer prints:
//
//  S  start          #  wall
//  E  end            ~  mud (weight 5)
//  .  empty          2-9 a cell with that weight
//
//Visited (*) and path (P) cells are read as empty, so the output of a
//finished search can be pasted back in. Lines starting with ; are comments.
public final class MazeParser {

    private MazeParser() {
    }

    public static Maze load(Path file) throws IOException {
        try {
            return parse(Files.readString(file));
        } catch (NoSuchFileException exception) {
            throw new IOException("File not found: " + file, exception);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(file + ": " + exception.getMessage(), exception);
        }
    }

    public static Maze parse(String text) {
        List<String> lines = new ArrayList<>();

        for (String line : text.split("\\R")) {
            String trimmed = line.stripTrailing();

            if (!trimmed.isEmpty() && !trimmed.startsWith(";")) {
                lines.add(trimmed);
            }
        }

        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Maze text is empty");
        }

        int columns = lines.get(0).length();

        for (int row = 0; row < lines.size(); row++) {
            if (lines.get(row).length() != columns) {
                throw new IllegalArgumentException(
                        "Line " + (row + 1) + " has " + lines.get(row).length()
                                + " cells, expected " + columns
                );
            }
        }

        Maze maze = new Maze(lines.size(), columns);
        int[] startAndEnd = new int[2];

        for (int row = 0; row < lines.size(); row++) {
            for (int column = 0; column < columns; column++) {
                readCell(maze, row, column, lines.get(row).charAt(column), startAndEnd);
            }
        }

        if (startAndEnd[0] != 1 || startAndEnd[1] != 1) {
            throw new IllegalArgumentException(
                    "Maze needs exactly one S and one E, found "
                            + startAndEnd[0] + " and " + startAndEnd[1]
            );
        }

        return maze;
    }

    private static void readCell(Maze maze, int row, int column, char symbol, int[] startAndEnd) {
        switch (symbol) {
            case '.', '*', 'P' -> {
            }
            case '#' -> maze.setWall(row, column);
            case '~' -> maze.setWeight(row, column, MazeGenerator.MUD_WEIGHT);
            case 'S' -> {
                startAndEnd[0]++;
                maze.setStart(row, column);
            }
            case 'E' -> {
                startAndEnd[1]++;
                maze.setEnd(row, column);
            }
            default -> {
                if (symbol >= '2' && symbol <= '9') {
                    maze.setWeight(row, column, symbol - '0');
                } else {
                    throw new IllegalArgumentException(
                            "Unknown symbol '" + symbol + "' at line " + (row + 1)
                                    + ", column " + (column + 1)
                    );
                }
            }
        }
    }
}
