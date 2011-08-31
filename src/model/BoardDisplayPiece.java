package model;
import java.net.URL;

import javax.swing.*;

import edu.neumont.learningChess.api.Location;



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
