import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Runs the program end to end and checks what it prints
class MainTest {

    private static String run(String... args) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        try {
            Main.main(args);
        } finally {
            System.setOut(originalOut);
        }

        return output.toString();
    }

    @Test
    void solvesTheExampleMaze() {
        String output = run("astar", "--delay=0", "--no-color");

        assertTrue(output.contains("A* completed:"));
        assertTrue(output.contains("Moves: 18"));
        assertTrue(output.contains("Path cost: 18"));
    }

    @Test
    void printsSeedOfRandomMaze() {
        String output = run("dijkstra", "--seed=7", "--size=11x21", "--delay=0", "--no-color");

        assertTrue(output.contains("Maze seed: 7"));
    }

    @Test
    void comparesAlgorithmsOnAFile() {
        String output = run("compare", "--file=mazes/swamp.txt", "--no-color");

        assertTrue(output.contains("| BFS"));
        assertTrue(output.contains("| Bidirectional BFS"));
        assertFalse(output.contains("Maze seed"));
    }

    @Test
    void benchmarksRandomMazes() {
        String output = run("benchmark", "--runs=3", "--size=11x11", "--seed=1");

        assertTrue(output.contains("Benchmarking 3 random 11x11 mazes (seeds 1 to 3, 0% mud)"));
        assertTrue(output.contains("| A*"));
    }

    @Test
    void reportsMazesWithoutPath() {
        String output = run("bfs", "--file=src/test/resources/blocked.txt", "--delay=0", "--no-color");

        assertTrue(output.contains("No path found."));
    }

    @Test
    void explainsBadInput() {
        assertTrue(run("teleport").contains("Unknown algorithm: teleport"));
        assertTrue(run("--size=2x2").contains("Usage:"));
        assertTrue(run("bfs", "--file=missing.txt").contains("File not found"));
    }

    @Test
    void guiNeedsADisplay() {
        //Tests run headless, so the window cannot open
        assertTrue(run("gui").contains("needs a display"));
    }
}
