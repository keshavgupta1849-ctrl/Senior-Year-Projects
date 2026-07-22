import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class AdversarialSearch<State, Action> {

    private int maxDepth;
    private Player computerPlayer;
    private boolean useMoveOrdering;
    private Random random;
    private int nodesExplored;

    public AdversarialSearch(int maxDepth, Player computerPlayer, boolean useMoveOrdering, Random random) {
        this.maxDepth = maxDepth;
        this.computerPlayer = computerPlayer;
        this.useMoveOrdering = useMoveOrdering;
        this.random = random;
        this.nodesExplored = 0;
    }

    public int getNodesExplored() {
        return nodesExplored;
    }

    public void resetNodesExplored() {
        nodesExplored = 0;
    }

    // Alpha-beta pruning with move ordering
    public int maxValueAB(Board board, int depth, int alpha, int beta) {
        nodesExplored++;

        if (board.isOver() || depth >= maxDepth) {
            return board.evaluate();
        }

        int v = Integer.MIN_VALUE;

        // Get moves and optionally order them
        ArrayList<Integer> moves = getMoves(board);
        if (useMoveOrdering && depth < maxDepth - 1) {
            orderMoves(board, moves, true);
        }

        for (Integer position : moves) {
            Board next = board.next(position);
            v = Math.max(v, minValueAB(next, depth + 1, alpha, beta));
            if (v >= beta) {
                return v;
            }
            alpha = Math.max(alpha, v);
        }
        return v;
    }

    public int minValueAB(Board board, int depth, int alpha, int beta) {
        nodesExplored++;

        if (board.isOver() || depth >= maxDepth) {
            return board.evaluate();
        }

        int v = Integer.MAX_VALUE;

        // Get moves and optionally order them
        ArrayList<Integer> moves = getMoves(board);
        if (useMoveOrdering && depth < maxDepth - 1) {
            orderMoves(board, moves, false);
        }

        for (Integer position : moves) {
            Board next = board.next(position);
            v = Math.min(v, maxValueAB(next, depth + 1, alpha, beta));
            if (v <= alpha) {
                return v;
            }
            beta = Math.min(beta, v);
        }
        return v;
    }

    public Integer miniMaxDecisionAB(Board board) {
        resetNodesExplored();

        ArrayList<Integer> allMoves = getMoves(board);
        if (allMoves.isEmpty()) {
            return null;
        }

        // Evaluate all moves at the root level
        ArrayList<MoveScore> moveScores = new ArrayList<>();
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (Integer position : allMoves) {
            Board next = board.next(position);
            int value = minValueAB(next, 1, alpha, beta);
            moveScores.add(new MoveScore(position, value));
            alpha = Math.max(alpha, value);
        }

        // Find the best score
        int bestScore = Integer.MIN_VALUE;
        for (MoveScore ms : moveScores) {
            if (ms.score > bestScore) {
                bestScore = ms.score;
            }
        }

        // Collect all moves with the best score
        ArrayList<Integer> bestMoves = new ArrayList<>();
        for (MoveScore ms : moveScores) {
            if (ms.score == bestScore) {
                bestMoves.add(ms.move);
            }
        }

        // Randomly select among best moves
        if (bestMoves.size() == 1) {
            return bestMoves.get(0);
        } else {
            int index = random.nextInt(bestMoves.size());
            return bestMoves.get(index);
        }
    }

    // Helper method to get all moves as an ArrayList
    private ArrayList<Integer> getMoves(Board board) {
        ArrayList<Integer> moves = new ArrayList<>();
        for (Integer move : board.moves()) {
            moves.add(move);
        }
        return moves;
    }

    // Move ordering heuristic: order moves by their static evaluation
    private void orderMoves(Board board, ArrayList<Integer> moves, boolean maximizing) {
        ArrayList<MoveScore> moveScores = new ArrayList<>();

        for (Integer move : moves) {
            Board next = board.next(move);
            int score = next.evaluate();
            moveScores.add(new MoveScore(move, score));
        }

        // Sort in descending order for maximizing, ascending for minimizing
        if (maximizing) {
            Collections.sort(moveScores, Collections.reverseOrder());
        } else {
            Collections.sort(moveScores);
        }

        // Update the moves list
        moves.clear();
        for (MoveScore ms : moveScores) {
            moves.add(ms.move);
        }
    }

    // Helper class to store move-score pairs
    private static class MoveScore implements Comparable<MoveScore> {
        Integer move;
        int score;

        MoveScore(Integer move, int score) {
            this.move = move;
            this.score = score;
        }

        @Override
        public int compareTo(MoveScore other) {
            return Integer.compare(this.score, other.score);
        }
    }
}