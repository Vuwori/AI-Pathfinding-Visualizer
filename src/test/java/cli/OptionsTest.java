package cli;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionsTest {

    private static Options parse(String... args) {
        return Options.parse(args, true);
    }

    @Test
    void defaults() {
        Options options = parse();

        assertEquals("bfs", options.algorithm());
        assertNull(options.file());
        assertFalse(options.random());
        assertEquals(15, options.rows());
        assertEquals(31, options.columns());
        assertEquals(0, options.mud());
        assertEquals(100, options.delay());
        assertEquals(100, options.runs());
        assertTrue(options.color());
    }

    @Test
    void readsEveryOption() {
        Options options = parse(
                "astar", "--file=mazes/swamp.txt", "--seed=42", "--size=21x41",
                "--mud=0.3", "--delay=5", "--runs=7", "--no-color"
        );

        assertEquals("astar", options.algorithm());
        assertEquals("mazes/swamp.txt", options.file());
        assertTrue(options.random());
        assertEquals(42, options.seed());
        assertEquals(21, options.rows());
        assertEquals(41, options.columns());
        assertEquals(0.3, options.mud());
        assertEquals(5, options.delay());
        assertEquals(7, options.runs());
        assertFalse(options.color());
    }

    @Test
    void mudImpliesRandomMaze() {
        assertTrue(parse("--mud=0.1").random());
        assertTrue(parse("--random").random());
    }

    @Test
    void randomSeedIsSmallAndPositive() {
        for (int i = 0; i < 20; i++) {
            long seed = parse("--random").seed();
            assertTrue(seed >= 0 && seed < 1_000_000, "Unexpected seed " + seed);
        }
    }

    @Test
    void rejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> parse("--size=21"));
        assertThrows(IllegalArgumentException.class, () -> parse("--size=3x3"));
        assertThrows(IllegalArgumentException.class, () -> parse("--size=axb"));
        assertThrows(IllegalArgumentException.class, () -> parse("--mud=1.5"));
        assertThrows(IllegalArgumentException.class, () -> parse("--delay=-1"));
        assertThrows(IllegalArgumentException.class, () -> parse("--runs=0"));
        assertThrows(IllegalArgumentException.class, () -> parse("--seed=abc"));
        assertThrows(IllegalArgumentException.class, () -> parse("--teleport"));
    }
}
