package model;


public class NullDisplay implements IDisplay {

	public NullDisplay() {
		
	}
	
	@Override
	public void addMoveHandler(IMoveHandler handler) {
	}

	@Override
	public void notifyCheck(boolean isWhite) {
	}

	@Override
	public void notifyCheckmate(boolean isWhite) {
	}

	@Override
	public void notifyStalemate() {
	}

	@Override
	public void promptForMove(boolean isWhite) {
	}


	@Override
	public void placePiece(Piece piece, Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setVisible(boolean visible) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Piece removePiece(Location location) {
		// TODO Auto-generated method stub
		return null;
	}
}
