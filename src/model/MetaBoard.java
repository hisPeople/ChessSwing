package model;

public class MetaBoard {

	private boolean[] rookMoved = new boolean[4];
	private boolean[] kingMoved = new boolean[2];
	private boolean isWhiteMove;
	private boolean pawnMovedTwo;
	private int pawnRow;
	private int pawnCol;
	

	public boolean getRookMoved(int rookNum)
	{
		boolean rookHasMoved = false;
		if(rookNum < 0 || rookNum > 3)
		{
			throw new ArrayIndexOutOfBoundsException();
		}
		else 
		{
			// look at first 4 empty 4 bit segments.
			rookHasMoved = rookMoved[rookNum];
		}
		return rookHasMoved;
	}
	
	public void setRookMoved(int rookNum, boolean hasMoved)
	{
		if(rookNum < 0 || rookNum > 3)
		{
			throw new ArrayIndexOutOfBoundsException();
		}
		else 
		{
			rookMoved[rookNum] = hasMoved;
		}
	}
	
	public boolean getKingMoved(boolean isWhite)
	{
		return kingMoved[isWhite?0:1];
	}
	
	//Should these be 1:2 instead.
	public void setKingMoved(boolean isWhite, boolean hasMoved)
	{
		kingMoved[isWhite?0:1] = hasMoved;
	}
	
	public boolean isWhiteTeamsMove()
	{
		return isWhiteMove;
	}
	
	public void setTeamsMove(boolean isWhiteTeam)
	{
		this.isWhiteMove = isWhiteTeam;
	}
	
	public boolean getPawnMovedTwo()
	{
		return pawnMovedTwo;
	}

	public void setPawnMovedTwo(boolean hasMoved)
	{
		pawnMovedTwo = hasMoved;
	}
	
	//look at 9th, 10th, and 11th empty 4 bit segments.
	//within each empty 4 bit segment get last bit.
	public int getPawnMovedTwoRow()
	{ 	
		return pawnRow;
	}
	
	public void setPawnMovedTwoRow(int row)
	{
		if(row < 0 || row > 7)
		{
			throw new ArrayIndexOutOfBoundsException();
		}
		else
		{
			pawnRow = row;		
		}
	}
	
	//look at 12th, 13th, and 14th empty 4 bit segments.
	//within each empty 4 bit segment get last bit.
	public int getPawnMovedTwoCol()
	{
		return pawnCol;
	}
	
	public void setPawnMovedTwoCol(int col)
	{
		if(col < 0 || col > 7)
		{
			throw new ArrayIndexOutOfBoundsException();
		}
		else
		{
			pawnCol = col;		
		}
	}
}

