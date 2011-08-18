import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;


public class Team {

	
	public enum Color {
		LIGHT,
		DARK
	}

	private Color color;
	private King king;
	private ArrayList<ChessPiece> workingPieces = new ArrayList<ChessPiece>();
	
	public Team(Color color) {
		this.color = color;
	}
	
	public void add(ChessPiece piece) {
		if (piece instanceof King) {
			king = (King) piece;
		}
		workingPieces.add(piece);
		piece.setTeam(this);
	}
	
	public void remove(ChessPiece piece) {
		workingPieces.remove(piece);
		piece.setTeam(null);
	}
	
	public boolean isWhite() {
		return color == Color.LIGHT;
	}

	public King getKing() {
		return king;
	}
	
	public boolean canAttack(ChessBoard board, Location target) {
		boolean attacks = false;
		for (Iterator<ChessPiece> i = workingPieces.iterator(); !attacks && i.hasNext();  ) {
			ChessPiece piece = i.next();
			attacks = piece.canAttack(board, target);
		}
		return attacks;
	}
	
	public Iterator<ChessPiece> getPieces() {
		return workingPieces.iterator();
	}
	
	public int getPieceCount() {
		return workingPieces.size();
	}
	
	public Iterator<Move> getMoves(ChessBoard board) {
		ArrayList<Move> moves = new ArrayList<Move>();
		for (Iterator<ChessPiece> i = workingPieces.iterator(); i.hasNext();  ) {
			ChessPiece piece = i.next();
			for (Enumeration<Location> e = piece.getLegalMoves(board); e.hasMoreElements(); ) {
				Location to = e.nextElement();
				Move move = new Move(piece.getLocation(), to);
				moves.add(move);
			}
		}
		return moves.iterator();
	}
}
