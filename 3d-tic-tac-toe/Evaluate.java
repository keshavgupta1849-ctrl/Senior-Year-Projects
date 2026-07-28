public class Evaluate {

	private static Player computerPlayer = null;

	public static void setComputerPlayer(Player player) {
		computerPlayer = player;
	}

	public static boolean isOver(Board board) {
		if (hasWon(board, Player.X) || hasWon(board, Player.O)) {
			return true;
		}
		return board.numberEmptySquares() == 0;
	}

	public static String winner(Board board) {
		if (hasWon(board, Player.X)) {
			return "X";
		}
		if (hasWon(board, Player.O)) {
			return "O";
		}
		return "Tie";
	}

	public static int score(Board board) {
		if (hasWon(board, Player.X)) {
			return computerPlayer == Player.X ? 10000 : -10000;
		}
		if (hasWon(board, Player.O)) {
			return computerPlayer == Player.O ? 10000 : -10000;
		}
		return 0;
	}

	public static int evaluate(Board board) {
		if (board.isOver()) {
			return score(board);
		}

		int evaluation = 0;

		// Strategic position control
		evaluation += evaluatePositionControl(board);

		// Line potential evaluation
		evaluation += evaluateLines(board);

		// Intersecting threats (very important!)
		evaluation += evaluateIntersectingTwoInARows(board);

		// Blocking opponent's winning threats
		evaluation += evaluateThreats(board);

		// Center control bonus
		evaluation += evaluateCenterControl(board);

		return evaluation;
	}

	private static boolean hasWon(Board board, Player player) {
		long playerPositions = board.get(player);
		for (Line line : Line.lines) {
			long linePositions = line.positions();
			if ((playerPositions & linePositions) == linePositions) {
				return true;
			}
		}
		return false;
	}

	private static int evaluatePositionControl(Board board) {
		int score = 0;

		long computerPositions = board.get(computerPlayer);
		long opponentPositions = board.get(computerPlayer.other());

		for (int position = 0; position < Coordinate.NCubed; position++) {
			boolean isComputer = Bit.isSet(computerPositions, position);
			boolean isOpponent = Bit.isSet(opponentPositions, position);

			if (isComputer) {
				score += getPositionValue(position);
			} else if (isOpponent) {
				score -= getPositionValue(position);
			}
		}

		return score;
	}

	private static int getPositionValue(int position) {
		int x = Coordinate.getX(position);
		int y = Coordinate.getY(position);
		int z = Coordinate.getZ(position);

		// Corner positions are very valuable (part of 7 lines each)
		boolean isCorner = (x == 0 || x == 3) && (y == 0 || y == 3) && (z == 0 || z == 3);
		if (isCorner) {
			return 4;
		}

		// Inner middle positions (part of many lines)
		boolean isInnerMiddle = (x == 1 || x == 2) && (y == 1 || y == 2) && (z == 1 || z == 2);
		if (isInnerMiddle) {
			return 3;
		}

		// Edge positions
		return 2;
	}

	private static int evaluateLines(Board board) {
		int score = 0;

		long computerPositions = board.get(computerPlayer);
		long opponentPositions = board.get(computerPlayer.other());

		for (Line line : Line.lines) {
			long linePositions = line.positions();

			int computerCount = Bit.countOnes(computerPositions & linePositions);
			int opponentCount = Bit.countOnes(opponentPositions & linePositions);

			// Computer's lines
			if (opponentCount == 0 && computerCount > 0) {
				if (computerCount == 4) {
					score += 100000; // Win
				} else if (computerCount == 3) {
					score += 1000; // Winning threat
				} else if (computerCount == 2) {
					score += 50; // Good position
				} else if (computerCount == 1) {
					score += 5; // Potential
				}
			}

			// Opponent's lines (defend more aggressively)
			if (computerCount == 0 && opponentCount > 0) {
				if (opponentCount == 4) {
					score -= 100000; // Loss
				} else if (opponentCount == 3) {
					score -= 1500; // Block critical threat (higher priority)
				} else if (opponentCount == 2) {
					score -= 70; // Block developing threat
				} else if (opponentCount == 1) {
					score -= 5; // Monitor
				}
			}
		}

		return score;
	}

	private static int evaluateThreats(Board board) {
		int score = 0;

		long computerPositions = board.get(computerPlayer);
		long opponentPositions = board.get(computerPlayer.other());
		long emptyPositions = board.emptySquares();

		// Count immediate winning moves for computer
		int computerWinningMoves = 0;
		int opponentWinningMoves = 0;

		for (Line line : Line.lines) {
			long linePositions = line.positions();

			int computerCount = Bit.countOnes(computerPositions & linePositions);
			int opponentCount = Bit.countOnes(opponentPositions & linePositions);
			int emptyCount = Bit.countOnes(emptyPositions & linePositions);

			// Computer has 3 in a row with one empty
			if (computerCount == 3 && opponentCount == 0 && emptyCount == 1) {
				computerWinningMoves++;
			}

			// Opponent has 3 in a row with one empty
			if (opponentCount == 3 && computerCount == 0 && emptyCount == 1) {
				opponentWinningMoves++;
			}
		}

		// Multiple winning moves is extremely valuable
		if (computerWinningMoves > 1) {
			score += 5000;
		} else if (computerWinningMoves == 1) {
			score += 2000;
		}

		// Must block opponent's winning threats
		if (opponentWinningMoves > 0) {
			score -= 3000 * opponentWinningMoves;
		}

		return score;
	}

	private static int evaluateIntersectingTwoInARows(Board board) {
		int score = 0;

		long computerPositions = board.get(computerPlayer);
		long opponentPositions = board.get(computerPlayer.other());

		score += countIntersectingTwoInARows(computerPositions, opponentPositions) * 200;
		score -= countIntersectingTwoInARows(opponentPositions, computerPositions) * 250;

		return score;
	}

	private static int countIntersectingTwoInARows(long playerPositions, long opponentPositions) {
		int count = 0;

		for (int i = 0; i < Line.lines.length; i++) {
			Line line1 = Line.lines[i];
			long line1Positions = line1.positions();

			int playerCount1 = Bit.countOnes(playerPositions & line1Positions);
			int opponentCount1 = Bit.countOnes(opponentPositions & line1Positions);

			if (playerCount1 == 2 && opponentCount1 == 0) {
				for (int j = i + 1; j < Line.lines.length; j++) {
					Line line2 = Line.lines[j];
					long line2Positions = line2.positions();

					int playerCount2 = Bit.countOnes(playerPositions & line2Positions);
					int opponentCount2 = Bit.countOnes(opponentPositions & line2Positions);

					if (playerCount2 == 2 && opponentCount2 == 0) {
						long intersection = line1Positions & line2Positions;
						if (intersection != 0) {
							long emptyIntersection = intersection & ~playerPositions & ~opponentPositions;
							if (emptyIntersection != 0) {
								count++;
							}
						}
					}
				}
			}
		}

		return count;
	}

	private static int evaluateCenterControl(Board board) {
		int score = 0;

		long computerPositions = board.get(computerPlayer);
		long opponentPositions = board.get(computerPlayer.other());

		// The 8 center positions are especially valuable
		int[] centerPositions = {21, 22, 25, 26, 37, 38, 41, 42};

		for (int pos : centerPositions) {
			if (Bit.isSet(computerPositions, pos)) {
				score += 8;
			} else if (Bit.isSet(opponentPositions, pos)) {
				score -= 8;
			}
		}

		return score;
	}
}