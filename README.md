# AI and Algorithms Coursework Projects

This is a set of Java projects I wrote for an Advanced Topics in AI and Machine Learning course. They cover uninformed and informed search, adversarial game search, local search optimization, and gradient-based machine learning. I built all of it from scratch, without pulling in any external AI or ML libraries.

One honest note before you dig in. I wrote these on a tight schedule while learning the material for the first time, so they aren't all perfectly optimal. The TSP solver's Canada map, for instance, lands on a close-to-correct tour rather than the exact shortest one. This is real coursework, not polished production code, and I'd rather say so upfront.

Each folder is a clean checkout that holds only the `.java` source. I stripped out the `.class` files, the `__MACOSX` junk, the `.idea` and `.iml` IDE clutter, and the duplicate checkpoint versions. Every folder also carries its own README with compile and run instructions.

## 1. Generic Search Engine, Missionaries and Cannibals, and US Road-Trip Planner

**Folder:** `search-engine-missionaries-tsp/`

The heart of this one is a reusable, generic search engine (`Search.java`). I built it around two small interfaces, `State` and `Action`, so you can solve any problem just by describing its states, its legal actions, and an optional heuristic. The engine runs seven algorithms, and you choose which one at runtime:

- Breadth-first search, depth-first search, depth-limited search, and iterative deepening
- Uniform-cost search (Dijkstra's algorithm)
- Greedy best-first search
- A* search

It handles both tree-like search, which ignores duplicate states, and graph search, which tracks the states it has already reached. That lets you compare the two approaches head to head.

I then solved two different problems with that same engine:

- **Missionaries and Cannibals** (`MissionariesAndCannibals.java`, `State.java`), the classic river-crossing puzzle. I generalized it to work with any number of missionaries and cannibals and any boat capacity.
- **US road-trip planner** (`Trip.java`, `City.java`), which finds routes between US states and capitals. It compares searching by step cost, meaning the fewest hops, against searching by real distance, meaning the shortest actual mileage.

## 2. Sliding-Tile Puzzle Solver (8-puzzle and 15-puzzle)

**Folder:** `sliding-tile-puzzle/`

This is an informed-search solver for the classic sliding-tile puzzle, built on the same generic `Search.java` engine, so it runs the full BFS, DFS, DLS, IDS, UCS, Greedy, and A* lineup at runtime. It works on any grid size, not just 3×3 or 4×4, and it accepts either goal arrangement.

The interesting part lives in `HeuristicType.java` and the heuristic logic inside `Board.java`. You can run A* with any of three heuristics and pick one from a command-line flag:

- **Hamming distance**, which counts the misplaced tiles
- **Manhattan distance**, which sums how far each tile sits from its goal, horizontally and vertically
- **Euclidean distance**, which measures the straight-line distance of each tile from its goal

That setup makes it easy to watch, empirically, how a sharper heuristic cuts down the number of nodes A* expands to crack the same puzzle. It turns heuristic dominance and admissibility into something you can actually see happen.

## 3. Traveling Salesperson Problem (TSP) Solver with Map Visualization

**Folder:** `tsp-solver/`

Here I paired a from-scratch TSP solver (`Plot/Solver.java`) with a Swing map plotter (`Plot/Plot.java`) that draws the resulting tour right on top of real geographic maps: US states, Canada, Europe, California, the Bay Area, Central and South America, and the Caribbean.

Since TSP is NP-hard, I went with local search and optimization instead of an exhaustive search:

- **Construction heuristics** build a starting tour: random shuffle, nearest-neighbor, and cheapest, farthest, and random insertion
- **2-opt local search** (`Plot/Improve.java`) keeps untangling and shortening the tour, with best-improvement, first-improvement, and random move-selection variants
- **Random restarts and chained local search** climb back out of local optima

It makes a nice step up from the tree and graph search of the earlier projects into genuine local search, since a real TSP instance has far too many tours to check one by one.

Keep the `Plot/` subfolder exactly as it is. It is not cosmetic. Those files declare `package Plot;` and reference each other by that package name, and `Plot/Maps/` holds the map data they read.

## 4. 3D Tic-Tac-Toe with Alpha-Beta Pruning

**Folder:** `3d-tic-tac-toe/`

This is a configurable N×N×N tic-tac-toe engine (`Board.java`, `Coordinate.java`, `Plane.java`, `Line.java`) that plays against a computer opponent using minimax search with alpha-beta pruning (`AdversarialSearch.java`). It includes:

- Configurable search depth in plies
- Move ordering, which makes the pruning cut more aggressively
- Node-count statistics, so you can measure how much the pruning actually saves
- A full CLI (`Parameters.java`) for ply depth, which player goes first, the random seed, and verbose trace output

`Bit.java` is a bitboard-style utility I use internally for fast win and line checks, which earns its keep once a 3D board gets large.

## 5. Gradient Descent for Linear and Logistic Regression

**Folder:** `gradient-descent-classifier/`

A machine learning classifier I wrote entirely from scratch, with no ML libraries, split across two programs:

- **`GradientDescent.java`** reads a CSV dataset and trains a regression model with gradient descent. It supports three interchangeable activation functions, linear, logistic sigmoid, and arctan, and I derived the gradient for each one by hand.
- **`Predict.java`** takes a trained model's coefficients and reports the classifier's error rate on a dataset. It supports the same activations plus ReLU, all selectable with command-line flags.

Together they cover the whole train-then-evaluate loop, and I wrote all the math by hand: the activation functions, their derivatives, and the gradient update rule.

## Bonus: Heuristic Hike-Time Estimator

**Folder:** `hike-time-estimator/`

A smaller warm-up (`BigClimb.java`). Give it GPS and elevation data for a trail and it estimates how long the hike takes, using a grade-adjusted pace heuristic that accounts for climb, distance, and steepness. It ships with sample output for a few real Bay Area trails, including Almaden Quicksilver and Montebello.

## Repo structure

```
├── search-engine-missionaries-tsp/
├── sliding-tile-puzzle/
├── tsp-solver/
│   └── Plot/            (package Plot, holds the map data and plotting classes)
│       └── Maps/
├── 3d-tic-tac-toe/
├── gradient-descent-classifier/
└── hike-time-estimator/
```
