import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Comparator;

public class Search {

    // Enum for search type - tree or graph
    public static enum Kind {
        TREE,  // Allows revisiting states
        GRAPH  // Prevents revisiting states
    }

    // Enum for all search algorithms
    public static enum Algorithm {
        BFS,   // Breadth First Search
        DFS,   // Depth First Search
        DLS,   // Depth Limited Search
        ID,    // Iterative Deepening
        UCS,   // Uniform Cost Search
        GBFS,  // Greedy Best First Search
        ASTAR  // A* Search
    }

    // Interface for actions that change environment state
    public static interface Action {
        // Returns cost of this action
        public double cost();
    }

    // Interface for environment states
    public static interface State {
        // Checks if this is a goal state
        public boolean isGoal();
        // Returns new state after applying action
        public State next(Action action);
        // Returns valid actions from this state
        public Iterable<Action> actions();
        // Heuristic estimate to goal (default 0 for uninformed search)
        default public double heuristic() { return 0.0; }
    }

    // Node class representing search space nodes
    private static class Node implements Comparable<Node> {
        // Static mapping from states to nodes for graph search
        private static HashMap<State,Node> nodes = new HashMap<>();
        // Count of nodes created
        private static int count = 0;
        // Total nodes created
        private static int total = 0;
        // Count of queries to find nodes
        private static int queries = 0;
        // Count of expanded nodes
        private static int expanded = 0;
        // Count of improved nodes
        private static int improved = 0;
        // Maximum frontier size
        private static int maxFrontierSize = 0;
        // Current frontier size
        private static int currentFrontierSize = 0;

        private State state;     // Environment state for this node
        private Node parent;     // Parent node on path from start
        private Action action;   // Action that led to this state
        private boolean visited; // Visited flag for search
        private boolean frontier;// Is node in frontier
        private int steps;       // Steps from start to this node
        private double cost;     // Cost from start to this node
        private double f;        // Evaluation function value for A* and GBFS

        // Constructor for new node
        private Node(State state) {
            this.state = state;
            this.visited = false;
            this.frontier = false;
            this.steps = 0;
            this.cost = 0;
            this.f = 0;
            count++;
            total++;
        }

        // Find or create node for given state
        public static Node find(State state, boolean unique) {
            queries++; // Track query count
            // Tree search always creates new nodes
            if (!unique) {
                return new Node(state);
            }
            // Graph search reuses existing nodes
            Node result = nodes.get(state);
            if (result == null) {
                result = new Node(state);
                nodes.put(state, result);
            }
            return result;
        }

        // Update path to this node with better path
        public void update(Node parent, Action action) {
            this.parent = parent;
            this.action = action;
            if (parent != null) {
                this.steps = parent.steps + 1;
                this.cost = parent.cost + action.cost();
            }
        }

        // Set evaluation function value
        public void setF(double f) {
            this.f = f;
        }

        // Mark node as visited
        public void markVisited() {
            this.visited = true;
        }

        // Check if node was visited
        public boolean visited() {
            return this.visited;
        }

        // Mark node as in frontier
        public void markFrontier() {
            if (!this.frontier) {
                this.frontier = true;
                currentFrontierSize++;
                if (currentFrontierSize > maxFrontierSize) {
                    maxFrontierSize = currentFrontierSize;
                }
            }
        }

        // Remove node from frontier
        public void removeFrontier() {
            if (this.frontier) {
                this.frontier = false;
                currentFrontierSize--;
            }
        }

        // Get state of this node
        public State state() {
            return this.state;
        }

        // Compare nodes for priority queue (by f value)
        @Override
        public int compareTo(Node other) {
            return Double.compare(this.f, other.f);
        }

        // Build solution path from goal to start
        public Iterable<Action> solution() {
            // First, collect actions in reverse order (goal to start)
            Array<Action> reversePath = new Array<>();
            Node temp = this;

            while (temp.parent != null) {
                reversePath.add(temp.action);
                temp = temp.parent;
            }

            // Now reverse the path to get start-to-goal order
            Array<Action> path = new Array<>();
            for (int i = reversePath.size() - 1; i >= 0; i--) {
                path.add(reversePath.get(i));
            }

            return path;
        }

