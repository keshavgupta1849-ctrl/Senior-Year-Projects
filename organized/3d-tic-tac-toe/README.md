# 3D Tic-Tac-Toe with Alpha-Beta Pruning

A configurable N×N×N tic-tac-toe engine (`Board.java`, `Coordinate.java`, `Plane.java`,
`Line.java`) played against a computer opponent using minimax search with alpha-beta
pruning (`AdversarialSearch.java`), including:
- Configurable search depth (plies)
- Move ordering to improve pruning efficiency
- Node-count statistics to measure pruning effectiveness
- A CLI (`Parameters.java`) for ply depth, which player moves first, random seed, and
  verbose/trace output

`Bit.java` is a bitboard-style utility used internally for fast win/line checking.

## Compiling and running

```bash
javac *.java
java TicTacToe -plies 4 -statistics
```

Run with no arguments to see all CLI options and their defaults.
