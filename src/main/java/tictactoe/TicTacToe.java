package tictactoe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicTacToe {

	private static final int SIZE = 3;
	char[][] board;
	char currentPlayer;
	Move moveFromParent;
	TicTacToe parentState;
	List<TicTacToe> childrenStates;

	public static final char EMPTY = ' ';
	public static final char X = 'X';
	public static final char O = 'O';

	public TicTacToe() {
		parentState = null;
		board = new char[SIZE][SIZE];
		currentPlayer = X;
		for (int i=0; i<SIZE; i++) {
			for (int j=0; j<SIZE; j++) {
				board[i][j] = EMPTY;
			}
		}
	}

	public TicTacToe clone() {
		TicTacToe newStep = new TicTacToe();
		for (int i=0; i<SIZE; i++) {
			for (int j=0; j<SIZE; j++) {
				newStep.board[i][j] = this.board[i][j];
			}
		}
		newStep.currentPlayer = this.currentPlayer;
		newStep.parentState = this;
		return newStep;
	}

	public void generateSuccessors() {
		if (checkVictory()) {
			this.childrenStates = new ArrayList<>();
			return;
		}
		List<TicTacToe> successors = new ArrayList<>(9);
		for (int y=0; y<SIZE; y++) {
			for (int x=0; x<SIZE; x++) {
				if (getPieceAt(x, y) == EMPTY) {
					TicTacToe child = this.clone();
					child.moveFromParent = new Move(x, y);
					child.makeMove(x, y);
					successors.add(child);
				}
			}
		}
		this.childrenStates = successors;
	}

	public int getUtility(char player) {
		generateSuccessors();
		if (childrenStates.isEmpty()) {
			if (checkVictory()) {
				if (player == currentPlayer) {
					return 1;
				} else {
					return -1;
				}
			}
			return 0;
		}
		Map<TicTacToe, Integer> utilityMap = new HashMap<>();
		for (TicTacToe successor: childrenStates) {
			utilityMap.put(successor, successor.getUtility(player));
		}
		if (this.currentPlayer == player) {
			// es mi turno, entonces elegiré la mejor posibilidad
			int maxUtility = Integer.MIN_VALUE;
			for (Map.Entry<TicTacToe, Integer> utilityEntry: utilityMap.entrySet()) {
				if (utilityEntry.getValue() > maxUtility) {
					maxUtility = utilityEntry.getValue();
				}
			}
			return maxUtility;
		}

		// es el turno de mi oponente, entonces elegirá la peor opción para mí
		int minUtility = Integer.MAX_VALUE;
		for (Map.Entry<TicTacToe, Integer> utilityEntry: utilityMap.entrySet()) {
			if (utilityEntry.getValue() < minUtility) {
				minUtility = utilityEntry.getValue();
			}
		}
		return minUtility;
	}

	public static class Move {
		final int x;
		final int y;

		public Move(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public Move getBestMove() {
		generateSuccessors();
		int maxUtility = Integer.MIN_VALUE;
		Move bestMove = null;
		for (TicTacToe successor : childrenStates) {
			int utility = successor.getUtility(this.currentPlayer);
			if (maxUtility < utility) {
				maxUtility = utility;
				bestMove = successor.moveFromParent;
			}
		}
		return bestMove;
	}

	public char getPieceAt(int posX, int posY) {
		return board[posY][posX];
	}

	public void makeMove(int posX, int posY) {
		if (posX < 0 || posX > SIZE || posY < 0 || posY > SIZE)  {
			throw new IllegalArgumentException("Coordinates are outside of tic-tac-toe board!");
		}
		if (board[posY][posX] != EMPTY) {
			throw new IllegalArgumentException("The desired position is already taken!");
		}
		if (checkVictory()) {
			throw new IllegalStateException("Cannot play on finished board!");
		}
		board[posY][posX] = currentPlayer;

		if (checkVictory()) {
			return;
		}

		if (currentPlayer == X) {
			currentPlayer = O;
		} else {
			currentPlayer = X;
		}
	}

	public void printBoard() {
		for (int i=0; i<SIZE; i++) {
			if (i > 0) {
				System.out.println("---+---+---");
			}
			for (int j=0; j<SIZE; j++) {
				if (j > 0) {
					System.out.print("|");
				}
				System.out.print(" " + board[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	public boolean checkVictory() {
		// horizontal
		for (int i = 0; i < SIZE; i++) {
			if (currentPlayer == getPieceAt(0, i)
					&& getPieceAt(0, i) == getPieceAt(1, i)
					&& getPieceAt(1, i) == getPieceAt(2, i)) {
				return true;
			}
		}
		// vertical
		for (int j = 0; j < SIZE; j++) {
			if (currentPlayer == getPieceAt(j, 0)
					&& getPieceAt(j, 0) == getPieceAt(j, 1)
					&& getPieceAt(j, 1) == getPieceAt(j, 2)) {
				return true;
			}
		}
		if (currentPlayer == getPieceAt(0, 0)
				&& getPieceAt(0, 0) == getPieceAt(1, 1)
				&& getPieceAt(1, 1) == getPieceAt(2, 2)) {
			return true;
		}
		if (currentPlayer == getPieceAt(2, 0)
				&& getPieceAt(2, 0) == getPieceAt(1, 1)
				&& getPieceAt(1, 1) == getPieceAt(0, 2)) {
			return true;
		}
		return false;
	}
}
