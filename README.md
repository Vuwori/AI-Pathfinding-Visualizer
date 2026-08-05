# AI Pathfinding Visualizer

An interactive Java application that visualizes how classic pathfinding algorithms search for the shortest path through a maze.

Instead of simply implementing graph algorithms, this project allows you to watch how each algorithm explores the maze and compare their behavior.

---

## Features

- Interactive maze representation
- Console-based maze visualization
- Animated Breadth-First Search (BFS), Depth-First Search (DFS) and Dijkstra's algorithm
- Choose the algorithm from the command line
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
| A* Search | 🚧 Planned |

## Usage

The project is built with Maven. The included Maven Wrapper (`./mvnw`) downloads Maven automatically, so only Java 21 needs to be installed.

Run directly:

```bash
./mvnw compile exec:java -Dexec.args="bfs"   # or: dfs, dijkstra (defaults to bfs)
```

Or build a runnable jar:

```bash
./mvnw package
java -jar target/ai-pathfinding-visualizer-1.0-SNAPSHOT.jar dijkstra
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

BFS and Dijkstra always find the shortest path. DFS finds *a* path, but it is usually much longer, which makes the difference between the algorithms easy to see.

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
    │   └── Dijkstra.java
    │
    ├── maze/
    │   ├── Cell.java
    │   ├── CellType.java
    │   └── Maze.java
    │
    ├── renderer/
    │   └── MazeRenderer.java
    │
    └── Main.java
```

---

## Technologies

- Java 21
- Maven
- Object-Oriented Programming
- Graph Traversal
- Breadth-First Search
- Depth-First Search
- Dijkstra's Algorithm
- Queues, Stacks and Priority Queues
- HashMap
- HashSet
- Console Animation

---

## Future Improvements

- Weighted cells for Dijkstra
- A* Search
- Random Maze Generator
- JavaFX GUI
- Adjustable animation speed
- Interactive controls
- Performance statistics

---

## Learning Outcomes

This project demonstrates:

- Graph traversal algorithms
- Data structures
- Object-oriented design
- Algorithm visualization
- Clean Java architecture
- Software development with Git

---

## Author

**Vanessa Amtmann**