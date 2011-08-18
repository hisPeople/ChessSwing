import java.net.URL;

import javax.swing.*;


@SuppressWarnings("serial")
public class BoardDisplayPiece extends JLabel implements IDisplay.Piece {

	private Location location;
	
	public BoardDisplayPiece(URL imageURL) {
		super(new ImageIcon(imageURL));
	}
	
	public void setPieceLocation(Location location) {
		this.location = location;
	}
	
	public Location getPieceLocation() {
		return location;
	}
}
