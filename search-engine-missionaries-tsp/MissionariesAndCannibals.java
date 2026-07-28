// Main program for solving Missionaries and Cannibals problem
public class MissionariesAndCannibals {
    private final int numberMissionaries;  // Total number of missionaries
    private final int numberCannibals;     // Total number of cannibals
    private final int boatCapacity;        // Maximum boat capacity
    private final double cannibalPrice;    // Cost for cannibal boat ticket

    // Constructor with all parameters
    public MissionariesAndCannibals(int missionaries, int cannibals, int capacity, double price) {
        assert missionaries > 0 : "Must have at least one missionary";
        assert cannibals > 0 : "Must have at least one cannibal";
        assert capacity > 0 : "Boat must have positive capacity";
        this.numberMissionaries = missionaries;
        this.numberCannibals = cannibals;
        this.boatCapacity = capacity;
        this.cannibalPrice = price;
    }

    // Constructor with default price
    public MissionariesAndCannibals(int missionaries, int cannibals, int capacity) {
        this(missionaries, cannibals, capacity, 1.0);
    }

    // Constructor with default capacity and price
    public MissionariesAndCannibals(int missionaries, int cannibals) {
        this(missionaries, cannibals, 2, 1.0);
    }

    // Default constructor (3 missionaries, 3 cannibals)
    public MissionariesAndCannibals() {
        this(3, 3, 2, 1.0);
    }

    // Action class for boat movements
    public class Action implements Search.Action {
        private final int missionaries;  // Number of missionaries in boat
        private final int cannibals;     // Number of cannibals in boat

        // Constructor for boat action
        public Action(int missionaries, int cannibals) {
            assert missionaries >= 0 : "Cannot have negative missionaries";
            assert cannibals >= 0 : "Cannot have negative cannibals";
            assert missionaries + cannibals > 0 : "Boat cannot be empty";
            assert missionaries + cannibals <= boatCapacity : "Boat capacity exceeded";
            this.missionaries = missionaries;
            this.cannibals = cannibals;
        }

        // Get number of missionaries in boat
        public int missionaries() { return this.missionaries; }

        // Get number of cannibals in boat
        public int cannibals() { return this.cannibals; }

        // Calculate cost of this action
        @Override
        public double cost() {
            return missionaries + cannibals * cannibalPrice;
        }

        // Check equality with another action
        public boolean equals(Action other) {
            return this.missionaries == other.missionaries && this.cannibals == other.cannibals;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Action && this.equals((Action) other);
        }

        @Override
        public int hashCode() {
            return missionaries * 31 + cannibals;
        }

        @Override
        public String toString() {
            return missionaries + "m," + cannibals + "c";
        }
    }

    // State class representing river bank configuration
    public class State implements Search.State {
        private final boolean left;  // Is boat on left side?
        private final int m;         // Missionaries on left bank
        private final int c;         // Cannibals on left bank

        // Constructor for state
        public State(int m, int c, boolean left) {
            assert m >= 0 && m <= numberMissionaries : "Invalid missionary count";
            assert c >= 0 && c <= numberCannibals : "Invalid cannibal count";
            this.left = left;
            this.m = m;
            this.c = c;
        }

        // Get missionaries on left bank
        public int missionaries() { return this.m; }

        // Get cannibals on left bank
        public int cannibals() { return this.c; }

        // Check if boat is on left bank
        public boolean onLeft() { return this.left; }

        // Check if this is goal state (all on right bank)
        @Override
        public boolean isGoal() {
            return this.m == 0 && this.c == 0 && !this.left;
        }

        // Calculate next state after applying action
        public State next(Action action) {
            int newM = this.m;
            int newC = this.c;

            if (this.left) {
                // Moving from left to right
                newM -= action.missionaries();
                newC -= action.cannibals();
            } else {
                // Moving from right to left
                newM += action.missionaries();
                newC += action.cannibals();
            }

            return new State(newM, newC, !this.left);
        }

        // Interface implementation
        @Override
        public Search.State next(Search.Action action) {
            if (action instanceof Action) {
                return next((Action) action);
            }
            return null;
        }

        // Get valid actions from current state
        @Override
        public Iterable<Search.Action> actions() {
            Array<Search.Action> result = new Array<>();

            // Check if current state is valid (no missionaries eaten)
            if (tooManyCannibals(true) || tooManyCannibals(false)) {
                return result;  // Return empty list if state is invalid
            }

            int availableM = this.left ? this.m : numberMissionaries - this.m;
            int availableC = this.left ? this.c : numberCannibals - this.c;

            // Generate all possible boat combinations
            for (int m = 0; m <= availableM; m++) {
                for (int c = 0; c <= availableC; c++) {
                    if (m + c > 0 && m + c <= boatCapacity) {
                        result.add(new Action(m, c));
                    }
                }
            }

            return result;
        }

