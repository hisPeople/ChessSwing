package controller;

//import java.io.BufferedReader;
//import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

//import json.JSONFactory;
//import json.JSONState;

import model.BoardState;
import model.ChessBoard;
import model.ICheckChecker;
import model.IllegalMoveException;
import edu.neumont.learningChess.api.Move;
import model.Team;
import model.TeamLocal;
import model.TeamServer;
//import model.Team.Color;
import edu.neumont.learningChess.model.Bishop;
import edu.neumont.learningChess.model.ChessPiece;
import edu.neumont.learningChess.model.King;
import edu.neumont.learningChess.model.Knight;
import edu.neumont.learningChess.model.Pawn;
import edu.neumont.learningChess.model.Queen;
import edu.neumont.learningChess.model.Rook;
import edu.neumont.learningChess.swingClient.view.BoardDisplayPiece;
import edu.neumont.learningChess.swingClient.view.IDisplay;

import edu.neumont.learningChess.api.Location;

public class GameController implements ChessBoard.IListener, ICheckChecker {

	private IDisplay boardDisplay;

	private Team whiteTeam;
	private Team blackTeam;

	private ChessBoard board;
	private Team currentPlayer;

	private boolean isCheckmate;
	private boolean isStalemate;

	private ArrayList<BoardState> history = new ArrayList<BoardState>();
	private ArrayList<Move> moveHistory = new ArrayList<Move>();

	public enum TeamType {
		LOCAL, SERVER
	}

	public GameController(IDisplay boardDisplay) {

		board = new ChessBoard();
		board.AddListener(this);

		// gameState =getGamestate();
		this.boardDisplay = boardDisplay;

		whiteTeam = buildTeam(Team.Color.LIGHT, TeamType.LOCAL);
		blackTeam = buildTeam(Team.Color.DARK, TeamType.SERVER);

		// Determine the player types here
		currentPlayer = whiteTeam;

		isCheckmate = false;
		isStalemate = false;
		// addToHistory();
	}

	public ChessBoard getBoard() {
		return board;
	}

	public Team getCurrentTeam() {
		return currentPlayer;
	}

	public void setCurrentTeam(Team team) {
		currentPlayer = team;
	}

