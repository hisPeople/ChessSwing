import java.util.Enumeration;


public class King extends ChessPiece {

	public static final String NAME = "King";
	private static final int WORTH = 10000;
	private static Adjustment[] kingAdjustments = {
			new Adjustment(0,1),
			new Adjustment(1,1),
			new Adjustment(1,0),
			new Adjustment(1,-1),
			new Adjustment(0,-1),
			new Adjustment(-1,-1),
			new Adjustment(-1,0),
			new Adjustment(-1,1)
	};
		
	public Location CastelingLeft(){
		int left;
		if(team.isWhite()){
			left = -2
		}else{
			left = 2
		}
		
		return new Location(location.getRow(), location.getColumn()+left, isCastle=true);
	}
	
	public Location CastelingRight(){
		int right;
		if(team.isWhite()){
			right = 2
		}else{
			right = -2
		}
		return new Location(location.getRow(), location.getColumn()+right, isCastle=true);
	}
	
	public King() {
		super(WORTH);
	}
	
	public String getName() {
		return NAME;
	}

	public Enumeration<Location> getLegalMoves(ChessBoard board) {
		
		MoveEnumeration moves = new MoveEnumeration(board, location);
		moves.addAdjustments(kingAdjustments);
		return moves;
	}
}
