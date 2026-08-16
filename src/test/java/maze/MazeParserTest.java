package maze;

import algorithms.Algorithms;
import algorithms.PathfindingAlgorithm;
import org.junit.jupiter.api.Test;
import renderer.MazeRenderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MazeParserTest {

    @Test
    void readsEverySymbol() {
        Maze maze = MazeParser.parse("""
                S.#
                ~3E
                """);

        assertEquals(2, maze.getRows());
        assertEquals(3, maze.getColumns());
        assertEquals(maze.getCell(0, 0), maze.getStartCell());
        assertEquals(maze.getCell(1, 2), maze.getEndCell());
        assertEquals(CellType.EMPTY, maze.getCell(0, 1).getType());
        assertEquals(CellType.WALL, maze.getCell(0, 2).getType());
        assertEquals(MazeGenerator.MUD_WEIGHT, maze.getCell(1, 0).getWeight());
        assertEquals(3, maze.getCell(1, 1).getWeight());
    }

    @Test
    void ignoresCommentsBlankLinesAndTrailingSpaces() {
        Maze maze = MazeParser.parse("""
                ; a comment

                S.E\s\s
                ...
                """);

        assertEquals(2, maze.getRows());
        assertEquals(3, maze.getColumns());
    }

    @Test
    void readsSearchOutputAsEmptyCells() {
        Maze maze = MazeParser.parse("SP*E");

        assertEquals(CellType.EMPTY, maze.getCell(0, 1).getType());
        assertEquals(CellType.EMPTY, maze.getCell(0, 2).getType());
    }

    @Test
    void renderedMazeCanBeReadBack() {
        Maze original = new MazeGenerator(99, 0.2).generate(15, 31);
        original.setWeight(3, 1, 7);

        String text = MazeRenderer.toText(original);
        Maze copy = MazeParser.parse(text);

        assertEquals(text, MazeRenderer.toText(copy));
        assertEquals(7, copy.getCell(3, 1).getWeight());
    }

    @Test
    void rejectsBadMazes() {
        assertThrows(IllegalArgumentException.class, () -> MazeParser.parse(""));
        assertThrows(IllegalArgumentException.class, () -> MazeParser.parse("S..\n.E"));
        assertThrows(IllegalArgumentException.class, () -> MazeParser.parse("S.x.E"));
        assertThrows(IllegalArgumentException.class, () -> MazeParser.parse("...E"));
        assertThrows(IllegalArgumentException.class, () -> MazeParser.parse("S.S.E"));
        assertThrows(IllegalArgumentException.class, () -> MazeParser.parse("S...."));
    }

    @Test
    void errorsNameTheProblem() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> MazeParser.parse("S..\n.?E")
        );

        assertTrue(exception.getMessage().contains("line 2, column 2"), exception.getMessage());
    }

    //Every maze shipped in the mazes folder must load and be solvable
    @Test
    void sampleMazesAreSolvable() throws IOException {
        List<Path> files;

        try (Stream<Path> listing = Files.list(Path.of("mazes"))) {
            files = listing.filter(file -> file.toString().endsWith(".txt")).sorted().toList();
        }

        assertFalse(files.isEmpty(), "No sample mazes found");

        for (Path file : files) {
            for (PathfindingAlgorithm algorithm : Algorithms.all()) {
                Maze maze = MazeParser.load(file);

                assertFalse(algorithm.findPath(maze).isEmpty(), algorithm + " found no path in " + file);
            }
        }
    }
}
