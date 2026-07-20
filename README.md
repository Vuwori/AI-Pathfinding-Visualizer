# AI Pathfinding Visualizer

An interactive Java application that visualizes how classic pathfinding algorithms search for the shortest path through a maze.

Instead of simply implementing graph algorithms, this project allows you to watch how each algorithm explores the maze and compare their behavior.

---

## Features

- Interactive maze representation
- Console-based maze visualization
- Animated Breadth-First Search (BFS)
- Displays the shortest path after the search completes
- Clean object-oriented architecture
- Extensible design for additional algorithms

---

## Current Algorithms

| Algorithm | Status |
|-----------|--------|
| Breadth-First Search (BFS) | ✅ Implemented |
| Depth-First Search (DFS) | 🚧 Planned |
| Dijkstra | 🚧 Planned |
| A* Search | 🚧 Planned |

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
- **P** = Shortest path
- **\*** = Visited cell (during animation)

---

## Project Structure

```
AI-Pathfinding-Visualizer/
│
├── algorithms/
│   └── BFS.java
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
- Object-Oriented Programming
- Graph Traversal
- Breadth-First Search
- Queues
- HashMap
- HashSet
- Console Animation

---

## Future Improvements

- DFS Visualization
- Dijkstra's Algorithm
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