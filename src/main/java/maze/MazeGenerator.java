package maze;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

//Generates random mazes with the recursive backtracker algorithm.
//Rooms sit on odd rows and columns; the generator walks randomly from
//room to room, knocking down the wall between them, and backtracks when
//it gets stuck. The result is a "perfect" maze with exactly one route
//between any two rooms. A few extra walls are then removed to create
//loops, so the algorithms have more than one route to choose from.
//Optionally, patches of mud are spread over the open cells.
public class MazeGenerator {

    public static final int MINIMUM_SIZE = 5;

    public static final int MUD_WEIGHT = 5;

    private static final int LARGEST_MUD_PATCH = 8;

    private static final double DEFAULT_LOOP_CHANCE = 0.1;

    private static final int[][] DIRECTIONS = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};

    private final Random random;
    private final double loopChance;
    private final double mudCoverage;

    public MazeGenerator(long seed) {
        this(seed, 0);
    }

    public MazeGenerator(long seed, double mudCoverage) {
        this(new Random(seed), DEFAULT_LOOP_CHANCE, mudCoverage);
    }

    public MazeGenerator(Random random, double loopChance) {
        this(random, loopChance, 0);
    }

    //mudCoverage is the rough share of open cells that become mud
    public MazeGenerator(Random random, double loopChance, double mudCoverage) {
        requireFraction(loopChance, "Loop chance");
        requireFraction(mudCoverage, "Mud coverage");

        this.random = random;
        this.loopChance = loopChance;
        this.mudCoverage = mudCoverage;
    }

    //Start is placed in the top-left room, end in the bottom-right room
    public Maze generate(int rows, int columns) {
        if (rows < MINIMUM_SIZE || columns < MINIMUM_SIZE) {
            throw new IllegalArgumentException(
                    "Generated mazes must be at least "
                            + MINIMUM_SIZE + "x" + MINIMUM_SIZE
            );
        }

        Maze maze = new Maze(rows, columns);

        fillWithWalls(maze);
        carvePassages(maze);
        addLoops(maze);

        maze.setStart(1, 1);
        maze.setEnd(lastRoomIndex(rows), lastRoomIndex(columns));

        addMud(maze);

        return maze;
    }

    private void fillWithWalls(Maze maze) {
        for (int row = 0; row < maze.getRows(); row++) {
            for (int column = 0; column < maze.getColumns(); column++) {
                maze.setWall(row, column);
            }
        }
    }

    //Iterative depth-first walk, so large mazes cannot overflow the call stack
    private void carvePassages(Maze maze) {
        Deque<Cell> stack = new ArrayDeque<>();

        Cell first = maze.getCell(1, 1);
        first.setType(CellType.EMPTY);
        stack.push(first);

        while (!stack.isEmpty()) {

            Cell current = stack.peek();
            List<int[]> directions = unvisitedDirections(maze, current);

            if (directions.isEmpty()) {
                stack.pop();
                continue;
            }

            int[] direction = directions.get(random.nextInt(directions.size()));
            int row = current.getRow();
            int column = current.getColumn();

            //Remove the wall between the two rooms, then the room itself
            maze.removeWall(row + direction[0] / 2, column + direction[1] / 2);
            maze.removeWall(row + direction[0], column + direction[1]);

            stack.push(maze.getCell(row + direction[0], column + direction[1]));
        }
    }

    private List<int[]> unvisitedDirections(Maze maze, Cell cell) {
        List<int[]> directions = new ArrayList<>();

        for (int[] direction : DIRECTIONS) {
            int row = cell.getRow() + direction[0];
            int column = cell.getColumn() + direction[1];

            if (isRoom(maze, row, column)
                    && maze.getCell(row, column).getType() == CellType.WALL) {
                directions.add(direction);
            }
        }

        Collections.shuffle(directions, random);
        return directions;
    }

    //Remove some of the walls that separate two open cells in a straight line
    private void addLoops(Maze maze) {
        for (int row = 1; row < maze.getRows() - 1; row++) {
            for (int column = 1; column < maze.getColumns() - 1; column++) {

                if (maze.getCell(row, column).isWalkable()) {
                    continue;
                }

                boolean betweenRows = isOpen(maze, row - 1, column)
                        && isOpen(maze, row + 1, column);
                boolean betweenColumns = isOpen(maze, row, column - 1)
                        && isOpen(maze, row, column + 1);

                if ((betweenRows || betweenColumns) && random.nextDouble() < loopChance) {
                    maze.removeWall(row, column);
                }
            }
        }
    }

    //Grow small patches of mud from random open cells until enough is covered
    private void addMud(Maze maze) {
        List<Cell> openCells = new ArrayList<>();

        for (int row = 0; row < maze.getRows(); row++) {
            for (int column = 0; column < maze.getColumns(); column++) {
                Cell cell = maze.getCell(row, column);

                if (cell.getType() == CellType.EMPTY) {
                    openCells.add(cell);
                }
            }
        }

        if (openCells.isEmpty()) {
            return;
        }

        int target = (int) Math.round(openCells.size() * mudCoverage);
        int covered = 0;

        while (covered < target) {
            Cell seed = openCells.get(random.nextInt(openCells.size()));
            covered += growMudPatch(maze, seed, Math.min(target - covered, 1 + random.nextInt(LARGEST_MUD_PATCH)));
        }
    }

    private int growMudPatch(Maze maze, Cell seed, int size) {
        Deque<Cell> frontier = new ArrayDeque<>();
        frontier.add(seed);
        int added = 0;

        while (!frontier.isEmpty() && added < size) {
            Cell cell = frontier.poll();

            if (cell.isWeighted() || cell.getType() != CellType.EMPTY) {
                continue;
            }

            cell.setWeight(MUD_WEIGHT);
            added++;

            List<Cell> neighbors = maze.getNeighbors(cell);
            Collections.shuffle(neighbors, random);
            frontier.addAll(neighbors);
        }

        return added;
    }

    private static void requireFraction(double value, String name) {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
    }

    private boolean isRoom(Maze maze, int row, int column) {
        return row > 0
                && column > 0
                && row <= lastRoomIndex(maze.getRows())
                && column <= lastRoomIndex(maze.getColumns());
    }

    private boolean isOpen(Maze maze, int row, int column) {
        return maze.isInsideMaze(row, column) && maze.getCell(row, column).isWalkable();
    }

    //Rooms are on odd indexes, and the outer border is always a wall
    private static int lastRoomIndex(int size) {
        return size % 2 == 0 ? size - 3 : size - 2;
    }
}
