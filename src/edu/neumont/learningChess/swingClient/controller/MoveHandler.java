package edu.neumont.learningChess.swingClient.controller;

import java.net.URL;

import edu.neumont.learningChess.api.ChessGame;
import edu.neumont.learningChess.api.ChessGameState;
import edu.neumont.learningChess.api.Location;
import edu.neumont.learningChess.api.Move;
import edu.neumont.learningChess.api.MoveDescription;
import edu.neumont.learningChess.api.PieceDescription;
import edu.neumont.learningChess.api.PieceType;
import edu.neumont.learningChess.api.TeamColor;
import edu.neumont.learningChess.model.LocationIterator;
import edu.neumont.learningChess.swingClient.view.BoardDisplay;
import edu.neumont.learningChess.swingClient.view.BoardDisplayPiece;
import edu.neumont.learningChess.swingClient.view.IDisplay;
import edu.neumont.learningChess.swingClient.view.IDisplay.Piece;
import edu.neumont.learningChess.swingClient.view.PawnPromotion;

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
		for(LocationIterator locations = new LocationIterator(); locations.hasNext();){
			Location location = locations.next();
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
		MoveDescription moveDescription = chessGame.getMoveDescription(move, pawnPromotion, true);
		
		if(moveDescription != null){
			chessGame.makeMove(moveDescription);
			
			Location origin = move.getFrom();
			Location destination = move.getTo();
			Location enPassantLocation = moveDescription
					.getEnPassantCapturedPawnLocation();
			Location rookLocation = moveDescription.getCastlingRookPosition();
			PieceType pawnPromotionType = moveDescription
					.getPawnPromotionType();

			if (enPassantLocation != null) {
				boardDisplay.removePiece(enPassantLocation);
				movePiece(move);
			} else if (rookLocation != null) {
				Piece rook = boardDisplay.removePiece(rookLocation);
				Location rookCastlingDestination = getRookCastlingDestination(move);
				boardDisplay.placePiece(rook, rookCastlingDestination);
				rook.setPieceLocation(rookCastlingDestination);
				movePiece(move);
			} else if (pawnPromotionType != null) {
				boardDisplay.removePiece(origin);
				TeamColor color = destination.getRow() == 7 ? TeamColor.LIGHT : TeamColor.DARK;
				PieceDescription pieceDescription = new PieceDescription(color, false, pawnPromotionType);
				this.placePiece(pieceDescription, destination);
			} else if (moveDescription.getCapturedPiece() != null) {
				boardDisplay.removePiece(destination);
				movePiece(move);
			} else {
				movePiece(move);
			}
		}
		
		return moveDescription != null;
	}

	private void movePiece(Move move) {
		Piece piece = boardDisplay.removePiece(move.getFrom());
		boardDisplay.placePiece(piece, move.getTo());
		piece.setPieceLocation(move.getTo());
	}
	
	private Location getRookCastlingDestination(Move move) {
		Location kingStart = move.getFrom();
		Location kingEnd = move.getTo();
		return new Location(kingStart.getRow(), (kingStart.getColumn() + kingEnd.getColumn())/2);
	}
}
