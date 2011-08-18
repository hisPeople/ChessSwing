package controller;

import model.BoardDisplay;
import model.GameState;
import model.Location;
import model.Team;
import peices.Bishop;
import peices.ChessPiece;
import peices.King;
import peices.Knight;
import peices.Pawn;
import peices.Queen;
import peices.Rook;

public class Main {

	public static void main(String[] args) {
		BoardDisplay board = new BoardDisplay();

		GameController game = new GameController(board);
		game.play();
		game.close();
	}

}