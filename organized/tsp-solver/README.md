# Traveling Salesperson Problem (TSP) Solver with Map Visualization

A from-scratch TSP solver (`Plot/Solver.java`) paired with a Swing-based map plotter
(`Plot/Plot.java`) that draws the resulting tour on real geographic maps (US states,
Canada, Europe, California, the Bay Area, Central/South America, the Caribbean — data
and images in `Plot/Maps/`).

Since TSP is NP-hard, this uses local search / optimization rather than exhaustive search:
- **Construction heuristics** for an initial tour: random shuffle, nearest-neighbor, and
  cheapest/farthest/random insertion.
- **2-opt local search** (`Plot/Improve.java`) to iteratively shorten the tour, with
  best-improvement, first-improvement, and random move-selection variants.
- **Random restarts and chained local search** to escape local optima.

## Compiling and running

```bash
javac CircularList.java ListTester.java Plot/*.java
java Plot.Solver -usa
```

Datasets available as flags: `-usa`, `-canada`, `-europe`, `-california`, `-bayarea`,
`-centralamerica`, `-southamerica`, `-caribbean`. Run with no arguments to see the full
list of construction/2-opt options.
