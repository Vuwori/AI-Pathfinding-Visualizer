package algorithms;

import java.util.List;
import java.util.Optional;

//The one place that lists every algorithm, used by the command line,
//the comparison mode and the tests
public final class Algorithms {

    private Algorithms() {
    }

    public static List<PathfindingAlgorithm> all() {
        return List.of(new BFS(), new DFS(), new Dijkstra(), new AStar());
    }

    //Accepts the display name or a short command-line name, ignoring case
    public static Optional<PathfindingAlgorithm> byName(String name) {
        String wanted = name.toLowerCase();

        return all().stream()
                .filter(algorithm -> commandName(algorithm).equals(wanted)
                        || algorithm.getName().toLowerCase().equals(wanted))
                .findFirst();
    }

    //"A*" becomes "astar", "Dijkstra" becomes "dijkstra"
    public static String commandName(PathfindingAlgorithm algorithm) {
        return algorithm.getName().toLowerCase().replace("*", "star");
    }
}
