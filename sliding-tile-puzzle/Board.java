import java.util.Arrays;

/*
 Board class represents the state of a sliding puzzle (like 8-puzzle or 15-puzzle).
 Implements Search.State interface to work with various search algorithms.
 The board tracks tile positions, empty space location, and calculates heuristics
 to guide search algorithms toward the solution.
 */
public class Board implements Search.State {
    private final byte[] tiles;  // Array storing tile values (using byte to save memory)
    private final int rows;      // Number of rows in the puzzle
    private final int cols;      // Number of columns in the puzzle
    private final int emptyPos;  // Index of the empty space (value 0)
    private final boolean goalTopLeft;  // True if goal has 0 in top-left, false if bottom-right
    private final HeuristicType heuristicType;  // Which heuristic function to use (Hamming, Manhattan, Euclidean)

    /*
      Move class represents an action that can be taken on the board.
     Each move has a direction (UP, DOWN, LEFT, RIGHT) and a cost (always 1.0 for sliding puzzles).
     */
    public static class Move implements Search.Action {
        private final String direction;  // Direction of the move
        private final double cost;       // Cost of performing this move (always 1.0)

        /*
          Constructor creates a move with the given direction.
          Cost is fixed at 1.0 because all moves in sliding puzzles have equal cost.
         */
        public Move(String direction) {
            this.direction = direction;
            this.cost = 1.0;
        }

       //Returns the cost of this move (always 1.0).
        @Override
        public double cost() {
            return cost;
        }

       //Returns string representation of the move (the direction).
        @Override
        public String toString() {
            return direction;
        }

