package model;
import peices.ChessPiece;


public class BoardState {

	private ChessPiece board[][] = new ChessPiece[8][8];
	private boolean whitesTurn;
	
	public BoardState(Team currentPlayer, ChessBoard board){
		getPeicesFromBoard(board);
		whitesTurn = (currentPlayer.isWhite()) ? true : false;
	}
	
	public BoardState(){};	
	
	private void getPeicesFromBoard(ChessBoard board){
		for(int x =0; x<8; x++){
			for(int y= 0; y<8; y++){
				this.board[x][y] = board.getPiece(new Location(x,y));
			}
		}
	}
	
	public boolean wasWhiteTeamsTurn(){
		return whitesTurn;
	}
	
	//returns a piece  user that is using this will have to get to color from the piece's team or call isWhitePiece.
	public ChessPiece getPiece(Location location)
	{
		return board[location.getRow()][location.getColumn()];
	}
	
	public boolean isWhitePiece(Location location)
	{
		return board[location.getRow()][location.getColumn()].getTeam().isWhite();
	}
	
	public void setPiece(ChessPiece piece)
	{
		Location pieceLocation = piece.getLocation();
		board[pieceLocation.getRow()][pieceLocation.getColumn()] = piece;
	}
	
	public void setEmptyPiece(Location location, ChessPiece piece)
	{	
		board[location.getRow()][location.getColumn()] = piece;
	}
}