        // Reset static counters
        public static void reset() {
            nodes.clear();
            count = 0;
            total = 0;
            queries = 0;
            expanded = 0;
            improved = 0;
            maxFrontierSize = 0;
            currentFrontierSize = 0;
        }

        // Get statistics
        public static int getCount() { return count; }
        public static int getTotal() { return total; }
        public static int getQueries() { return queries; }
        public static int getExpanded() { return expanded; }
        public static int getImproved() { return improved; }
        public static int getMaxFrontierSize() { return maxFrontierSize; }

        @Override
        public String toString() {
            return state.toString();
        }
    }

    // Abstract base class for all search algorithms
    public static abstract class SearchAlgorithm {
        protected final boolean unique;  // Use graph search if true
        protected final boolean trace;   // Enable trace output

        public SearchAlgorithm(Kind kind, boolean trace) {
            this.unique = (kind == Kind.GRAPH);
            this.trace = trace;
            Node.reset(); // Reset statistics for new search
        }

        // Find node for state based on search type
        protected Node find(State state) {
            return Node.find(state, this.unique);
        }

        // Abstract search method to implement
        public abstract Node search(State start);

        // Solve problem and return action sequence
        public Iterable<Action> solve(State start) {
            Node goal = search(start);
            return goal == null ? null : goal.solution();
        }

        // Check if node is solution
        protected boolean isSolution(Node node) {
            return node.state.isGoal();
        }

        // Get search statistics
        public int getStatesCreated() { return Node.getTotal(); }
        public int getQueries() { return Node.getQueries(); }
        public int getExpanded() { return Node.getExpanded(); }
        public int getImproved() { return Node.getImproved(); }
        public int getMaxFrontierSize() { return Node.getMaxFrontierSize(); }
    }

    // Breadth First Search implementation
    public static class BreadthFirstSearch extends SearchAlgorithm {
        public BreadthFirstSearch(Kind kind, boolean trace) {
            super(kind, trace);
        }

        public Node search(State start) {
            // Use queue for FIFO frontier
            Queue<Node> queue = new Queue<>();
            Node initial = find(start);
            initial.markVisited();
            initial.markFrontier();
            queue.enqueue(initial);

            // Main BFS loop
            while (!queue.isEmpty()) {
                Node node = queue.dequeue();
                node.removeFrontier();
                Node.expanded++; // Track expanded nodes

                if (trace) System.out.println("Expanding: " + node);

                // Check for goal
                State state = node.state();
                if (state.isGoal()) return node;

                // Expand node
                for (Action action : state.actions()) {
                    Node next = find(state.next(action));

                    if (trace) System.out.printf("  Next(%s) = %s: ", action, next);

                    // Add unvisited nodes to frontier
                    if (next.visited()) {
                        if (trace) System.out.println("visited");
                    } else {
                        next.update(node, action);
                        next.markVisited();
                        next.markFrontier();
                        queue.enqueue(next);
                        if (trace) System.out.println("added");
                    }
                }
            }
            return null;
        }
    }

    // Depth First Search implementation
    public static class DepthFirstSearch extends SearchAlgorithm {
        public DepthFirstSearch(Kind kind, boolean trace) {
            super(kind, trace);
        }

        public Node search(State start) {
            // Use stack for LIFO frontier
            Stack<Node> stack = new Stack<>();
            Node initial = find(start);
            initial.markVisited();
            initial.markFrontier();
            stack.push(initial);

            // Main DFS loop
            while (!stack.isEmpty()) {
                Node node = stack.pop();
                node.removeFrontier();
                Node.expanded++; // Track expanded nodes

                if (trace) System.out.println("Expanding: " + node);

                // Check for goal
                State state = node.state();
                if (state.isGoal()) return node;

                // Expand node
                for (Action action : state.actions()) {
                    Node next = find(state.next(action));

                    if (trace) System.out.printf("  Next(%s) = %s: ", action, next);

                    // Add unvisited nodes to frontier
                    if (next.visited()) {
                        if (trace) System.out.println("visited");
                    } else {
                        next.update(node, action);
                        next.markVisited();
                        next.markFrontier();
                        stack.push(next);
                        if (trace) System.out.println("added");
                    }
                }
            }
            return null;
        }
    }

