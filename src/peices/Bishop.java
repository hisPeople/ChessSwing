package peices;


import java.util.Enumeration;


import model.ChessBoard;
import edu.neumont.learningChess.api.Location;
import model.MoveEnumeration;


public class Bishop extends ChessPiece {

	public static final String NAME = "Bishop";
	private static final int WORTH = 3;
	

	public Bishop() {
		super(WORTH);
	}

	public String getName() {
		return NAME;
	}
	
	public Enumeration<Location> getLegalMoves(ChessBoard board) {
		MoveEnumeration moves = new MoveEnumeration(board, location);
		moves.addDiags();
		return moves;
	}
}