       //Checks if two moves are equal by comparing their directions.
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Move)) return false;
            Move other = (Move) obj;
            return direction.equals(other.direction);
        }

       //Returns hash code based on the direction string.
        @Override
        public int hashCode() {
            return direction.hashCode();
        }
    }

    // Static move constants - reused throughout the program to avoid creating duplicate objects
    public static final Move UP = new Move("UP");
    public static final Move DOWN = new Move("DOWN");
    public static final Move LEFT = new Move("LEFT");
    public static final Move RIGHT = new Move("RIGHT");

  // Constructor that creates a Board from a byte array.
    public Board(byte[] tiles, int rows, int cols, boolean goalTopLeft, HeuristicType heuristicType) {
        this.tiles = tiles;
        this.rows = rows;
        this.cols = cols;
        this.goalTopLeft = goalTopLeft;
        this.heuristicType = heuristicType;

        // Find the position of the empty tile (value 0)
        // This loop searches through the tiles array to locate the empty space
        int pos = -1;
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] == 0) {  // 0 represents the empty space
                pos = i;
                break;  // Stop once we find it
            }
        }
        this.emptyPos = pos;
    }

    // Constructor that creates a Board from an int array.
    public Board(int[] tiles, int rows, int cols, boolean goalTopLeft, HeuristicType heuristicType) {
        this.rows = rows;
        this.cols = cols;
        this.goalTopLeft = goalTopLeft;
        this.heuristicType = heuristicType;
        this.tiles = new byte[tiles.length];

        // Convert int array to byte array and find empty position
        // This loop does two things: converts data type and locates the empty space
        int pos = -1;
        for (int i = 0; i < tiles.length; i++) {
            this.tiles[i] = (byte) tiles[i];  // Cast to byte to save memory
            if (tiles[i] == 0) {  // Track where the empty space is
                pos = i;
            }
        }
        this.emptyPos = pos;
    }

    /**
     * Checks if this board is in the goal state.
     * The goal state depends on goalTopLeft flag:
     * - If true: tiles should be [0, 1, 2, 3, ...]
     * - If false: tiles should be [1, 2, 3, ..., 0] (0 at the end)
     */
    @Override
    public boolean isGoal() {
        // This loop checks each tile to see if it's in its goal position
        for (int i = 0; i < tiles.length; i++) {
            int expected;
            if (goalTopLeft) {
                // Goal with 0 in top-left: position i should contain value i
                expected = i;
            } else {
                // Goal with 0 in bottom-right: position i should contain value (i+1) mod length
                expected = (i + 1) % tiles.length;
            }
            if (tiles[i] != expected) {
                return false;  // If any tile is out of place, not a goal state
            }
        }
        return true;  // All tiles are in correct positions
    }

    /**
     * Generates the next board state by applying the given action (move).
     * Creates a new Board object with the empty space moved in the specified direction.
     */
    @Override
    public Board next(Search.Action action) {
        Move move = (Move) action;

        // Calculate the new position of the empty space based on the move direction
        int newPos;
        switch (move.direction) {
            case "UP":
                // Moving empty space up means subtracting cols (moving to previous row)
                newPos = emptyPos - cols;
                break;
            case "DOWN":
                // Moving empty space down means adding cols (moving to next row)
                newPos = emptyPos + cols;
                break;
            case "LEFT":
                // Moving empty space left means subtracting 1
                newPos = emptyPos - 1;
                break;
            case "RIGHT":
                // Moving empty space right means adding 1
                newPos = emptyPos + 1;
                break;
            default:
                throw new IllegalArgumentException("Invalid move: " + move.direction);
        }

        // Create a copy of the tiles array and swap the empty space with the target tile
        byte[] newTiles = tiles.clone(); //This creates a shallow copy (so just the references instead of the actual object itself)
        newTiles[emptyPos] = newTiles[newPos];  // Move the tile into the empty space
        newTiles[newPos] = 0;  // The tile's old position becomes empty

        // Return a new Board with the updated tile configuration
        return new Board(newTiles, rows, cols, goalTopLeft, heuristicType);
    }

    /**
     * Returns all valid actions that can be taken from this state.
     * A move is valid if it doesn't move the empty space off the board.
     */
    @Override
    public Iterable<Search.Action> actions() {
        Array<Search.Action> validMoves = new Array<>();

        // Calculate the row and column of the empty space
        int row = emptyPos / cols;
        int col = emptyPos % cols;

        // Check each direction and add if valid
        if (row > 0) {
            // Can move up if not in the first row
            validMoves.add(UP);
        }
        if (row < rows - 1) {
            // Can move down if not in the last row
            validMoves.add(DOWN);
        }
        if (col > 0) {
            // Can move left if not in the first column
            validMoves.add(LEFT);
        }
        if (col < cols - 1) {
            // Can move right if not in the last column
            validMoves.add(RIGHT);
        }

        return validMoves;
    }

    /**
     * Calculates and returns the heuristic value for this board state.
     * The heuristic estimates the distance to the goal state.
     * Lower values mean closer to the goal.
     */
    @Override
    public double heuristic() {
        // This switch selects which heuristic function to use
        switch (heuristicType) {
            case HAMMING:
                // Count of tiles in wrong positions
                return hammingDistance();
            case MANHATTAN:
                // Sum of distances tiles need to move
                return manhattanDistance();
            case EUCLIDEAN:
                // Straight-line distance
                return euclideanDistance();
            default:
                return 0.0;  // No heuristic
        }
    }

    /**
     * Hamming distance: counts how many tiles are in the wrong position.
     * This is an admissible heuristic but not very informed.
     * Example: if 5 tiles are misplaced, returns 5.
     */
    private double hammingDistance() {
        int count = 0;
        // This loop checks each tile to see if it's in the wrong position
        for (int i = 0; i < tiles.length; i++) {
            int tile = tiles[i];
            if (tile == 0) continue;  // Skip the empty space

            // Calculate where this tile should be in the goal state
            int goalPos;
            if (goalTopLeft) {
                goalPos = tile;  // Tile n should be at position n
            } else {
                goalPos = (tile - 1 + tiles.length) % tiles.length;  // Tile n should be at position n-1
            }

            if (i != goalPos) {
                count++;  // This tile is not in its goal position
            }
        }
        return count;
    }

    /**
     * Manhattan distance: sum of horizontal and vertical distances each tile needs to move.
     * This is an admissible and more informed heuristic than Hamming.
     * Calculates grid distance (no diagonal moves allowed).
     */
    private double manhattanDistance() {
        int sum = 0;
        // This loop calculates the Manhattan distance for each misplaced tile
        for (int i = 0; i < tiles.length; i++) {
            int tile = tiles[i];
            if (tile == 0) continue;  // Skip the empty space

            // Calculate where this tile should be in the goal state
            int goalPos;
            if (goalTopLeft) {
                goalPos = tile;
            } else {
                goalPos = (tile - 1 + tiles.length) % tiles.length;
            }

            // Convert linear positions to 2D coordinates
            int currentRow = i / cols;
            int currentCol = i % cols;
            int goalRow = goalPos / cols;
            int goalCol = goalPos % cols;

            // Manhattan distance is the sum of absolute differences in rows and columns
            sum += Math.abs(currentRow - goalRow) + Math.abs(currentCol - goalCol);
        }
        return sum;
    }

    /**
     * Euclidean distance: sum of straight-line distances each tile needs to move.
     * This is admissible but typically less useful than Manhattan for grid-based puzzles.
     * Calculates "as the crow flies" distance.
     */
    private double euclideanDistance() {
        double sum = 0.0;
        // This loop calculates the Euclidean distance for each misplaced tile
        for (int i = 0; i < tiles.length; i++) {
            int tile = tiles[i];
            if (tile == 0) continue;  // Skip the empty space

            // Calculate where this tile should be in the goal state
            int goalPos;
            if (goalTopLeft) {
                goalPos = tile;
            } else {
                goalPos = (tile - 1 + tiles.length) % tiles.length;
            }

            // Convert linear positions to 2D coordinates
            int currentRow = i / cols;
            int currentCol = i % cols;
            int goalRow = goalPos / cols;
            int goalCol = goalPos % cols;

            // Euclidean distance uses Pythagorean theorem: sqrt(dx^2 + dy^2)
            int dr = currentRow - goalRow;
            int dc = currentCol - goalCol;
            sum += Math.sqrt(dr * dr + dc * dc);
        }
        return sum;
    }

    /*
      Checks if two boards are equal.
      Boards are equal if they have the same tile configuration, dimensions, and goal type.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Board)) return false;
        Board other = (Board) obj;
        return Arrays.equals(tiles, other.tiles) &&
                rows == other.rows &&
                cols == other.cols &&
                goalTopLeft == other.goalTopLeft;
    }


    // Generates a hash code for this board.
    @Override
    public int hashCode() {
        int hash = Arrays.hashCode(tiles);
        hash = 31 * hash + rows;      // Mix in rows
        hash = 31 * hash + cols;      // Mix in cols
        hash = 31 * hash + (goalTopLeft ? 1 : 0);  // Mix in goal type
        return hash;
    }

   // Creates a string representation of the board with a grid layout.
    @Override
    public String toString() {
        //So that we can append to the end of the string
        StringBuilder sb = new StringBuilder();

        // Calculate width needed for formatting (based on largest tile number)
        int maxTile = tiles.length - 1;
        int width = String.valueOf(maxTile).length();

        // This loop iterates through all tiles to build the grid representation
        for (int i = 0; i < tiles.length; i++) {
            // Add horizontal separator between rows (but not before first row)
            if (i > 0 && i % cols == 0) {
                sb.append("\n");
                // This inner loop creates the horizontal line between rows
                for (int c = 0; c < cols; c++) {
                    if (c > 0) sb.append("+");  // Intersection between cells
                    for (int w = 0; w < width + 2; w++) {
                        sb.append("-");  // Horizontal line
                    }
                }
                sb.append("\n");
            } else if (i % cols != 0) {
                sb.append(" | ");  // Vertical separator between columns
            }

            // Display the tile value (or blank for empty space)
            if (tiles[i] == 0) {
                // Empty space: display as blank with appropriate spacing
                for (int w = 0; w < width; w++) {
                    sb.append(" ");
                }
            } else {
                // Display tile number with proper formatting
                sb.append(String.format("%" + width + "d", (int) tiles[i]));
            }
        }

        return sb.toString();
    }

    // Getter methods for accessing board properties
    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getTile(int pos) {
        return tiles[pos];
    }

    public int getEmptyPos() {
        return emptyPos;
    }
}