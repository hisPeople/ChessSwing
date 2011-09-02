package edu.neumont.learningChess.swingClient;


import edu.neumont.learningChess.swingClient.controller.MoveHandler;
import edu.neumont.learningChess.swingClient.view.BoardDisplay;

public class Main {

	public static void main(String[] args) {
		BoardDisplay board = new BoardDisplay();
		MoveHandler handler = new MoveHandler(board);
		board.addMoveHandler(handler);
	}
}