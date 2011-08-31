package model;

import edu.neumont.learningChess.api.Location;

public interface ChessCommandHandler {

	public void handlePlacement(String pieceId, boolean isWhite, Location location);
	public void handleMovement(Location from, Location to, boolean takesPiece);
}
