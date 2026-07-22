# Generic Search Engine + Missionaries & Cannibals + US Trip Planner

A reusable search engine (`Search.java`) built around two interfaces, `State` and `Action`,
so any problem can plug in by describing its states, legal actions, and (optionally) a
heuristic. Supports BFS, DFS, depth-limited search, iterative deepening, uniform-cost
search, greedy best-first search, and A* — plus both tree-like and graph search modes.

Two example problems are solved with it:
- `MissionariesAndCannibals.java` / `State.java` — the river-crossing puzzle, generalized
  to any number of missionaries/cannibals and boat capacity.
- `Trip.java` / `City.java` — route-finding between US states/capitals, comparing
  step-cost search against real-distance-weighted search.

## Compiling and running

```bash
javac *.java
java MissionariesAndCannibals
java Trip
```

Run either program with no arguments to see its usage/options.
