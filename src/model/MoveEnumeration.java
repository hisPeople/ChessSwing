package model;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;


import peices.ChessPiece;


public class MoveEnumeration implements Enumeration<Location> {

	private ChessBoard board;
	private Location from;
	private boolean pieceIsWhite;
	private ArrayList<Location> locations = new ArrayList<Location>();
	private Iterator<Location> iter = null;
	
	public MoveEnumeration(ChessBoard board, Location from) {
		this.board = board;
		this.from = from;
		ChessPiece piece = board.getPiece(from);
		pieceIsWhite = piece.getTeam().isWhite();
		
	}
	
	public void addAdjustments(Adjustment[] adjustments) {
		for (int i = 0; i < adjustments.length; i++) {
			Location newLoc = adjustments[i].getLocation(from);
			if (ChessBoard.isInBounds(newLoc)) {
				if (board.hasPiece(newLoc)) {
					if (board.getPiece(newLoc).getTeam().isWhite() != pieceIsWhite) {
						locations.add(newLoc);
					}
				} else {
					locations.add(newLoc);
				}
			}
		}
	}
	
	public void addDiags() {
		
		int startRow = from.getRow();
		int startColumn = from.getColumn();
		
		boolean blocked = false;
		int row = startRow + 1;
		for (int col = startColumn+1; !blocked && col < Math.min(ChessBoard.N_ROWS, ChessBoard.N_COLS); col++) {
			blocked = isValidAddLocation(row, col, pieceIsWhite);
			row++;
		}
		blocked = false;
		row = startRow - 1;
		for (int col = startColumn+1; !blocked && col < Math.min(ChessBoard.N_ROWS, ChessBoard.N_COLS); col++) {
			blocked = isValidAddLocation(row, col, pieceIsWhite);
			row--;
		}
		blocked = false;
		row = startRow + 1;
		for (int col = startColumn-1; !blocked && col > -Math.min(ChessBoard.N_ROWS, ChessBoard.N_COLS); col--) {
			blocked = isValidAddLocation(row, col, pieceIsWhite);
			row++;
		}
		blocked = false;
		row = startRow - 1;
		for (int col = startColumn-1; !blocked && col > -Math.min(ChessBoard.N_ROWS, ChessBoard.N_COLS); col--) {
			blocked = isValidAddLocation(row, col, pieceIsWhite);
			row--;
		}
	}

	private boolean isValidAddLocation(int row, int col, boolean pieceIsWhite) {
		boolean blocked = false;
		Location newLoc = new Location(row, col);
		if (ChessBoard.isInBounds(newLoc)) {
			if (board.hasPiece(newLoc)) {
				blocked = true;
				if (board.getPiece(newLoc).getTeam().isWhite() != pieceIsWhite) {
					locations.add(newLoc);
				}
			} else {
				locations.add(newLoc);
			}
		} else {
			blocked = true;
		}
		return blocked;
	}
	
	public void addPerps() {
		int startRow = from.getRow();
		int startColumn = from.getColumn();
		
		boolean blocked = false;
		for (int row = startRow+1; !blocked && (row < ChessBoard.N_ROWS); row++) {
			blocked = isValidAddLocation(row, startColumn, pieceIsWhite);
		}
		blocked = false;
		for (int row = startRow-1; !blocked && (row >= 0); row--) {
			blocked = isValidAddLocation(row, startColumn, pieceIsWhite);
		}
		blocked = false;
		for (int col = startColumn+1; !blocked && (col < ChessBoard.N_COLS); col++) {
			blocked = isValidAddLocation(startRow, col, pieceIsWhite);
		}
		blocked = false;
		for (int col = startColumn-1; !blocked && (col >= 0); col--) {
			blocked = isValidAddLocation(startRow, col, pieceIsWhite);
		}
	}
	
	@Override
	public boolean hasMoreElements() {
		if (iter == null) {
			iter = locations.iterator();
		}
		return iter.hasNext();
	}

	@Override
	public Location nextElement() {
		return iter.next();
	}
}
