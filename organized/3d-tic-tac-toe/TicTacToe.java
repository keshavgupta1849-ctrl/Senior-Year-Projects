import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class TicTacToe {

    private Board board;
    private Player computerPlayer;
    private Player humanPlayer;
    private AdversarialSearch<Board, Integer> search;
    private Parameters params;
    private ArrayList<Board> history;
    private Scanner input;
    private Random random;
    private long totalThinkingTime;
    private int totalMoves;

    public TicTacToe(Parameters params) {
        this.params = params;
        this.history = new ArrayList<Board>();
        this.input = new Scanner(System.in);
        this.random = new Random(params.seed());
        this.totalThinkingTime = 0;
        this.totalMoves = 0;

        if (params.board().isEmpty()) {
            this.board = new Board();
        } else {
            this.board = Board.valueOf(params.board());
        }

        if (params.first()) {
            this.computerPlayer = Player.X;
            this.humanPlayer = Player.O;
        } else {
            this.computerPlayer = Player.O;
            this.humanPlayer = Player.X;
        }

        Evaluate.setComputerPlayer(computerPlayer);
        this.search = new AdversarialSearch<Board, Integer>(
                params.plies(),
                computerPlayer,
                params.ordering(),
                random
        );

        history.add(board);
    }

    public void play() {
        System.out.println("3D Tic-Tac-Toe");
        System.out.println("You are player " + humanPlayer);
        System.out.println("Computer is player " + computerPlayer);
        System.out.println();

        board.print();

        try {
            while (!board.isOver()) {
                if (board.turn() == humanPlayer) {
                    humanMove();
                } else {
                    computerMove();
                }
            }

            gameOver();

        } catch (Game.QuitException e) {
            System.out.println("Game quit by user");
        }
    }

    private void humanMove() throws Game.QuitException {
        System.out.println("Your move");

        try {
            int position = Coordinate.askPosition(input, board);
            board = board.next(position);
            history.add(board);

            if (params.verbose()) {
                System.out.println("You played: " + Coordinate.toString(position));
            }

            board.print();

        } catch (Game.UndoException e) {
            undo();
        }
    }

    private void computerMove() {
        if (params.verbose()) {
            System.out.println("Computer thinking...");
        }

        long startTime = System.currentTimeMillis();

        Integer position = search.miniMaxDecisionAB(board);

        long endTime = System.currentTimeMillis();
        long thinkingTime = endTime - startTime;
        totalThinkingTime += thinkingTime;
        totalMoves++;

        if (position == null) {
            System.out.println("Error: No valid move found!");
            return;
        }

        board = board.next(position);
        history.add(board);

        System.out.println("My move: " + Coordinate.toString(position));

        if (params.trace()) {
            System.out.println("  Time: " + thinkingTime + " ms");
            System.out.println("  Nodes explored: " + search.getNodesExplored());
            System.out.println("  Evaluation: " + board.evaluate());
        }

        board.print();
    }

    private void undo() {
        if (history.size() <= 1) {
            System.out.println("Cannot undo - no moves to undo");
            return;
        }

        history.remove(history.size() - 1);

        if (history.size() > 0 && board.turn() == humanPlayer) {
            history.remove(history.size() - 1);
        }

        if (history.size() > 0) {
            board = history.get(history.size() - 1);
            System.out.println("Undo successful");
            board.print();
        } else {
            board = new Board();
            history.add(board);
            System.out.println("Undo successful - back to start");
            board.print();
        }
    }

    private void gameOver() {
        System.out.println("Game Over!");
        String winner = board.winner();

        if (winner.equals("Tie")) {
            System.out.println("The game is a tie!");
        } else {
            System.out.println("Player " + winner + " wins!");

            Line winningLine = findWinningLine(board, Player.valueOf(winner.charAt(0)));
            if (winningLine != null) {
                System.out.println("Winning line: " + winningLine.name());
            }
        }

        if (params.statistics()) {
            printStatistics();
        }
    }

    private Line findWinningLine(Board board, Player player) {
        long playerPositions = board.get(player);
        for (Line line : Line.lines) {
            long linePositions = line.positions();
            if ((playerPositions & linePositions) == linePositions) {
                return line;
            }
        }
        return null;
    }

    private void printStatistics() {
        System.out.println();
        System.out.println("Game Statistics:");
        System.out.println("  Total moves: " + (history.size() - 1));
        System.out.println("  Computer moves: " + totalMoves);
        if (totalMoves > 0) {
            System.out.println("  Average thinking time: " + (totalThinkingTime / totalMoves) + " ms");
        }
        System.out.println("  Final board evaluation: " + board.score());
        System.out.println("  Search depth: " + params.plies() + " plies");
        System.out.println("  Alpha-beta pruning: " + (params.alphaBeta() ? "enabled" : "disabled"));
        System.out.println("  Move ordering: " + (params.ordering() ? "enabled" : "disabled"));
        System.out.println("  Random seed: " + params.seed());
    }

    public static void main(String[] args) {
        Parameters params = new Parameters(args);

        if (params.errors()) {
            System.err.println("Error in parameters - exiting");
            System.err.println();
            printUsage();
            return;
        }

        if (params.verbose()) {
            System.out.println("Parameters:");
            System.out.println("  Plies: " + params.plies());
            System.out.println("  Computer plays first: " + params.first());
            System.out.println("  Alpha-beta: " + params.alphaBeta());
            System.out.println("  Move ordering: " + params.ordering());
            System.out.println("  Statistics: " + params.statistics());
            System.out.println("  Trace: " + params.trace());
            System.out.println("  Seed: " + params.seed());
            if (!params.board().isEmpty()) {
                System.out.println("  Starting board: " + params.board());
            }
            System.out.println();
        }

        TicTacToe game = new TicTacToe(params);
        game.play();
    }

    private static void printUsage() {
        System.out.println("Usage: java TicTacToe [options] [board-configuration]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -first         Computer plays first (player X) [default]");
        System.out.println("  -second        Computer plays second (player O)");
        System.out.println("  -plies #       Search depth in plies [default: 3]");
        System.out.println("  -seed #        Random number seed for reproducibility");
        System.out.println("  -trace         Enable trace output for debugging");
        System.out.println("  -verbose       Enable verbose output");
        System.out.println("  -statistics    Print game statistics at end");
        System.out.println("  -noordering    Disable move ordering");
        System.out.println();
        System.out.println("Board configuration:");
        System.out.println("  Use X, O, and . for positions");
        System.out.println("  64 characters total (4x4x4 board)");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java TicTacToe -first -plies 4");
        System.out.println("  java TicTacToe -second -plies 3 -seed 12345");
        System.out.println("  java TicTacToe -trace \"X...O...............................................................\"");
    }
}