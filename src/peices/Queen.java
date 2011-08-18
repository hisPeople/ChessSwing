package peices;

import java.util.Enumeration;


import model.ChessBoard;
import model.Location;
import model.MoveEnumeration;



public class Queen extends ChessPiece {

	public static final String NAME = "Queen";
	private static final int WORTH = 9;
	
	public Queen() {
		super(WORTH);
	}

	public String getName() {
		return NAME;
	}
	
	public Enumeration<Location> getLegalMoves(ChessBoard board) {
		MoveEnumeration moves = new MoveEnumeration(board, location);
		moves.addDiags();
		moves.addPerps();
		return moves;
	}
	
}
