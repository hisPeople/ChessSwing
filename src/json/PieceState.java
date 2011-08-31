package json;

import edu.neumont.learningChess.api.Location;


public class PieceState 
{
	public  Location location;
	public String pieceName;
	public boolean specialMoveInformation = false;
	public boolean isWhite = false;
	
	public PieceState(String pieceName, Location location, boolean specialMoveInformation, boolean isWhite)
	{
		this.location = location;
		this.pieceName = pieceName;
		this.specialMoveInformation = specialMoveInformation;
		this.isWhite = isWhite;
	}
}
