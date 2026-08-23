package cli;

import maze.MazeGenerator;
import renderer.MazeRenderer;

import java.util.Random;

//The parsed command line. Every option has a default, so running the
//program without arguments animates BFS on the example maze.
public record Options(
        String algorithm,
        String file,
        boolean random,
        long seed,
        int rows,
        int columns,
        double mud,
        long delay,
        int runs,
        boolean color
) {

    public static Options parse(String[] args) {
        return parse(args, MazeRenderer.terminalSupportsColor());
    }

    //colorByDefault is passed in so tests do not depend on the terminal they run in
    public static Options parse(String[] args, boolean colorByDefault) {
        String algorithm = "bfs";
        String file = null;
        boolean random = false;
        Long seed = null;
        int rows = 15;
        int columns = 31;
        double mud = 0;
        long delay = 100;
        int runs = 100;
        boolean color = colorByDefault;

        for (String arg : args) {
            try {
                if (arg.startsWith("--file=")) {
                    file = value(arg);
                } else if (arg.equals("--random")) {
                    random = true;
                } else if (arg.startsWith("--seed=")) {
                    seed = Long.parseLong(value(arg));
                    random = true;
                } else if (arg.startsWith("--size=")) {
                    String[] size = value(arg).toLowerCase().split("x");
                    if (size.length != 2) {
                        throw new IllegalArgumentException("Size must look like 21x41");
                    }
                    rows = Integer.parseInt(size[0]);
                    columns = Integer.parseInt(size[1]);
                } else if (arg.startsWith("--mud=")) {
                    mud = Double.parseDouble(value(arg));
                    random = true;
                } else if (arg.startsWith("--runs=")) {
                    runs = Integer.parseInt(value(arg));
                } else if (arg.equals("--no-color")) {
                    color = false;
                } else if (arg.startsWith("--delay=")) {
                    delay = Long.parseLong(value(arg));
                } else if (arg.startsWith("--")) {
                    throw new IllegalArgumentException("Unknown option: " + arg);
                } else {
                    algorithm = arg;
                }
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Invalid number in " + arg);
            }
        }

        if (rows < MazeGenerator.MINIMUM_SIZE || columns < MazeGenerator.MINIMUM_SIZE) {
            throw new IllegalArgumentException(
                    "Maze size must be at least "
                            + MazeGenerator.MINIMUM_SIZE + "x" + MazeGenerator.MINIMUM_SIZE
            );
        }

        if (mud < 0 || mud > 1) {
            throw new IllegalArgumentException("Mud must be between 0 and 1");
        }

        if (runs <= 0) {
            throw new IllegalArgumentException("Runs must be at least 1");
        }

        if (delay < 0) {
            throw new IllegalArgumentException("Delay cannot be negative");
        }

        //Pick a seed when none is given, so the maze can be reproduced later
        long finalSeed = seed != null ? seed : new Random().nextInt(1_000_000);

        return new Options(algorithm, file, random, finalSeed, rows, columns, mud, delay, runs, color);
    }

    private static String value(String arg) {
        return arg.substring(arg.indexOf('=') + 1);
    }
}
