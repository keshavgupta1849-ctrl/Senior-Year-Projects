# AI & Algorithms Coursework Projects

A collection of Java projects built for an Advanced Topics in AI / Advanced Topics in Machine Learning course, covering uninformed and informed search, adversarial game search, local search optimization, and gradient-based machine learning — implemented from scratch (no external AI/ML libraries).

Each project below went through multiple checkpoints as it was built out over the year. This README identifies the **final, most complete version of each project** — the checkpoint folders are earlier snapshots kept for history and are safe to leave out of a "greatest hits" view of the repo. 

In addition if you test these out there will be some errors. For example for TSP solver if you try the Canada map it does it output the correct one, but a close to correct one. These all are not completley correct and efficent because I did them on a time limit while learning for the first time in class

---

## Which folder is the final version of each project

| Project | Final folder to keep | Earlier checkpoints (superseded) |
|---|---|---|
| Generic search engine + Missionaries & Cannibals + US road-trip planner | `GuptaCheckPoint3/` | `Gupta_SearchProjectCheckpoint1_java/`, `GuptaCheckPoint2_java/` |
| 8-puzzle / 15-puzzle solver (A* and friends) | `GuptaSlidingTilePuzzle/` | `Guptacheckpoint4/` (earlier, no heuristic choice) |
| Traveling Salesperson Problem solver | `TSP_Checkpoint4/` | `tsp1/`, `CheckPoint_3Gupta/`, `THETSP/` (each is an earlier snapshot missing the final `Solver.java`) |
| 3D Tic-Tac-Toe with alpha-beta search | `3DTicTacToe/` | `TicTacToeCheckpoint1/` (2D-only version), `Bit_java/` (a standalone copy of one utility class, now folded into 3DTicTacToe) |
| Gradient descent / linear regression classifier | `submission/` | `LinearRegressionThingy_java/`, `GradientDescent2/` |

A smaller, standalone exercise — `Gupta_AIExcer2/BigClimb.java`, a heuristic hike-time estimator — is included as a bonus below but is more of a warm-up exercise than a full project.

---

## 1. Generic Search Engine, Missionaries & Cannibals, and US Road-Trip Planner
**Folder:** `GuptaCheckPoint3/`

The core of this project is a reusable, generic search engine (`Search.java`) built around two simple interfaces — `State` and `Action` — so that *any* problem can be solved just by describing its states, legal actions, and (optionally) a heuristic. The engine supports seven algorithms selectable at runtime:

