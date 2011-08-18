package other;
import model.IDisplay;
import model.Location;


public class NullDisplay implements IDisplay {

	public class NullPiece implements IDisplay.Piece {
		public NullPiece() {
		}
		public void setPieceLocation(Location location) {
			
		}
		@Override
		public void setVisible(boolean isVisible) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private NullPiece nullPiece = new NullPiece();

	public NullDisplay() {}
	
	@Override
	public void addMoveHandler(IMoveHandler handler) {
	}

	@Override
	public void dispose() {
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
	public void placePiece(Piece piece, Location location) {
	}

	@Override
	public void promptForMove(boolean isWhite) {
	}

	@Override
	public Piece removePiece(Location location) {
		return nullPiece;
	}

	@Override
	public void setVisible(boolean visible) {
	}
}
