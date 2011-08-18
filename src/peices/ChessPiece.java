package peices;

import java.util.Enumeration;

import model.ChessBoard;
import model.Location;
import model.Move;
import model.Team;



public abstract class ChessPiece {

	protected Location location = null;
	protected Team team;
	protected boolean hasMoved = false;
	protected int worth;
	
	public ChessPiece(int worth) {
		this.worth = worth;
	}
	
	public void setLocation(Location location) {
		//hasMoved = (this.location != null);
		this.location = location;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public boolean hasMoved() {
		return hasMoved;
	}
	
	public void setHasMoved(boolean hasMoved) {
		this.hasMoved = hasMoved;
	}
	
	public String getName() {
		return null;
	}
	
	public void setTeam(Team team) {
		this.team = team;
	}
	
	public Team getTeam() {
		return team;
	}
	
	public abstract Enumeration<Location> getLegalMoves(ChessBoard board);
	
	public boolean canAttack(ChessBoard board, Location target) {
		boolean attacks = false;
		for (Enumeration<Location> e = getLegalMoves(board); !attacks && e.hasMoreElements(); ) {
			Location location = e.nextElement();
			attacks = target.equals(location);
		}
		return attacks;
	}
	
	public boolean isLegalMove(ChessBoard board, Move move) {
		boolean isValid = false;
		
		for (Enumeration<Location> e = getLegalMoves(board); !isValid && e.hasMoreElements(); ) {
			Location there = e.nextElement();
			isValid = there.equals(move.getTo());
		}
		return isValid;
	}
	
	public int getValue() {
		return worth;
	}
}