    // Depth Limited Search implementation
    public static class DepthLimitedSearch extends SearchAlgorithm {
        private final int limit; // Depth limit

        public DepthLimitedSearch(Kind kind, boolean trace, int limit) {
            super(kind, trace);
            this.limit = limit;
        }

        public Node search(State start) {
            // Use stack for DLS
            Stack<Node> stack = new Stack<>();
            Node initial = find(start);
            initial.markVisited();
            initial.markFrontier();
            stack.push(initial);

            // Main DLS loop
            while (!stack.isEmpty()) {
                Node node = stack.pop();
                node.removeFrontier();
                Node.expanded++; // Track expanded nodes

                if (trace) System.out.println("Expanding: " + node + " at depth " + node.steps);

                // Check for goal
                State state = node.state();
                if (state.isGoal()) return node;

                // Only expand if within depth limit
                if (node.steps < limit) {
                    for (Action action : state.actions()) {
                        Node next = find(state.next(action));

                        if (trace) System.out.printf("  Next(%s) = %s: ", action, next);

                        // Add unvisited nodes to frontier
                        if (next.visited()) {
                            if (trace) System.out.println("visited");
                        } else {
                            next.update(node, action);
                            next.markVisited();
                            next.markFrontier();
                            stack.push(next);
                            if (trace) System.out.println("added");
                        }
                    }
                }
            }
            return null;
        }
    }

    // Iterative Deepening implementation
    public static class IterativeDeepening extends SearchAlgorithm {
        public IterativeDeepening(Kind kind, boolean trace) {
            super(kind, trace);
        }

        public Node search(State start) {
            // Try increasing depth limits
            for (int depth = 0; depth < 1000; depth++) {
                if (trace) System.out.println("Trying depth limit: " + depth);

                // Run DLS with current depth
                DepthLimitedSearch dls = new DepthLimitedSearch(unique ? Kind.GRAPH : Kind.TREE, trace, depth);
                Node result = dls.search(start);

                if (result != null) return result;
            }
            return null;
        }
    }

    // Uniform Cost Search implementation
    public static class UniformCostSearch extends SearchAlgorithm {
        public UniformCostSearch(Kind kind, boolean trace) {
            super(kind, trace);
        }

        public Node search(State start) {
            // Use priority queue ordered by path cost
            PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.cost));
            HashMap<State, Node> frontier = new HashMap<>();
            HashMap<State, Node> explored = new HashMap<>();

            Node initial = find(start);
            initial.markFrontier();
            pq.add(initial);
            frontier.put(start, initial);