        // Check if cannibals outnumber missionaries on given bank
        public boolean tooManyCannibals(boolean checkLeft) {
            if (checkLeft) {
                return this.m > 0 && this.c > this.m;
            } else {
                int rightM = numberMissionaries - this.m;
                int rightC = numberCannibals - this.c;
                return rightM > 0 && rightC > rightM;
            }
        }

        // Default heuristic (uninformed)
        @Override
        public double heuristic() {
            return 0.0;
        }

        // Check equality with another state
        public boolean equals(State other) {
            return this.m == other.m && this.c == other.c && this.left == other.left;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof State && this.equals((State) other);
        }

        @Override
        public int hashCode() {
            return m * 31 + c * 7 + (left ? 1 : 0);
        }

        @Override
        public String toString() {
            return "(" + m + "m," + c + "c," + (left ? "L" : "R") + ")";
        }
    }

    // Main method to run Missionaries and Cannibals solver
    public static void main(String[] args) {
        // Default parameters
        int missionaries = 3;
        int cannibals = 3;
        int capacity = 2;
        double price = 1.0;
        Search.Algorithm algorithm = Search.Algorithm.BFS;
        Search.Kind kind = Search.Kind.GRAPH;
        boolean minimizeCost = false;
        boolean trace = false;
        boolean statistics = false;
        boolean quiet = false;
        double wg = 1.0;
        double wh = 1.0;

        // Parse command line arguments
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-missionaries": case "-m":
                    missionaries = Integer.parseInt(args[++i]);
                    break;
                case "-cannibals": case "-c":
                    cannibals = Integer.parseInt(args[++i]);
                    break;
                case "-boat": case "-b":
                    capacity = Integer.parseInt(args[++i]);
                    break;
                case "-price": case "-p":
                    price = Double.parseDouble(args[++i]);
                    break;
                case "-dfs":
                    algorithm = Search.Algorithm.DFS;
                    break;
                case "-bfs":
                    algorithm = Search.Algorithm.BFS;
                    break;
                case "-dls":
                    algorithm = Search.Algorithm.DLS;
                    break;
                case "-id":
                    algorithm = Search.Algorithm.ID;
                    break;
                case "-ucs":
                    algorithm = Search.Algorithm.UCS;
                    break;
                case "-gbfs":
                    algorithm = Search.Algorithm.GBFS;
                    break;
                case "-astar":
                    algorithm = Search.Algorithm.ASTAR;
                    break;
                case "-tree":
                    kind = Search.Kind.TREE;
                    break;
                case "-graph":
                    kind = Search.Kind.GRAPH;
                    break;
                case "-steps":
                    minimizeCost = false;
                    break;
                case "-cost":
                    minimizeCost = true;
                    break;
                case "-trace":
                    trace = true;
                    break;
                case "-statistics": case "-stats":
                    statistics = true;
                    break;
                case "-quiet":
                    quiet = true;
                    break;
                case "-wg":
                    wg = Double.parseDouble(args[++i]);
                    break;
                case "-wh":
                    wh = Double.parseDouble(args[++i]);
                    break;
            }
        }

        // Create problem instance
        final int totalM = missionaries, totalC = cannibals, totalCap = capacity;
        MissionariesAndCannibals problem = new MissionariesAndCannibals(missionaries, cannibals, capacity, price);
        State initial = problem.new State(missionaries, cannibals, true);

        // Override action costs if minimizing steps
        if (!minimizeCost) {
            // Use anonymous class to override cost method
            problem = new MissionariesAndCannibals(missionaries, cannibals, capacity, price) {

                public class Action extends MissionariesAndCannibals.Action {
                    public Action(int missionaries, int cannibals) {
                        super(missionaries, cannibals);
                    }
                    @Override
                    public double cost() {
                        return 1.0;  // Constant cost per step
                    }
                }


                public class State extends MissionariesAndCannibals.State {
                    public State(int m, int c, boolean left) {
                        super(m, c, left);
                    }
                    @Override
                    public Iterable<Search.Action> actions() {
                        Array<Search.Action> result = new Array<>();
                        if (tooManyCannibals(true) || tooManyCannibals(false)) {
                            return result;
                        }
                        int availableM = this.onLeft() ? this.missionaries() : totalM - this.missionaries();
                        int availableC = this.onLeft() ? this.cannibals() : totalC - this.cannibals();
                        for (int m = 0; m <= availableM; m++) {
                            for (int c = 0; c <= availableC; c++) {
                                if (m + c > 0 && m + c <= totalCap) {
                                    result.add(new Action(m, c));
                                }
                            }
                        }
                        return result;
                    }
                }
            };
            initial = problem.new State(missionaries, cannibals, true);
        }

        // Create search algorithm
        Search.SearchAlgorithm search = createSearchAlgorithm(algorithm, kind, trace, wg, wh);

        // Solve problem and measure time
        Timer timer = new Timer();
        Iterable<Search.Action> solution = search.solve(initial);
        double elapsedTime = timer.elapsedTime();

        // Display results
        if (!quiet && solution != null) {
            printSolution(solution, problem, initial);
        }

        if (statistics) {
            printStatistics(search, solution, elapsedTime);
        }

        if (solution == null && !quiet) {
            System.out.println("No solution found");
        }
    }

    // Create appropriate search algorithm instance
    private static Search.SearchAlgorithm createSearchAlgorithm(Search.Algorithm algorithm, Search.Kind kind,
                                                                boolean trace, double wg, double wh) {
        switch (algorithm) {
            case BFS: return new Search.BreadthFirstSearch(kind, trace);
            case DFS: return new Search.DepthFirstSearch(kind, trace);
            case DLS: return new Search.DepthLimitedSearch(kind, trace, 10); // Default limit 10
            case ID: return new Search.IterativeDeepening(kind, trace);
            case UCS: return new Search.UniformCostSearch(kind, trace);
            case GBFS: return new Search.GreedyBestFirstSearch(kind, trace);
            case ASTAR: return new Search.AStarSearch(kind, trace, wg, wh);
            default: return new Search.BreadthFirstSearch(kind, trace);
        }
    }

    // Print solution in required format
    private static void printSolution(Iterable<Search.Action> solution, MissionariesAndCannibals problem, State initial) {
        State current = initial;
        double totalCost = 0;
        int steps = 0;

        for (Search.Action searchAction : solution) {
            if (searchAction instanceof MissionariesAndCannibals.Action) {
                MissionariesAndCannibals.Action action = (MissionariesAndCannibals.Action) searchAction;
                int leftM = current.missionaries();
                int leftC = current.cannibals();
                int rightM = problem.numberMissionaries - leftM;
                int rightC = problem.numberCannibals - leftC;

                String arrow = current.onLeft() ? "==>" : "<==";
                String leftState = leftM + "m," + leftC + "c";
                String rightState = "(" + rightM + "m," + rightC + "c," + (current.onLeft() ? "R" : "L") + ")";

                System.out.printf("%s %s %s %s\n", arrow, leftState, rightState, action);

                current = (State) current.next(action);
                totalCost += action.cost();
                steps++;
            }
        }

        // Print final state
        String arrow = "==>";
        String leftState = current.missionaries() + "m," + current.cannibals() + "c";
        String rightState = "(" + (problem.numberMissionaries - current.missionaries()) + "m," +
                (problem.numberCannibals - current.cannibals()) + "c,R)";
        System.out.printf("%s %s %s\n", arrow, leftState, rightState);

        System.out.printf("Cost = %.0f\n", totalCost);
        System.out.printf("Steps = %d\n", steps);
    }

    // Print search statistics
    private static void printStatistics(Search.SearchAlgorithm search, Iterable<Search.Action> solution, double elapsedTime) {
        if (solution != null) {
            double cost = 0;
            int steps = 0;
            for (Search.Action action : solution) {
                cost += action.cost();
                steps++;
            }
            System.out.printf("Cost = %.0f\n", cost);
            System.out.printf("Steps = %d\n", steps);
        } else {
            System.out.println("Cost = N/A");
            System.out.println("Steps = N/A");
        }
        System.out.printf("States = %d\n", search.getStatesCreated());
        System.out.printf("Queries = %d\n", search.getQueries());
        System.out.printf("Expanded = %d\n", search.getExpanded());
        System.out.printf("Improved = %d\n", search.getImproved());
        System.out.printf("Max size = %d\n", search.getMaxFrontierSize());
        System.out.printf("%.2f milliseconds\n", elapsedTime * 1000);
    }
}