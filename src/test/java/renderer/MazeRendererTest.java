package renderer;

import maze.Maze;
import maze.MazeGenerator;
import maze.MazeParser;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MazeRendererTest {

    private static String stripColors(String text) {
        return text.replaceAll("\033\\[[0-9;]*m", "");
    }

    @Test
    void plainTextUsesOneSymbolPerCell() {
        Maze maze = MazeParser.parse("""
                S.#
                ~4E
                """);

        String expected = "S.#" + System.lineSeparator() + "~4E" + System.lineSeparator();

        assertEquals(expected, MazeRenderer.toText(maze));
    }

    @Test
    void coloredTextHasSameLayoutAsPlainText() {
        Maze maze = new MazeGenerator(3, 0.2).generate(15, 31);

        String colored = MazeRenderer.toColoredText(maze);

        assertTrue(colored.contains("\033["), "Expected ANSI color codes");
        assertEquals(MazeRenderer.toText(maze), stripColors(colored));
    }

    @Test
    void plainTextHasNoColorCodes() {
        Maze maze = new MazeGenerator(3, 0.2).generate(15, 31);

        assertFalse(MazeRenderer.toText(maze).contains("\033"));
    }

    @Test
    void animationDrawsOneFramePerVisit() {
        Maze maze = MazeParser.parse("S..E");
        ConsoleAnimation animation = new ConsoleAnimation(0);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        try {
            animation.onVisit(maze, maze.getCell(0, 1));
            animation.onVisit(maze, maze.getCell(0, 2));
        } finally {
            System.setOut(originalOut);
        }

        String frames = output.toString();

        //The screen is cleared once, later frames only move the cursor home
        assertEquals(1, frames.split("\033\\[2J", -1).length - 1);
        assertEquals(2, frames.split("\033\\[H", -1).length - 1);
    }
}
