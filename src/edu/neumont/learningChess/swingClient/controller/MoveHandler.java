package edu.neumont.learningChess.swingClient.controller;

import java.net.URL;

import edu.neumont.learningChess.api.ChessGame;
import edu.neumont.learningChess.api.ChessGameState;
import edu.neumont.learningChess.api.Location;
import edu.neumont.learningChess.api.MoveDescription;
import edu.neumont.learningChess.api.Move;
import edu.neumont.learningChess.api.PieceDescription;
import edu.neumont.learningChess.api.TeamColor;
import edu.neumont.learningChess.model.LocationEnumeration;
import edu.neumont.learningChess.swingClient.view.BoardDisplay;
import edu.neumont.learningChess.swingClient.view.BoardDisplayPiece;
import edu.neumont.learningChess.swingClient.view.IDisplay;
import edu.neumont.learningChess.swingClient.view.PawnPromotion;
import edu.neumont.learningChess.swingClient.view.IDisplay.Piece;

public class MoveHandler implements IDisplay.IMoveHandler {
	private ChessGame chessGame = new ChessGame();
	private BoardDisplay boardDisplay;
	private PawnPromotion pawnPromotion;
	
	public MoveHandler(BoardDisplay boardDisplay){
		this.boardDisplay = boardDisplay;
		pawnPromotion = new PawnPromotion();
		setupBoard();
	}
	
	private void setupBoard(){
		ChessGameState gameState = chessGame.getGameState();
		for(LocationEnumeration locations = new LocationEnumeration(); locations.hasMoreElements();){
			Location location = locations.nextElement();
			PieceDescription pieceDescription = gameState.getPieceDescription(location);
			
			if(pieceDescription != null){
				placePiece(pieceDescription, location);
			}
		}
	}
	
	public void placePiece(PieceDescription pieceDescription, Location location) {
		IDisplay.Piece displayPiece = new BoardDisplayPiece(getImageURL(pieceDescription));
		boardDisplay.placePiece(displayPiece, location);
		displayPiece.setPieceLocation(location);
	}
	
	private URL getImageURL(PieceDescription pieceDescription) {
		String imageLetter = pieceDescription.getColor() == TeamColor.LIGHT ? "w" : "b";
		String imagePath = "/Images/" + pieceDescription.getPieceType().name().toLowerCase() + imageLetter + ".gif";
		URL imageUrl = getClass().getResource(imagePath);
		return imageUrl;
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
