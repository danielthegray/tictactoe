package tictactoe;

import org.junit.Assert;
import org.junit.Test;

public class TicTacToeTest {

	@Test
	public void testMoves() {
		TicTacToe game = new TicTacToe();

		try {
			game.makeMove(-1, 0);
			Assert.fail("Illegal move did not throw exception");
		} catch (IllegalArgumentException ex) {
			Assert.assertEquals("Coordinates are outside of tic-tac-toe board!",
					ex.getMessage());
		}
		try {
			game.makeMove(0, 8);
			Assert.fail("Illegal move did not throw exception");
		} catch (IllegalArgumentException ex) {
			Assert.assertEquals("Coordinates are outside of tic-tac-toe board!",
					ex.getMessage());
		}

		game.makeMove(0, 0);
		Assert.assertEquals(TicTacToe.X, game.getPieceAt(0,0));

		game.makeMove(0, 1);
		Assert.assertEquals(TicTacToe.O, game.getPieceAt(0, 1));

		try {
			game.makeMove(0, 0);
			Assert.fail("Illegal move did not throw exception");
		} catch (IllegalArgumentException ex) {
			Assert.assertEquals("The desired position is already taken!",
					ex.getMessage());
		}
	}

	@Test
	public void checkVictory() {
		TicTacToe game = new TicTacToe();
		game.makeMove(0,0);

		game.makeMove(1,1);

		game.makeMove(1,0);

		game.makeMove(2,1);

		game.makeMove(2,0);

		Assert.assertTrue(game.checkVictory());
		try {
			game.makeMove(2, 2);
		} catch (IllegalStateException ex) {
			Assert.assertEquals("Cannot play on finished board!",
					ex.getMessage());

		}
	}
}
