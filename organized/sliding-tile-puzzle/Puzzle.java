import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Puzzle {


    public static void main(String[] args) {
        try {
            // Initialize default configuration values
            int rows = 4;  // Default to 4x4 puzzle (15-puzzle)
            int cols = 4;
            boolean goalTopLeft = false;  // Default goal has 0 in bottom-right
            Array<Integer> tiles = new Array<>();  // Will hold the tile values
            Search.Algorithm algorithm = Search.Algorithm.ASTAR;  // Default to A* search
            HeuristicType heuristic = HeuristicType.HAMMING;  // Default to Hamming distance
            int limit = 10;  // Default depth limit for DLS
            boolean stats = false;  // Whether to show statistics
            boolean trace = false;  // Whether to trace algorithm execution
            boolean quiet = false;  // Whether to suppress solution output
            boolean verbose = false;  // Whether to show board state after each move

            // Parse command line arguments
            // This loop processes each command-line argument to configure the puzzle solver
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];

                // This switch statement handles each possible command-line flag
                switch (arg) {
                    case "-rows":
                        // Set the number of rows in the puzzle
                        rows = Integer.parseInt(args[++i]);
                        break;
                    case "-cols":
                    case "-columns":
                        // Set the number of columns in the puzzle
                        cols = Integer.parseInt(args[++i]);
                        break;
                    case "-size":
                        // Set both rows and columns to the same value (for square puzzles)
                        rows = cols = Integer.parseInt(args[++i]);
                        break;
                    case "-top":
                        // Goal state has empty space (0) in top-left corner
                        goalTopLeft = true;
                        break;
                    case "-bottom":
                        // Goal state has empty space (0) in bottom-right corner
                        goalTopLeft = false;
                        break;
                    case "-bfs":
                        // Use Breadth-First Search algorithm
                        algorithm = Search.Algorithm.BFS;
                        break;
                    case "-dfs":
                        // Use Depth-First Search algorithm
                        algorithm = Search.Algorithm.DFS;
                        break;
                    case "-dls":
                        // Use Depth-Limited Search algorithm
                        algorithm = Search.Algorithm.DLS;
                        break;
                    case "-id":
                        // Use Iterative Deepening search algorithm
                        algorithm = Search.Algorithm.ID;
                        break;
                    case "-ucs":
                        // Use Uniform Cost Search algorithm
                        algorithm = Search.Algorithm.UCS;
                        break;
                    case "-gbfs":
                        // Use Greedy Best-First Search algorithm
                        algorithm = Search.Algorithm.GBFS;
                        break;
                    case "-astar":
                        // Use A* Search algorithm (optimal and complete)
                        algorithm = Search.Algorithm.ASTAR;
                        break;
                    case "-limit":
                        // Set depth limit for depth-limited search
                        limit = Integer.parseInt(args[++i]);
                        break;
                    case "-hamming":
                    case "-L0":
                        // Use Hamming distance heuristic (counts misplaced tiles)
                        heuristic = HeuristicType.HAMMING;
                        break;
                    case "-manhattan":
                    case "-L1":
                        // Use Manhattan distance heuristic (sum of grid distances)
                        heuristic = HeuristicType.MANHATTAN;
                        break;
                    case "-euclidean":
                    case "-L2":
                        // Use Euclidean distance heuristic (straight-line distance)
                        heuristic = HeuristicType.EUCLIDEAN;
                        break;
                    case "-stats":
                        // Enable statistics output (steps, states, time, etc.)
                        stats = true;
                        break;
                    case "-trace":
                        // Enable algorithm trace output (shows search progress)
                        trace = true;
                        break;
                    case "-quiet":
                        // Suppress solution move output (only show stats if enabled)
                        quiet = true;
                        break;
                    case "-verbose":
                        // Show board state after each move in the solution
                        verbose = true;
                        break;
                    case "-start":
                        // Read initial board configuration from a file
                        String filename = args[++i];
                        tiles = readBoardFromFile(filename);
                        break;
                    case ".":
                        // A dot represents the empty space (0)
                        tiles.add(0);
                        break;
                    default:
                        // Try to parse as a tile number
                        try {
                            int tile = Integer.parseInt(arg);
                            tiles.add(tile);
                        } catch (NumberFormatException e) {
                            System.err.println("Unknown argument: " + arg);
                            System.exit(1);
                        }
                        break;
                }
            }

            // Validate input - ensure board dimensions are positive
            if (rows <= 0 || cols <= 0) {
                System.err.println("Error: Must specify board dimensions");
                System.exit(1);
            }

            // Check that we have the correct number of tiles
            int expectedTiles = rows * cols;
            if (tiles.size() != expectedTiles) {
                System.err.println("Error: Expected " + expectedTiles + " tiles, but got " + tiles.size());
                System.exit(1);
            }

            // Validate tile values - ensure all values are in range and unique
            // This loop checks that each tile value appears exactly once
            boolean[] seen = new boolean[expectedTiles];
            for (int i = 0; i < tiles.size(); i++) {
                int tile = tiles.get(i);
                // Check if tile value is in valid range [0, expectedTiles-1]
                if (tile < 0 || tile >= expectedTiles) {
                    System.err.println("Error: Tile value " + tile + " is out of range");
                    System.exit(1);
                }
                // Check if this tile value has already been seen (duplicate check)
                if (seen[tile]) {
                    System.err.println("Error: Duplicate tile value: " + tile);
                    System.exit(1);
                }
                seen[tile] = true;  // Mark this tile value as seen
            }

            // Convert Array<Integer> to int[] for Board constructor
            // This loop transfers values from the Array object to a primitive array
            int[] tileArray = new int[tiles.size()];
            for (int i = 0; i < tiles.size(); i++) {
                tileArray[i] = tiles.get(i);
            }

            // Create initial board state with specified dimensions, goal type, and heuristic
            Board board = new Board(tileArray, rows, cols, goalTopLeft, heuristic);

            // Create the appropriate search algorithm instance based on user selection
            Search.SearchAlgorithm search = createSearch(algorithm, trace, limit);

            // Start timer to measure algorithm execution time
            Timer timer = new Timer();

            // Run the search algorithm to find a solution
            Iterable<Search.Action> solution = search.solve(board);

            // Get elapsed time from timer
            double elapsedTime = timer.elapsedTime();

            // Check if a solution was found
            if (solution == null) {
                System.out.println("No solution found");
                System.exit(0);
            }

            // Count steps and display solution
            int steps = 0;
            Board current = board;

            if (!quiet) {
                // Display the solution moves (unless in quiet mode)
                if (verbose) {
                    // Show initial board state if verbose mode is on
                    System.out.println(board);
                }

                // This loop iterates through each move in the solution
                for (Search.Action action : solution) {
                    steps++;  // Count this step
                    System.out.println(action);  // Print the move direction
                    current = (Board) current.next(action);  // Apply the move to get next state
                    if (verbose) {
                        // Show board state after this move if verbose mode is on
                        System.out.println(current);
                    }
                }
            } else {
                // In quiet mode, just count the steps without displaying them
                for (Search.Action action : solution) {
                    steps++;
                }
            }

            // Display statistics if requested
            if (stats) {
                if (!quiet) {
                    System.out.println();  // Add blank line before stats
                }
                // Output various statistics about the search
                System.out.println("Steps = " + steps);  // Number of moves in solution
                System.out.println("States = " + search.getStatesCreated());  // Total states generated
                System.out.println("Queries = " + search.getQueries());  // Number of state queries
                System.out.println("Expanded = " + search.getExpanded());  // States expanded
                System.out.println("Max size = " + search.getMaxFrontierSize());  // Max frontier size

                // Calculate and display effective branching factor
                double b = calculateBranchingFactor(search.getStatesCreated(), steps);
                System.out.printf("Branching = %.2f\n", b);

                // Display elapsed time
                System.out.println(Timer.toString(elapsedTime));
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            // Catch error when a flag is missing its required value
            System.err.println("Error: Missing argument value");
            System.exit(1);
        } catch (NumberFormatException e) {
            // Catch error when a number argument can't be parsed
            System.err.println("Error: Invalid number format");
            System.exit(1);
        } catch (Exception e) {
            // Catch any other unexpected errors
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Reads a board configuration from a file.
     * The file should contain space-separated integers representing tile values.
     * Non-integer tokens in the file are ignored.
     */
    private static Array<Integer> readBoardFromFile(String filename) throws FileNotFoundException {
        Array<Integer> tiles = new Array<>();
        Scanner scanner = new Scanner(new File(filename));

        // This loop reads all tokens from the file
        while (scanner.hasNext()) {
            if (scanner.hasNextInt()) {
                // If the token is an integer, add it to the tiles array
                tiles.add(scanner.nextInt());
            } else {
                // Skip non-integer tokens (like dots or text)
                scanner.next();
            }
        }

        scanner.close();
        return tiles;
    }

    //creates appropraite search algorithim
    private static Search.SearchAlgorithm createSearch(Search.Algorithm algorithm, boolean trace, int limit) {
        Search.Kind kind = Search.Kind.GRAPH;  // Use graph search (avoids revisiting states)

        // This switch creates the appropriate search algorithm object
        switch (algorithm) {
            case BFS:
                // Breadth-First Search: explores all states at depth d before depth d+1
                return new Search.BreadthFirstSearch(kind, trace);
            case DFS:
                // Depth-First Search: explores as deep as possible before backtracking
                return new Search.DepthFirstSearch(kind, trace);
            case DLS:
                // Depth-Limited Search: DFS with a maximum depth limit
                return new Search.DepthLimitedSearch(kind, trace, limit);
            case ID:
                // Iterative Deepening: runs DLS with increasing depth limits
                return new Search.IterativeDeepening(kind, trace);
            case UCS:
                // Uniform Cost Search: expands lowest cost path first
                return new Search.UniformCostSearch(kind, trace);
            case GBFS:
                // Greedy Best-First Search: expands state with lowest heuristic value
                return new Search.GreedyBestFirstSearch(kind, trace);
            case ASTAR:
                // A* Search: expands state with lowest f(n) = g(n) + h(n)
                return new Search.AStarSearch(kind, trace);
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }

    // Calculate effective branchingfactor
    private static double calculateBranchingFactor(int states, int depth) {
        if (depth == 0) return 1.0;  // Handle edge case

        // Initial guess - typical average for sliding puzzles is around 2-3
        double b = 2.5;

        // Newton's method iteration: finds root of f(b) = 0
        // This loop iteratively refines the estimate of the branching factor
        for (int i = 0; i < 100; i++) {
            // Evaluate f(b) = (sum of geometric series) - N
            double f = evaluatePolynomial(b, depth, states);
            // Evaluate f'(b) (derivative)
            double fPrime = evaluateDerivative(b, depth);

            // Newton's method update: b_new = b - f(b)/f'(b)
            double newB = b - f / fPrime;

            // Check for convergence (if change is small enough, we're done)
            if (Math.abs(newB - b) < 0.01) {
                return newB;
            }

            b = newB;  // Update estimate for next iteration
        }

        return b;  // Return final estimate after max iterations
    }

    // evaluates Polynominal branching factor
    private static double evaluatePolynomial(double x, int d, int N) {
        // Handle special case where x is very close to 1 (would cause division by zero)
        // When x=1, the series sum is simply d+1
        if (Math.abs(x - 1.0) < 0.0001) {
            return d + 1 - N;
        }
        // Standard geometric series formula: (x^(d+1) - 1)/(x - 1) - N
        return (Math.pow(x, d + 1) - 1) / (x - 1) - N;
    }

    // elevaluates the derivative of the polynomial function
    private static double evaluateDerivative(double x, int d) {
        // Handle special case where x is very close to 1 (would cause division by zero)
        // Using L'Hôpital's rule, the limit as x->1 is d(d+1)/2
        if (Math.abs(x - 1.0) < 0.0001) {
            return d * (d + 1) / 2.0;
        }
        // Calculate numerator: (d+1)(x-1)x^d - (x^(d+1) - 1)
        double numerator = (d + 1) * (x - 1) * Math.pow(x, d) - (Math.pow(x, d + 1) - 1);
        // Calculate denominator: (x-1)^2
        double denominator = (x - 1) * (x - 1);
        // Return the derivative value
        return numerator / denominator;
    }
}