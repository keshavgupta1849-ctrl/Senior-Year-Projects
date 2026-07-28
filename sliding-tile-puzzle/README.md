# Sliding-Tile Puzzle Solver (8-puzzle / 15-puzzle)

An informed-search solver for the classic sliding-tile puzzle, built on the same generic
`Search.java` engine (BFS/DFS/DLS/IDS/UCS/Greedy/A*, selectable at runtime). Supports
arbitrary grid sizes and either goal-tile arrangement (0 in top-left or bottom-right).

`HeuristicType.java` lets A* run with any of three heuristics, chosen via command-line flag:
- **Hamming** — number of misplaced tiles
- **Manhattan** — sum of horizontal + vertical tile distances from goal
- **Euclidean** — straight-line tile distance from goal

## Compiling and running

```bash
javac *.java
java Puzzle -rows 3 -cols 3
```

Use `-h hamming|manhattan|euclidean` to pick a heuristic, and `-a bfs|dfs|dls|id|ucs|gbfs|astar`
to pick a search algorithm. Run with no arguments to see all options and their defaults.
`solved.txt` contains a sample goal configuration.