            // Main UCS loop
            while (!pq.isEmpty()) {
                Node node = pq.poll();
                State state = node.state();
                frontier.remove(state);
                node.removeFrontier();
                Node.expanded++; // Track expanded nodes

                if (trace) System.out.println("Expanding: " + node + " with cost " + node.cost);

                // Check for goal
                if (state.isGoal()) return node;

                explored.put(state, node);

                // Expand node
                for (Action action : state.actions()) {
                    State nextState = state.next(action);
                    double newCost = node.cost + action.cost();

                    if (trace) System.out.printf("  Next(%s) = %s: ", action, nextState);

                    // Skip if already explored with better cost
                    if (explored.containsKey(nextState)) {
                        if (trace) System.out.println("explored");
                        continue;
                    }

                    Node next = frontier.get(nextState);

                    // Add or update frontier node
                    if (next == null) {
                        next = find(nextState);
                        next.update(node, action);
                        next.markFrontier();
                        pq.add(next);
                        frontier.put(nextState, next);
                        if (trace) System.out.println("added with cost " + newCost);
                    } else if (newCost < next.cost) {
                        // Found better path
                        pq.remove(next);
                        next.update(node, action);
                        pq.add(next);
                        Node.improved++; // Track improved nodes
                        if (trace) System.out.println("improved to cost " + newCost);
                    } else {
                        if (trace) System.out.println("worse cost");
                    }
                }
            }
            return null;
        }
    }

    // Greedy Best First Search implementation
    public static class GreedyBestFirstSearch extends SearchAlgorithm {
        public GreedyBestFirstSearch(Kind kind, boolean trace) {
            super(kind, trace);
        }

        public Node search(State start) {
            // Use priority queue ordered by heuristic
            PriorityQueue<Node> pq = new PriorityQueue<>();
            HashMap<State, Node> frontier = new HashMap<>();
            HashMap<State, Node> explored = new HashMap<>();

            Node initial = find(start);
            initial.setF(start.heuristic());
            initial.markFrontier();
            pq.add(initial);
            frontier.put(start, initial);

            // Main GBFS loop
            while (!pq.isEmpty()) {
                Node node = pq.poll();
                State state = node.state();
                frontier.remove(state);
                node.removeFrontier();
                Node.expanded++; // Track expanded nodes

                if (trace) System.out.println("Expanding: " + node + " with h=" + node.f);

                // Check for goal
                if (state.isGoal()) return node;

                explored.put(state, node);

                // Expand node
                for (Action action : state.actions()) {
                    State nextState = state.next(action);

                    if (trace) System.out.printf("  Next(%s) = %s: ", action, nextState);

                    // Skip if already explored
                    if (explored.containsKey(nextState)) {
                        if (trace) System.out.println("explored");
                        continue;
                    }

                    // Skip if already in frontier
                    if (frontier.containsKey(nextState)) {
                        if (trace) System.out.println("in frontier");
                        continue;
                    }

                    // Add to frontier
                    Node next = find(nextState);
                    next.update(node, action);
                    next.setF(nextState.heuristic());
                    next.markFrontier();
                    pq.add(next);
                    frontier.put(nextState, next);
                    if (trace) System.out.println("added with h=" + next.f);
                }
            }
            return null;
        }
    }

    // A* Search implementation
    public static class AStarSearch extends SearchAlgorithm {
        private double wg = 1.0; // Weight for g(n)
        private double wh = 1.0; // Weight for h(n)

        public AStarSearch(Kind kind, boolean trace) {
            super(kind, trace);
        }

        public AStarSearch(Kind kind, boolean trace, double wg, double wh) {
            super(kind, trace);
            this.wg = wg;
            this.wh = wh;
        }

        public Node search(State start) {
            // Use priority queue ordered by f = g + h
            PriorityQueue<Node> pq = new PriorityQueue<>();
            HashMap<State, Node> frontier = new HashMap<>();
            HashMap<State, Node> explored = new HashMap<>();

            Node initial = find(start);
            initial.setF(wg * 0 + wh * start.heuristic());
            initial.markFrontier();
            pq.add(initial);
            frontier.put(start, initial);

            // Main A* loop
            while (!pq.isEmpty()) {
                Node node = pq.poll();
                State state = node.state();
                frontier.remove(state);
                node.removeFrontier();
                Node.expanded++; // Track expanded nodes

                if (trace) System.out.println("Expanding: " + node + " with f=" + node.f);

                // Check for goal
                if (state.isGoal()) return node;

                explored.put(state, node);

                // Expand node
                for (Action action : state.actions()) {
                    State nextState = state.next(action);
                    double newCost = node.cost + action.cost();
                    double f = wg * newCost + wh * nextState.heuristic();

                    if (trace) System.out.printf("  Next(%s) = %s: ", action, nextState);

                    // Skip if already explored
                    if (explored.containsKey(nextState)) {
                        if (trace) System.out.println("explored");
                        continue;
                    }

                    Node next = frontier.get(nextState);

                    // Add or update frontier node
                    if (next == null) {
                        next = find(nextState);
                        next.update(node, action);
                        next.setF(f);
                        next.markFrontier();
                        pq.add(next);
                        frontier.put(nextState, next);
                        if (trace) System.out.println("added with f=" + f);
                    } else if (newCost < next.cost) {
                        // Found better path
                        pq.remove(next);
                        next.update(node, action);
                        next.setF(f);
                        pq.add(next);
                        Node.improved++; // Track improved nodes
                        if (trace) System.out.println("improved to f=" + f);
                    } else {
                        if (trace) System.out.println("worse cost");
                    }
                }
            }
            return null;
        }
    }
}