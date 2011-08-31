package controller;

import edu.neumont.learningChess.api.ChessGame;
import edu.neumont.learningChess.api.MoveDescription;
import model.BoardDisplay;
import model.IDisplay;
import edu.neumont.learningChess.api.Move;
import model.IDisplay.Piece;

public class MoveHandler implements IDisplay.IMoveHandler {
	private ChessGame chessGame = new ChessGame();
	private BoardDisplay boardDisplay;
	private PawnPromotion pawnPromotion;
	
	public MoveHandler(BoardDisplay boardDisplay){
		this.boardDisplay = boardDisplay;
		pawnPromotion = new PawnPromotion();
	}
	
	@Override
	public boolean handleMove(Move move){
		MoveDescription moveDescription = chessGame.getMoveDescription(move, pawnPromotion);
		
		if(moveDescription != null){
			chessGame.makeMove(moveDescription);
			
			Piece piece = boardDisplay.removePiece(move.getFrom());
			boardDisplay.placePiece(piece, move.getTo());
			piece.setPieceLocation(move.getTo());
		}
		
		return moveDescription != null;
	}

}
