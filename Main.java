import maze.Cell;
import maze.Maze;

public class Main {

    public static void main(String[] args) {
        Maze maze = new Maze(7, 17);

        maze.setStart(1, 1);
        maze.setEnd(5, 15);

        maze.setWall(1, 6);
        maze.setWall(2, 1);
        maze.setWall(2, 2);
        maze.setWall(2, 3);

        Cell start = maze.getStartCell();

        System.out.println("Start: " + start);
        System.out.println("End: " + maze.getEndCell());
        System.out.println(
                "Start neighbors: " + maze.getNeighbors(start)
        );
    }
}