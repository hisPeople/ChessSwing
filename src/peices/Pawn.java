package peices;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import model.ChessBoard;
import model.Location;


public class Pawn extends ChessPiece {

	public static final String NAME = "Pawn";
	private static final int WORTH = 1;
	
	public Pawn() {
		super(WORTH);
	}
	
	public String getName() {
		return NAME;
	}
	
	public Location nextStep() {
		int step = team.isWhite()? 1: -1;
		return new Location(location.getRow()+step, location.getColumn());
	}

	public Location longStep() {
		int step = team.isWhite()? 2: -2;
		return new Location(location.getRow()+step, location.getColumn());
	}

	public Location strikeLeft() {
		int step;
		int left;
		if (team.isWhite()) {
			step = 1;
			left = -1;
		} else {
			step = -1;
			left = 1;
		}
		return new Location(location.getRow()+step, location.getColumn()+left);
	}

	public Location strikeRight() {
		int step;
		int right;
		if (team.isWhite()) {
			step = 1;
			right = 1;
		} else {
			step = -1;
			right = -1;
		}
		return new Location(location.getRow()+step, location.getColumn()+right);
	}

	public Enumeration<Location> getLegalMoves(ChessBoard board) {
		return new PawnMoves(board, this);
	}
	
	public class PawnMoves implements Enumeration<Location> {

		private Iterator<Location> iter;
		
		public PawnMoves(ChessBoard board, Pawn pawn) {
			
			ArrayList<Location> moves = new ArrayList<Location>();
			boolean pawnIsWhite = pawn.getTeam().isWhite();
			
			if (ChessBoard.isInBounds(pawn.longStep()) &&
			   (!pawn.hasMoved()) &&
			   (!board.hasPiece(pawn.nextStep())) && 
			   (!board.hasPiece(pawn.longStep()))) {
					moves.add(pawn.longStep());
			}
			if (ChessBoard.isInBounds(pawn.nextStep()) && !board.hasPiece(pawn.nextStep())) {
				moves.add(pawn.nextStep());
			}
			if (ChessBoard.isInBounds(pawn.strikeLeft()) && board.hasPiece(pawn.strikeLeft(), !pawnIsWhite)) {
				moves.add(pawn.strikeLeft());
			}
			if (ChessBoard.isInBounds(pawn.strikeRight()) && board.hasPiece(pawn.strikeRight(), !pawnIsWhite)) {
				moves.add(pawn.strikeRight());
			}
			iter = moves.iterator();
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
}
