// Main program for finding paths between US states
public class Trip {

    // Action class for moving between states
    public static class Action implements Search.Action {
        private final State from;  // Starting US State
        private final State to;    // Destination US State
        private final boolean useDistance;  // Use actual distance or step cost

        // Constructor for travel action
        public Action(State from, State to, boolean useDistance) {
            this.from = from;
            this.to = to;
            this.useDistance = useDistance;
        }

        // Get destination state
        public State to() { return this.to; }

        // Get starting state
        public State from() { return this.from; }

        // Calculate cost of travel
        @Override
        public double cost() {
            if (useDistance) {
                return from.capital().distanceTo(to.capital());
            } else {
                return 1.0;  // Step cost
            }
        }

        @Override
        public String toString() {
            return from.code() + " -> " + to.code();
        }
    }

    // State wrapper for US states in search (renamed to avoid conflict)
    public static class TripState implements Search.State {
        private final State state;        // Underlying US state
        private final State goal;         // Goal state for heuristic
        private final boolean useDistance; // Use distance for cost calculation

        // Constructor for search state
        public TripState(State state, State goal, boolean useDistance) {
            this.state = state;
            this.goal = goal;
            this.useDistance = useDistance;
        }

        // Get underlying US state
        public State getState() { return this.state; }

        // Check if this is goal state
        @Override
        public boolean isGoal() {
            return this.state.equals(goal);
        }

        // Calculate next state after action
        @Override
        public Search.State next(Search.Action action) {
            if (action instanceof Action) {
                Action tripAction = (Action) action;
                return new TripState(tripAction.to(), goal, useDistance);
            }
            return null;
        }

        // Get valid travel actions to neighboring states
        @Override
        public Iterable<Search.Action> actions() {
            Array<Search.Action> result = new Array<>();
            for (State neighbor : state.neighbors()) {
                result.add(new Action(state, neighbor, useDistance));
            }
            return result;
        }

        // Heuristic: straight-line distance to goal capital
        @Override
        public double heuristic() {
            return state.capital().distanceTo(goal.capital());
        }

        // Check equality with another state
        public boolean equals(TripState other) {
            return this.state.equals(other.state);
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof TripState && this.equals((TripState) other);
        }

        @Override
        public int hashCode() {
            return state.hashCode();
        }

        @Override
        public String toString() {
            return state.code();
        }
    }

    // Main method to run US trip planner
    public static void main(String[] args) {
        // Default parameters
        String fromState = "CA";
        String toState = "VA";
        Search.Algorithm algorithm = Search.Algorithm.BFS;
        Search.Kind kind = Search.Kind.GRAPH;
        boolean useDistance = false;
        boolean trace = false;
        boolean statistics = false;
        boolean quiet = false;
        double wg = 1.0;
        double wh = 1.0;

        // Parse command line arguments
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-from":
                    fromState = args[++i];
                    break;
                case "-to":
                    toState = args[++i];
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
                    useDistance = false;
                    break;
                case "-cost":
                    useDistance = true;
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

        // Find start and goal states using US State class
        State start = State.find(fromState);
        State goal = State.find(toState);

        if (start == null) {
            System.err.println("Unknown starting state: " + fromState);
            return;
        }
        if (goal == null) {
            System.err.println("Unknown goal state: " + toState);
            return;
        }

        // Create search state using TripState wrapper
        TripState initial = new TripState(start, goal, useDistance);

        // Create search algorithm
        Search.SearchAlgorithm search = createSearchAlgorithm(algorithm, kind, trace, wg, wh);

        // Solve problem and measure time
        Timer timer = new Timer();
        Iterable<Search.Action> solution = search.solve(initial);
        double elapsedTime = timer.elapsedTime();

        // Display results
        if (!quiet && solution != null) {
            printSolution(solution, useDistance);
        }

        if (statistics) {
            printStatistics(search, solution, elapsedTime);
        }

        if (solution == null && !quiet) {
            System.out.println("No path found from " + fromState + " to " + toState);
        }
    }

    // Create appropriate search algorithm instance
    private static Search.SearchAlgorithm createSearchAlgorithm(Search.Algorithm algorithm, Search.Kind kind,
                                                                boolean trace, double wg, double wh) {
        switch (algorithm) {
            case BFS: return new Search.BreadthFirstSearch(kind, trace);
            case DFS: return new Search.DepthFirstSearch(kind, trace);
            case DLS: return new Search.DepthLimitedSearch(kind, trace, 50); // Default limit 50
            case ID: return new Search.IterativeDeepening(kind, trace);
            case UCS: return new Search.UniformCostSearch(kind, trace);
            case GBFS: return new Search.GreedyBestFirstSearch(kind, trace);
            case ASTAR: return new Search.AStarSearch(kind, trace, wg, wh);
            default: return new Search.BreadthFirstSearch(kind, trace);
        }
    }

    // Print solution in required format
    private static void printSolution(Iterable<Search.Action> solution, boolean useDistance) {
        double totalCost = 0;
        int steps = 0;

        for (Search.Action searchAction : solution) {
            if (searchAction instanceof Action) {
                Action action = (Action) searchAction;
                double cost = action.cost();
                System.out.printf("%s (%.0f)\n", action, cost);
                totalCost += cost;
                steps++;
            }
        }

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