- Breadth-first search, depth-first search, depth-limited search, iterative deepening
- Uniform-cost search (Dijkstra's algorithm)
- Greedy best-first search
- A* search

It also supports both **tree-like search** (no duplicate-state checking) and **graph search** (tracks reached states), so the tradeoffs between the two can be directly compared.

Two problems are solved with this same engine:
- **Missionaries and Cannibals** (`MissionariesAndCannibals.java`, `State.java`) — the classic river-crossing puzzle, generalized to any number of missionaries/cannibals and any boat capacity.
- **US road-trip planner** (`Trip.java`, `City.java`) — finds routes between US states/capitals, comparing step-cost search (fewest hops) against real-distance-weighted search (shortest actual mileage).

## 2. Sliding-Tile Puzzle Solver (8-puzzle / 15-puzzle)
**Folder:** `GuptaSlidingTilePuzzle/`

An informed-search solver for the classic sliding-tile puzzle, built on the same generic `Search.java` engine as above (BFS/DFS/DLS/IDS/UCS/Greedy/A*, selectable at runtime). The puzzle itself supports arbitrary grid sizes (not just the standard 3×3 or 4×4) and either goal-tile arrangement.

The interesting part is `HeuristicType.java` and the heuristic logic in `Board.java`, which lets the solver run A* with any of three different heuristics, picked as a command-line flag:
- **Hamming distance** — number of misplaced tiles
- **Manhattan distance** — sum of horizontal + vertical distances of tiles from their goal positions
- **Euclidean distance** — straight-line distance of tiles from their goal positions

This makes it easy to empirically compare how heuristic quality affects the number of nodes A* has to expand to solve the same puzzle — a direct, hands-on demonstration of heuristic dominance and admissibility.

## 3. Traveling Salesperson Problem (TSP) Solver with Map Visualization
**Folder:** `TSP_Checkpoint4/src/`

A from-scratch TSP solver (`Solver.java`) combined with a Swing-based map plotter (`Plot.java`) that draws the resulting tour directly on real geographic maps (US states, Canada, Europe, California, the Bay Area, Central/South America, the Caribbean).

The solver is a local-search / optimization approach rather than exhaustive search, since TSP is NP-hard:
- **Construction heuristics** for building an initial tour: random shuffle, nearest-neighbor, and three flavors of cheapest/farthest/random insertion
- **2-opt local search** (`Improve.java`) to iteratively untangle and shorten the tour, with best-improvement, first-improvement, and random move-selection variants
- **Random restarts and chained local search** to escape local optima, a practical technique for improving on a single 2-opt hill-climb

This is a good demonstration of moving from "the tree/graph search of Chapter 3" to *local search and optimization* — since with a real TSP instance there are far too many possible tours to search exhaustively.

## 4. 3D Tic-Tac-Toe with Alpha-Beta Pruning
**Folder:** `3DTicTacToe/`

A configurable N×N×N tic-tac-toe engine (`Board.java`, `Coordinate.java`, `Plane.java`, `Line.java`) played against a computer opponent using **minimax search with alpha-beta pruning** (`AdversarialSearch.java`), including:
- Configurable search depth (plies)
- Move ordering to improve pruning efficiency
- Node-count statistics, so pruning effectiveness can be measured directly
- A full CLI (`Parameters.java`) for configuring ply depth, which player moves first, random seed, and verbose/trace output

`Bit.java` is a bitboard-style utility class used internally by the board representation for fast win/line checking — a nice touch for a game whose board can get large in 3D.

## 5. Gradient Descent for Linear/Logistic Regression
**Folder:** `submission/`

A machine learning classifier built entirely from scratch — no ML libraries — split into two programs:
- **`GradientDescent.java`** — reads a CSV dataset, then trains a regression model via gradient descent, supporting three interchangeable activation functions: linear, logistic (sigmoid), and arctan, each with its own hand-derived gradient.
- **`Predict.java`** — takes a trained model's coefficients and reports the classifier's error rate on a dataset, supporting the same activation choices (plus ReLU) via command-line flags.

Together these implement the full train → evaluate loop for a simple classifier, with all the math (activation functions and their derivatives, the gradient update rule) written by hand rather than called from a library.

---

## Bonus: Heuristic Hike-Time Estimator
**Folder:** `Gupta_AIExcer2/` (`BigClimb.java`)

A smaller warm-up exercise: given GPS/elevation data for a hiking trail, estimates hiking time using a grade-adjusted pace heuristic (accounting for climb, distance, and steepness), with sample outputs for several real Bay Area trails (Almaden Quicksilver, Montebello, etc.).

---

## Repo structure

```
├── search-engine-missionaries-tsp/
├── sliding-tile-puzzle/
├── tsp-solver/
│   └── Plot/            (package Plot — map data/plotting classes)
│       └── Maps/
├── 3d-tic-tac-toe/
├── gradient-descent-classifier/
└── hike-time-estimator/
```

Each folder is a clean `.java`-only checkout — no `.class` files, no `__MACOSX`, no
`.idea`/`.iml` project clutter, no duplicate checkpoint versions. Each folder also has its
own README with compile/run instructions. The `tsp-solver/Plot/` subfolder is required as-is
(not just cosmetic) because those files declare `package Plot;` and reference each other
by that package name.
