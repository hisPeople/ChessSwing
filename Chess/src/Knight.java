import java.util.Enumeration;


public class Knight extends ChessPiece {

	public static final String NAME = "Knight";
	private static final int WORTH = 3;

	private static Adjustment[] knightAdjustments = {
		new Adjustment(2,1),
		new Adjustment(2,-1),
		new Adjustment(-2,1),
		new Adjustment(-2,-1),
		new Adjustment(1,2),
		new Adjustment(-1,2),
		new Adjustment(1,-2),
		new Adjustment(-1,-2)
	};
	

	public Knight() {
		super(WORTH);
	}
	
	public String getName() {
		return NAME;
	}
	
	public Enumeration<Location> getLegalMoves(ChessBoard board) {
		MoveEnumeration moves = new MoveEnumeration(board, location);
		moves.addAdjustments(knightAdjustments);
		return moves;
	}
}
