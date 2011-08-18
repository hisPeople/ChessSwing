package model;




public interface IDisplay {
	
	
	public interface Piece {
		public void setPieceLocation(Location location);
		public void setVisible(boolean isVisible);
	}
	
	public interface IMoveHandler {
		public boolean handleMove(Move move);
	}
	

	public Piece removePiece(Location location);
	public void placePiece(Piece piece, Location location);
	public void setVisible(boolean visible);
	public void addMoveHandler(IMoveHandler handler);
	public void promptForMove(boolean isWhite);
	public void notifyCheck(boolean isWhite);
	public void notifyCheckmate(boolean isWhite);
	public void notifyStalemate();
	public void dispose();
}
