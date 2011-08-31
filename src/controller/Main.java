package controller;

import model.BoardDisplay;

public class Main {

	public static void main(String[] args) {
		BoardDisplay board = new BoardDisplay();
		MoveHandler handler = new MoveHandler(board);
		board.addMoveHandler(handler);
	}

}