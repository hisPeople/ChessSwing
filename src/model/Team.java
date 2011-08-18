package model;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import peices.ChessPiece;
import peices.King;


public abstract class Team implements IDisplay.IMoveHandler {

	
	public enum Color {
		LIGHT,
		DARK
	}

	protected Color color;
	private King king;
	protected ChessBoard board;
	private ICheckChecker checkChecker;
	
	private ArrayList<ChessPiece> workingPieces = new ArrayList<ChessPiece>();
	protected boolean awaitingMove = false;
	protected Move awaitedMove = null;
	
	public Team(Color color, ChessBoard board, ICheckChecker checkChecker) {
		this.color = color;
		this.board = board;
		this.checkChecker = checkChecker;
	}
	//for testing
	public Team(Color color){
		this.color = color;
	}
	
	//for testing
	public void setBoard(ChessBoard chess){
		this.board = chess;
	}
	
	//for testing 
	public void setCheckChecker(ICheckChecker checker){
		this.checkChecker = checker;
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
	
	public abstract Move getMove(); 

	@Override
	public abstract boolean handleMove(Move move); 

	public boolean isLegalMove(Move move) {
		ChessPiece movingPiece = board.getPiece(move.getFrom());
		Team movingTeam = movingPiece.getTeam();
		return (movingTeam == this) && movingPiece.isLegalMove(board, move) && !causesCheckmate(move);
	}

	// the good methods 
//	public boolean handleMove(Move move)
//	{
//		boolean canHandleMove = true;
//		boolean legal = isLegalMove(move);
//		if(legal)
//		{
//			canHandleMove = true;
//			awaitedMove = move;
//			notify();
//		}else{
//			canHandleMove = false;
//		}
//		return canHandleMove;
//	}

	
	private boolean causesCheckmate(Move move) {
		board.tryMove(move);
		boolean result = checkChecker.isInCheck(this);
		board.undoTriedMove();
		
		return result;
	}
	
}
