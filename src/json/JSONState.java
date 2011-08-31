package json;

import model.ChessBoard;
import model.GameState;
import edu.neumont.learningChess.api.Location;
import model.Team;
import model.TeamLocal;
import model.TeamServer;
import peices.Bishop;
import peices.ChessPiece;
import peices.King;
import peices.Knight;
import peices.Pawn;
import peices.Queen;
import peices.Rook;



public class JSONState 
{

	public static final int CHESS_BOARD_SIZE = 8;
	
	public PieceState[] pieceStates = new PieceState[32];
	public boolean isWhiteTurn;
	public boolean pawnJumped;
	public Location pawnJumpedLocation;
	
	private int currentArrayPosition = 0;
	
	public JSONState(ChessBoard board, boolean isWhiteTurn)
	{
		this.isWhiteTurn = isWhiteTurn;
		this.pawnJumped = board.hasEnPassantMove();
		if(pawnJumped)
		{
			this.pawnJumpedLocation = board.getEnPassantablePawnLocation();
		}
		for (int i = 0; i < CHESS_BOARD_SIZE; i++) 
		{
			for (int j = 0; j < CHESS_BOARD_SIZE; j++) 
			{
				addPieceToPieceStates(board.getPiece( new Location(i,j)), new Location(i,j));
			}
		}
	}
	
	public GameState getGameState()
	{
		GameState generatedGameState = new GameState();
		
		//We made our own constructor for these teams...is this bad?
		Team white = new TeamLocal(Team.Color.LIGHT);
		Team black = new TeamServer(Team.Color.DARK);
		
		
		
		for (int i = 0; i < pieceStates.length; i++) 
		{
			PieceState currentPiece = pieceStates[i];
			if(currentPiece != null)
			{
				ChessPiece piece = null;
				if(currentPiece.isWhite)
				{
					piece = makeChessPieceFromPieceState(currentPiece);
					piece.setTeam(white);
					if(piece instanceof King)
					{
						generatedGameState.setKingMoved(true, piece.hasMoved());
					}
				}
				else
				{
					piece = makeChessPieceFromPieceState(currentPiece);
					piece.setTeam(black);
					if(piece instanceof King)
					{
						generatedGameState.setKingMoved(false, piece.hasMoved());
					}
				}
				generatedGameState.setPiece(piece.getLocation(), piece);
			}
		}
		
		setRooksMoved(generatedGameState, new Location(0, 0), 0);
		setRooksMoved(generatedGameState, new Location(0, 7), 1);
		setRooksMoved(generatedGameState, new Location(7, 0), 2);
		setRooksMoved(generatedGameState, new Location(7, 7), 3);
		
		generatedGameState.setTeamsMove(this.isWhiteTurn);
		
		//Set pawn metadata
		generatedGameState.setPawnMovedTwo(pawnJumped);
		if(pawnJumped)
		{
			generatedGameState.setPawnMovedTwoRow(this.pawnJumpedLocation.getRow());
			generatedGameState.setPawnMovedTwoCol(this.pawnJumpedLocation.getColumn());
		}
		
		return generatedGameState;
	}
	
	private void setRooksMoved(GameState gameState, Location location, int rookNumber)
	{
		ChessPiece cornerPiece = gameState.getPiece(location);
		if(cornerPiece != null)
		{
			if(cornerPiece instanceof Rook)
			{
				if(!cornerPiece.hasMoved())
					gameState.setRookMoved(rookNumber, false);
				else
					gameState.setRookMoved(rookNumber, true);
			}
		}
	}
	
	private ChessPiece makeChessPieceFromPieceState(PieceState pieceState)
	{
		ChessPiece madeChessPiece = null;
		String name = pieceState.pieceName;
		
		if(name.equals("Pawn"))
		{
			madeChessPiece = new Pawn();
			//generatedGameState.setPawnMovedTwo(pieceState.specialMoveInformation);
		}
		else if(name.equals("Rook"))
		{
			madeChessPiece = new Rook();
			madeChessPiece.setHasMoved(pieceState.specialMoveInformation);
		}
		else if(name.equals("Knight"))
		{
			madeChessPiece = new Knight();
		}
		else if(name.equals("Bishop"))
		{
			madeChessPiece = new Bishop();
		}
		else if(name.equals("Queen"))
		{
			madeChessPiece = new Queen();
		}
		else if(name.equals("King"))
		{
			madeChessPiece = new King();
			madeChessPiece.setHasMoved(pieceState.specialMoveInformation);
		}
		else
		{
			System.out.println("JSONState could not make a piece with the piece name " + name);
		}
		madeChessPiece.setLocation(pieceState.location);
		
		return madeChessPiece;
	}
	
	public JSONState(PieceState[] pieces, boolean isWhiteTurn, boolean pawnJumped, Location pawnJumpedLocation)
	{
		this.pieceStates = pieces;
		this.isWhiteTurn = isWhiteTurn;
		this.pawnJumped = pawnJumped;
		this.pawnJumpedLocation = pawnJumpedLocation;
	}
	
	private void addPieceToPieceStates(ChessPiece pieceToBeAddedToArray, Location pieceLocation)
	{
		
		if(pieceToBeAddedToArray != null)
		{
			addPieceToPieceStates(pieceToBeAddedToArray, pieceLocation, pieceToBeAddedToArray.getTeam().isWhite());
		}
	}
	
	private void addPieceToPieceStates(ChessPiece pieceToBeAddedToArray, Location pieceLocation, boolean isWhite )
	{
		String name = pieceToBeAddedToArray.getName();
		
		if(name.equals("Pawn"))
		{
			this.pieceStates[this.currentArrayPosition] = new PieceState(pieceToBeAddedToArray.getName(), pieceLocation, false, isWhite);//Will need to be the boolean saying if the pawn has jumped or not.
		}
		else if(name.equals("Rook"))
		{
			this.pieceStates[this.currentArrayPosition] = new PieceState(pieceToBeAddedToArray.getName(), pieceLocation, pieceToBeAddedToArray.hasMoved(), isWhite);
		}
		else if(name.equals("Bishop"))
		{
			this.pieceStates[this.currentArrayPosition] = new PieceState(pieceToBeAddedToArray.getName(), pieceLocation, false, isWhite);//boolean will not be used, since I know the piece type	
		}
		else if(name.equals("Knight"))
		{
			this.pieceStates[this.currentArrayPosition] = new PieceState(pieceToBeAddedToArray.getName(), pieceLocation, false, isWhite);//boolean will not be used, since I know the piece type
		}
		else if(name.equals("Queen"))
		{
			this.pieceStates[this.currentArrayPosition] = new PieceState(pieceToBeAddedToArray.getName(), pieceLocation, false, isWhite);//boolean will not be used, since I know the piece type
		}
		else if(name.equals("King"))
		{
			this.pieceStates[this.currentArrayPosition] = new PieceState(pieceToBeAddedToArray.getName(), pieceLocation, pieceToBeAddedToArray.hasMoved(), isWhite);
		}
		this.currentArrayPosition++;
	}
}