	public void play() {
		if (!(isCheckmate || isStalemate)) {
			// boolean isOk = currentPlayer.handleMove(move);
			Move move = currentPlayer.getMove();
			boolean isOk = currentPlayer.isLegalMove(move);
			if (isOk) {
				// Castles the king if attempted correctly
				castleHandleLargeSide(move);
				castleHandleSmallSide(move);

				board.makeMove(move);
				checkEnPassant(move);
				canEnPassantNextTurn(move);
				currentPlayer = (currentPlayer == whiteTeam) ? blackTeam
						: whiteTeam;
				isCheckmate = isCheckmate();
				isStalemate = isStalemate();
				checkPawnPromotion(move);

				if (!isCheckmate && isInCheck(currentPlayer)) {
					boardDisplay.notifyCheck(currentPlayer == whiteTeam);
				}
				addToHistory();
				addMoveToHistory(move);
			} else {
				throw new IllegalMoveException();
			}
			if (isCheckmate) {
				boardDisplay.notifyCheckmate(currentPlayer == blackTeam);
			} else if (isStalemate) {
				boardDisplay.notifyStalemate();
			}
		}
		else
			sendHistory();
	}
	
	
	public synchronized void sendHistory() {
		String json = JSONFactory.createJSONFromMoveHistory(moveHistory);
		try {
			URL url = new URL("HTTP","chess.neumont.edu",8081,"/ChessGame/analyzehistory");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(json);
			out.flush();
			 out.close();
			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	private void addMoveToHistory(Move move) {
		moveHistory.add(move);
	}

	// handles the castle if it happens
	private void castleHandleLargeSide(Move move) {
		boolean inCheck = false;
		if (!board.getPiece(move.getFrom()).hasMoved()
				&& (board.getPiece(move.getFrom()) instanceof King)
				&& move.getTo().getColumn() == move.getFrom().getColumn() - 2) {
			int x = 1;
			inCheck = this.isInCheck(currentPlayer);
			while (!inCheck && x < 3) {
				board.tryMove(new Move(move.getFrom(), new Location(move
						.getFrom().getRow(), move.getFrom().getColumn() - x)));
				inCheck = this.isInCheck(currentPlayer);
				board.undoTriedMove();
				x++;
			}
			if (!inCheck) {
				Location loc = new Location(move.getFrom().getRow(), move
						.getFrom().getColumn() - 4);
				ChessPiece rook = board.getPiece(loc);
				board.makeMove(new Move(rook.getLocation(), new Location(rook
						.getLocation().getRow(), 3)));
			}
		}
	}

	private void castleHandleSmallSide(Move move) {
		boolean inCheck = false;
		if (!board.getPiece(move.getFrom()).hasMoved()
				&& (board.getPiece(move.getFrom()) instanceof King)
				&& move.getTo().getColumn() == move.getFrom().getColumn() + 2) {
			int x = 1;
			inCheck = this.isInCheck(currentPlayer);
			while (!inCheck && x < 3) {
				board.tryMove(new Move(move.getFrom(), new Location(move
						.getFrom().getRow(), move.getFrom().getColumn() + x)));
				inCheck = this.isInCheck(currentPlayer);
				board.undoTriedMove();
				x++;
			}
			if (!inCheck) {
				Location loc = new Location(move.getFrom().getRow(), move
						.getFrom().getColumn() + 3);
				ChessPiece rook = board.getPiece(loc);
				board.makeMove(new Move(rook.getLocation(), new Location(rook
						.getLocation().getRow(), 5)));
			}
		}
	}

	private void checkPawnPromotion(Move move) {
		Location to = move.getTo();
		ChessPiece piece = board.getPiece(to);
		if (piece instanceof Pawn) {
			int currentLocation = to.getRow();
			int boardTopRow = 7;
			int boardBottomRow = 0;
			if (currentPlayer == whiteTeam) {
				if (currentLocation == boardBottomRow)
					promoteThePawn(piece, to);

			} else {
				if (currentLocation == boardTopRow)
					promoteThePawn(piece, to);
			}
		}
	}

	private void promoteThePawn(ChessPiece piece, Location to) {
		ChessPiece promotionPiece = new Queen();
		promotionPiece.setTeam(piece.getTeam());
		board.removePiece(piece, to);
		boardDisplay.removePiece(to);
		board.placePiece(promotionPiece, to);
	}

	private void canEnPassantNextTurn(Move move) {
		Location to = move.getTo();
		Location from = move.getFrom();
		int distance = Math.abs(to.getRow() - from.getRow());
		ChessPiece piece = board.getPiece(to);
		if (piece instanceof Pawn && distance == 2) {
			board.hasEnPassantMove(true, to);
		} else
			board.hasEnPassantMove(false);
	}

	private void checkEnPassant(Move move) {
		if (board.hasEnPassantMove()) {
			Location to = move.getTo();
			Location from = move.getFrom();
			ChessPiece adjPawnRight = null;
			ChessPiece adjPawnLeft = null;
			Location adjPawnLeftLoc = new Location(from.getRow(),
					from.getColumn() - 1);
			Location adjPawnRightLoc = new Location(from.getRow(),
					from.getColumn() + 1);
			if (ChessBoard.isInBounds(adjPawnRightLoc))
				adjPawnRight = board.getPiece(new Location(from.getRow(), from
						.getColumn() + 1));
			if (ChessBoard.isInBounds(adjPawnLeftLoc))
				adjPawnLeft = board.getPiece(new Location(from.getRow(), from
						.getColumn() - 1));
			if (adjPawnRight != null && adjPawnRight.getTeam() != currentPlayer) {
				if (adjPawnRight instanceof Pawn) {
					Location adjPawnLoc = adjPawnRight.getLocation();
					if (to.getColumn() == adjPawnLoc.getColumn()) {
						board.removePiece(adjPawnRight, adjPawnLoc);
						boardDisplay.removePiece(adjPawnLoc);
					}
				}
			} else if (adjPawnLeft != null
					&& adjPawnLeft.getTeam() != currentPlayer) {
				if (adjPawnLeft instanceof Pawn) {
					Location adjPawnLoc = adjPawnLeft.getLocation();
					if (to.getColumn() == adjPawnLoc.getColumn()) {
						board.removePiece(adjPawnLeft, adjPawnLoc);
						boardDisplay.removePiece(adjPawnLoc);
					}
				}
			}
		}
	}

	private void addToHistory() {
		BoardState state = new BoardState(currentPlayer, board);
		history.add(state);
	}

	public Iterator<BoardState> getHistory() {
		return history.iterator();
	}

	public void close() {
		// boardDisplay.dispose();
	}

	private Team buildTeam(Team.Color color, TeamType type) {

		char mainRow;
		char pawnRow;

		if (color == Team.Color.LIGHT) {
			mainRow = '1';
			pawnRow = '2';
		} else {
			mainRow = '8';
			pawnRow = '7';
		}
		Team team = null;
		switch (type) {
		case LOCAL:
			team = new TeamLocal(color, board, this);
			break;
		case SERVER:
			team = new TeamServer(color, board, this);
			break;
		default:
			throw new RuntimeException("undefined team type");
		}
		boardDisplay.addMoveHandler(team);

		setupPiece(new King(), new Location(mainRow, 'e'), team);
		setupPiece(new Queen(), new Location(mainRow, 'd'), team);
		setupPiece(new Bishop(), new Location(mainRow, 'c'), team);
		setupPiece(new Bishop(), new Location(mainRow, 'f'), team);
		setupPiece(new Knight(), new Location(mainRow, 'b'), team);
		setupPiece(new Knight(), new Location(mainRow, 'g'), team);
		setupPiece(new Rook(), new Location(mainRow, 'a'), team);
		setupPiece(new Rook(), new Location(mainRow, 'h'), team);
		for (int i = 0; i < 8; i++) {
			setupPiece(new Pawn(), new Location(pawnRow, (char) ('a' + i)),
					team);
		}
		return team;
	}

	private void setupPiece(ChessPiece piece, Location location, Team team) {
		team.add(piece);
		board.placePiece(piece, location);
		piece.setTeam(team);
	}

	private URL getImageURL(ChessPiece piece) {
		Team team = piece.getTeam();
		String imageLetter = team.isWhite() ? "w" : "b";
		String imagePath = "/Images/" + piece.getName() + imageLetter + ".gif";
		URL imageUrl = getClass().getResource(imagePath);
		return imageUrl;
	}

	public boolean isInCheck(Team team) {
		Location kingsLocation = team.getKing().getLocation();
		Team attackingTeam = (team == whiteTeam) ? blackTeam : whiteTeam;
		boolean inCheckTemp = attackingTeam.canAttack(board, kingsLocation);
		if (!inCheckTemp) {
			isCastlingNotInCheck(team, kingsLocation, attackingTeam,
					inCheckTemp);
		} else {
			team.getKing().inCheckLeft = inCheckTemp;
			team.getKing().inCheckRight = inCheckTemp;
		}
		return inCheckTemp;
	}

	private void isCastlingNotInCheck(Team team, Location kingsLocation,
			Team attackingTeam, boolean inCheckTemp) {
		King king = team.getKing();
		if (attackingTeam.canAttack(board, new Location(kingsLocation.getRow(),
				kingsLocation.getColumn() - 1))) {
			king.inCheckLeft = true;
		} else {
			king.inCheckLeft = false;
		}
		if (attackingTeam.canAttack(board, new Location(kingsLocation.getRow(),
				kingsLocation.getColumn() + 1))) {
			king.inCheckRight = true;
		} else {
			king.inCheckRight = false;
		}
	}

	public boolean canMove(Team team) {
		boolean canMove = false;
		// For each piece of the current team and cannot yet move applies...
		for (Iterator<ChessPiece> i = team.getPieces(); !canMove && i.hasNext();) {
			ChessPiece piece = i.next();
			// For each valid move of that piece and checkmate applies...
			for (Enumeration<Location> e = piece.getLegalMoves(board); !canMove
					&& e.hasMoreElements();) {
				Location to = e.nextElement();
				// Apply the move
				board.tryMove(new Move(piece.getLocation(), to));
				// checkmate applies If the current team is not in check
				canMove = !isInCheck(team);
				// Unapply the move
				board.undoTriedMove();
			}
		}
		// return true iff checkmate applies
		return canMove;
	}

	public boolean isCheckmate() {
		Team currentTeam = currentPlayer;
		return isInCheck(currentTeam) && !canMove(currentTeam);
	}

	public boolean isStalemate() {
		Team currentTeam = currentPlayer;
		return (!isInCheck(currentTeam) && !canMove(currentTeam))
				|| ((whiteTeam.getPieceCount() == 1) && (blackTeam
						.getPieceCount() == 1));
	}

	public void movePiece(Move move, boolean capturePiece) {
		if (capturePiece) {
			IDisplay.Piece displayPiece = boardDisplay
					.removePiece(move.getTo());
			displayPiece.setPieceLocation(null);
		}
		IDisplay.Piece displayPiece = boardDisplay.removePiece(move.getFrom());
		boardDisplay.placePiece(displayPiece, move.getTo());
		displayPiece.setPieceLocation(move.getTo());
	}

	@Override
	public void placePiece(ChessPiece piece, Location location) {
		IDisplay.Piece displayPiece = new BoardDisplayPiece(getImageURL(piece));
		boardDisplay.placePiece(displayPiece, location);
		displayPiece.setPieceLocation(location);
	}

	public List<Location> getMoves(ChessPiece chess) {
		List<Location> correctLocations = new ArrayList<Location>();
		Enumeration<Location> temp = (Enumeration<Location>) chess
				.getLegalMoves(board);
		while (temp.hasMoreElements()) {
			correctLocations.add(temp.nextElement());
		}
		return correctLocations;
	}

	public ChessPiece getPeice(Location l) {
		ChessPiece piece = board.getPiece(l);
		return piece;
	}

}
