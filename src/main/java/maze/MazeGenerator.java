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
public class MazeGenerator {

    public static final int MINIMUM_SIZE = 5;

    private static final double DEFAULT_LOOP_CHANCE = 0.1;

    private static final int[][] DIRECTIONS = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};

    private final Random random;
    private final double loopChance;

    public MazeGenerator(long seed) {
        this(new Random(seed), DEFAULT_LOOP_CHANCE);
    }

    public MazeGenerator(Random random, double loopChance) {
        if (loopChance < 0 || loopChance > 1) {
            throw new IllegalArgumentException(
                    "Loop chance must be between 0 and 1"
            );
        }

        this.random = random;
        this.loopChance = loopChance;
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
