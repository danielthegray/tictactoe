package tictactoe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TicTacToe {

	private static final int SIZE = 3;
	char[][] board;
	char currentPlayer;
	Move moveFromParent;
	TicTacToe parentState;
	List<TicTacToe> childrenStates = null;
	Integer utilityOfState = null;

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

	public int getUtilityRecursive(char player) {
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
		Stream<Integer> utilities = childrenStates.stream()//
				.map(successor -> successor.getUtilityRecursive(player));
		if (this.currentPlayer == player) {
			// it's my turn, so pick the best option for me (highest utility)
			return utilities.max(Integer::compareTo).orElseThrow();
		}
		return utilities.min(Integer::compareTo).orElseThrow();
	}

	public static class Move {
		final int x;
		final int y;

		public Move(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public Move getBestMoveRecursive() {
		generateSuccessors();
		int maxUtility = Integer.MIN_VALUE;
		Move bestMove = null;
		for (TicTacToe successor : childrenStates) {
			int utility = successor.getUtilityRecursive(this.currentPlayer);
			if (maxUtility < utility) {
				maxUtility = utility;
				bestMove = successor.moveFromParent;
			}
		}
		return bestMove;
	}

	public Move getBestMove() {
		char rootPlayer = currentPlayer;
		TicTacToe currentState = this;
		Predicate<TicTacToe> childHasNullUtility = child -> child.utilityOfState == null;
		while (currentState != null) {
			if (currentState.childrenStates == null) {
				currentState.generateSuccessors();
			}
			if (currentState.childrenStates.isEmpty()
					// limite de profundidad?
			) {
				if (currentState.checkVictory()) {
					if (currentState.currentPlayer == rootPlayer) {
						currentState.utilityOfState = 1;
					} else {
						currentState.utilityOfState = -1;
					}
				} else {
					currentState.utilityOfState = 0;
				}
				currentState = currentState.parentState;
				continue;
			}
			if (currentState.childrenStates.stream().noneMatch(childHasNullUtility)) {
				Stream<Integer> childUtilities = currentState.childrenStates.stream()//
						.map(child -> child.utilityOfState);
				if (rootPlayer == currentState.currentPlayer) {
					currentState.utilityOfState = childUtilities.max(Integer::compareTo).orElseThrow();
				} else {
					currentState.utilityOfState = childUtilities.min(Integer::compareTo).orElseThrow();
				}
				currentState = currentState.parentState;
				continue;
			}
			currentState = currentState.childrenStates.stream().filter(childHasNullUtility).findFirst().orElseThrow();
		}
		return childrenStates.stream().filter(child -> child.utilityOfState.equals(utilityOfState)).findFirst().orElseThrow().moveFromParent;
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
