package peices;

import java.util.Enumeration;


import model.ChessBoard;
import model.MoveEnumeration;

import edu.neumont.learningChess.api.Location;

public class Rook extends ChessPiece {

	public static final String NAME = "Rook";
	private static final int WORTH = 5;
	
	public Rook() {
		super(WORTH);
	}

	
	public String getName() {
		return NAME;
	}

	public Enumeration<Location> getLegalMoves(ChessBoard board) {
		MoveEnumeration moves = new MoveEnumeration(board, location);
		moves.addPerps();
		return moves;
	}
}
