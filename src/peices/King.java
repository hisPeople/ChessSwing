package peices;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import model.Adjustment;
import model.ChessBoard;
import model.Location;
import model.MoveEnumeration;


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
	public boolean inCheckLeft;
	public boolean inCheckRight;
	
	public King() {
		super(WORTH);
	}
		
	
	
	public String getName() {
		return NAME;
	}
		
	public Enumeration<Location> getLegalMoves(ChessBoard board) {
		return new KingMoves(board, this);
	}
	
	public class KingMoves implements Enumeration<Location>{
		 private Iterator<Location> iter;
		 
		 public KingMoves(ChessBoard board, King king){
			  ArrayList<Location> kingMoves = new ArrayList<Location>();
			  MoveEnumeration moves = new MoveEnumeration(board, location);
			  moves.addAdjustments(kingAdjustments);
			  while(moves.hasMoreElements())
				  kingMoves.add(moves.nextElement());
			  ChessPiece rook;
			  Location castleLocation;
			  if(!king.hasMoved() && !inCheckRight)
				{					
					rook = board.getPiece(new Location(location.getRow(), location.getColumn()+3));
					castleLocation = CastleingLeftB_RightW(rook, board);
					if(castleLocation != null)
						kingMoves.add(castleLocation);
				}
			  if(!king.hasMoved() && !inCheckLeft)
				{
					rook = board.getPiece(new Location(location.getRow(), location.getColumn()-4));
					castleLocation = CastleingLeftW_RightB(rook, board);
					if(castleLocation != null)
						kingMoves.add(castleLocation);
				}
				
			  iter = kingMoves.iterator();
		 }	
		
		
		@Override
		public boolean hasMoreElements() {
			return iter.hasNext();
		}

		@Override
		public Location nextElement() {
			return iter.next();
		}
	}
	
	//this is castling left for black and right for white
	public Location CastleingLeftB_RightW(ChessPiece rook, ChessBoard board)
	{
		Location castleLocation = null;
		
		if(rook !=null && !rook.hasMoved &&
		   board.rangeIsEmpty(new Location(this.location.getRow(),5), new Location(this.location.getRow(),6)))
		{
			castleLocation =  new Location(location.getRow(), location.getColumn()+2);		   
		}
		return castleLocation;
	}
	
	//this is castling left for black and right for white
	public Location CastleingLeftW_RightB(ChessPiece rook, ChessBoard board)
	{
		Location castleLocation = null;
		
		if(rook !=null && !rook.hasMoved &&
		   board.rangeIsEmpty(new Location(this.location.getRow(),1), new Location(this.location.getRow(),3)))
		{
			castleLocation =  new Location(location.getRow(), location.getColumn()-2);		   
		}
		return castleLocation;
	}
	
	
	
	
	
}
