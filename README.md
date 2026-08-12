# AI Pathfinding Visualizer

[![Tests](https://github.com/Vuwori/AI-Pathfinding-Visualizer/actions/workflows/tests.yml/badge.svg)](https://github.com/Vuwori/AI-Pathfinding-Visualizer/actions/workflows/tests.yml)

An interactive Java application that visualizes how classic pathfinding algorithms search for the shortest path through a maze.

Instead of simply implementing graph algorithms, this project allows you to watch how each algorithm explores the maze and compare their behavior.

---

## Features

- Interactive maze representation
- Console-based maze visualization
- Animated Breadth-First Search (BFS), Depth-First Search (DFS), Dijkstra's algorithm and A* search
- Random maze generator (recursive backtracker) with reproducible seeds
- Comparison mode that runs every algorithm on the same maze and reports path length, cells explored and run time
- Choose the algorithm, maze size and animation speed from the command line
- Displays the shortest path after the search completes
- Clean object-oriented architecture
- Extensible design for additional algorithms

---

## Current Algorithms

| Algorithm | Status |
|-----------|--------|
| Breadth-First Search (BFS) | ✅ Implemented |
| Depth-First Search (DFS) | ✅ Implemented |
| Dijkstra | ✅ Implemented |
| A* Search (Manhattan heuristic) | ✅ Implemented |

## Usage

The project is built with Maven. The included Maven Wrapper (`./mvnw`) downloads Maven automatically, so only Java 21 needs to be installed.

Run directly:

```bash
./mvnw compile exec:java -Dexec.args="bfs"   # or: dfs, dijkstra, astar, compare (defaults to bfs)
```

Or build a runnable jar:

```bash
./mvnw package
java -jar target/ai-pathfinding-visualizer-1.0-SNAPSHOT.jar dijkstra
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

Options:

| Option | Description |
|--------|-------------|
| `--random` | Use a randomly generated maze instead of the example maze |
| `--seed=N` | Generate the random maze from a fixed seed (implies `--random`) |
| `--size=RxC` | Size of the random maze, e.g. `21x41` (default `15x31`) |
| `--delay=MS` | Milliseconds between animation frames (default `100`) |

```bash
java -jar target/ai-pathfinding-visualizer-1.0-SNAPSHOT.jar astar --random --size=21x41 --delay=20
java -jar target/ai-pathfinding-visualizer-1.0-SNAPSHOT.jar compare --seed=2026 --size=21x41
```

Random runs print their seed, so any interesting maze can be reproduced later.

Run the tests:

```bash
./mvnw test
```

BFS, Dijkstra and A* always find the shortest path. DFS finds *a* path, but it is usually much longer, which makes the difference between the algorithms easy to see.

---

## Testing

The project has a JUnit 5 test suite covering the maze model, the maze generator, the comparison mode and all four algorithms. Every algorithm is checked for valid paths (start to end, one step at a time, never through walls), unreachable ends and missing start or end cells. BFS, Dijkstra and A* are also checked for finding the shortest path, and A* is checked to never explore more cells than Dijkstra. Generated mazes are checked to be fully connected, surrounded by walls and reproducible from their seed.

The tests run automatically on GitHub Actions for every push and pull request to `main`.

---

## Example

### Initial Maze

```text
.................
.S....#..........
.###.............
.................
.................
...............E.
.................
```

### BFS Result

```text
.................
.SPPP.#..........
.###P............
....P............
....P............
....PPPPPPPPPPPE.
.................
```

- **S** = Start
- **E** = End
- **#** = Wall
- **.** = Empty cell
- **P** = Final path
- **\*** = Visited cell (during animation)

### Comparing the Algorithms

`compare` runs every algorithm on the same maze. On the example maze above:

```text
| Algorithm | Path length | Cells explored | Time (ms) |
|-----------|-------------|----------------|-----------|
| BFS       |          18 |            111 |     0.204 |
| DFS       |          52 |             51 |     0.090 |
| Dijkstra  |          18 |            109 |     0.165 |
| A*        |          18 |             17 |     0.049 |
```

A* finds the same shortest path as BFS and Dijkstra while exploring only a fraction of the maze, because its heuristic steers the search towards the end. DFS explores few cells here, but its path is almost three times as long.

On a generated 21x41 maze (`--seed=2026`) the walls force more detours, so the gap narrows but A* still explores about a third fewer cells:

```text
| Algorithm | Path length | Cells explored |
|-----------|-------------|----------------|
| BFS       |          74 |            407 |
| DFS       |         140 |            204 |
| Dijkstra  |          74 |            400 |
| A*        |          74 |            268 |
```

Times are the fastest of five runs and vary between machines.

---

## Project Structure

```
AI-Pathfinding-Visualizer/
│
├── pom.xml
├── mvnw / mvnw.cmd
│
└── src/main/java/
    ├── algorithms/
    │   ├── PathfindingAlgorithm.java
    │   ├── BFS.java
    │   ├── DFS.java
    │   ├── Dijkstra.java
    │   └── AStar.java
    │
    ├── analysis/
    │   └── AlgorithmComparison.java
    │
    ├── maze/
    │   ├── Cell.java
    │   ├── CellType.java
    │   ├── Maze.java
    │   └── MazeGenerator.java
    │
    ├── renderer/
    │   └── MazeRenderer.java
    │
    └── Main.java

src/test/java/
    ├── algorithms/
    │   └── PathfindingAlgorithmTest.java
    │
    ├── analysis/
    │   └── AlgorithmComparisonTest.java
    │
    └── maze/
        ├── CellTest.java
        ├── MazeGeneratorTest.java
        └── MazeTest.java
```

---

## Technologies

- Java 21
- Maven
- JUnit 5
- GitHub Actions
- Object-Oriented Programming
- Graph Traversal
- Breadth-First Search
- Depth-First Search
- Dijkstra's Algorithm
- A* Search and Heuristics
- Procedural Maze Generation
- Queues, Stacks and Priority Queues
- HashMap
- HashSet
- Console Animation

---

## Future Improvements

- Weighted cells for Dijkstra and A*
- Diagonal movement
- JavaFX GUI
- Interactive controls

---

## Learning Outcomes

This project demonstrates:

- Graph traversal algorithms
- Informed search with admissible heuristics
- Procedural generation with reproducible randomness
- Measuring and comparing algorithm performance
- Data structures
- Object-oriented design
- Algorithm visualization
- Clean Java architecture
- Software development with Git

---

## Author

**Vanessa Amtmann**