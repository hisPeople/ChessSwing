import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

public class GameController implements ChessBoard.IListener, ICheckChecker {

	public enum PlayerType {
		Human, AI, Remote
	}

	private IDisplay boardDisplay;

	private Team whiteTeam;
	private Team blackTeam;

	private ChessBoard board;

	private Player whitePlayer;
	private Player blackPlayer;
	private Player currentPlayer;
	
	private boolean showDisplay;

	private ArrayList<BoardState> history = new ArrayList<BoardState>();

	public GameController(PlayerType whiteType, PlayerType blackType) {

		showDisplay = (whiteType == PlayerType.Human)
				|| (blackType == PlayerType.Human);

		board = new ChessBoard();
		board.AddListener(this);
		if (showDisplay) {
			boardDisplay = new BoardDisplay();
		} else {
			boardDisplay = new NullDisplay();
		}
		// boardDisplay = (showDisplay)? new BoardDisplay(): new NullDisplay();

		whiteTeam = buildTeam(Team.Color.LIGHT);
		blackTeam = buildTeam(Team.Color.DARK);

		// Determine the player types here
		whitePlayer = createPlayer(whiteType, whiteTeam);
		blackPlayer = createPlayer(blackType, blackTeam);

		boardDisplay.setVisible(true);
	}

	private Player createPlayer(PlayerType playerType, Team team) {
		Player player = null;
		switch (playerType) {
		case Human:
			player = new HumanPlayer(team, board, this);
			boardDisplay.addMoveHandler((HumanPlayer) player);
			break;
		case Remote:
			player = new RemotePlayer(team, board, this);
			break;
		case AI:
			player = new AIPlayer(board, team, (team == whiteTeam) ? blackTeam
					: whiteTeam);
			break;
		}
		return player;
	}

	public void play() {
		currentPlayer = whitePlayer;
		boolean isCheckmate = false;
		boolean isStalemate = false;
		addToHistory();
		while (!(isCheckmate || isStalemate)) {
			boardDisplay.promptForMove((currentPlayer == whitePlayer));
			Move move = currentPlayer.getMove();
			board.makeMove(move);
			currentPlayer = (currentPlayer == whitePlayer) ? blackPlayer
					: whitePlayer;
			isCheckmate = isCheckmate();
			isStalemate = isStalemate();
			if (!isCheckmate && isInCheck(currentPlayer.getTeam())) {
				boardDisplay.notifyCheck(currentPlayer == whitePlayer);
			}
			addToHistory();
		}
		if (isCheckmate) {
			boardDisplay.notifyCheckmate(currentPlayer == blackPlayer);
		} else if (isStalemate) {
			boardDisplay.notifyStalemate();
		}
	}

	private void addToHistory() {
		BoardState state = BoardState.createFromBoard(board,
				(currentPlayer == whitePlayer));
		history.add(state);
	}

	public Iterator<BoardState> getHistory() {
		return history.iterator();
	}

	public void close() {
		boardDisplay.dispose();
	}

	private Team buildTeam(Team.Color color) {

		char mainRow;
		char pawnRow;

		if (color == Team.Color.LIGHT) {
			mainRow = '1';
			pawnRow = '2';
		} else {
			mainRow = '8';
			pawnRow = '7';
		}

		Team team = new Team(color);

		setupPiece(new King(), new Location(mainRow, 'e'), team);
		setupPiece(new Queen(), new Location(mainRow, 'd'), team);
		setupPiece(new Bishop(), new Location(mainRow, 'c'), team);
		setupPiece(new Bishop(), new Location(mainRow, 'f'), team);
		setupPiece(new Knight(), new Location(mainRow, 'b'), team);
		setupPiece(new Knight(), new Location(mainRow, 'g'), team);
		setupPiece(new Rook(), new Location(mainRow, 'a'), team);
		setupPiece(new Rook(), new Location(mainRow, 'h'), team);
		for (int i = 0; i < BoardDisplay.N_COLS; i++) {
			setupPiece(new Pawn(), new Location(pawnRow, (char) ('a' + i)),
					team);
		}

		return team;
	}

	private void setupPiece(ChessPiece piece, Location location, Team team) {
		team.add(piece);
		board.placePiece(piece, location);
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
		return attackingTeam.canAttack(board, kingsLocation);
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
		Team currentTeam = currentPlayer.getTeam();
		return isInCheck(currentTeam) && !canMove(currentTeam);
	}

	public boolean isStalemate() {
		Team currentTeam = currentPlayer.getTeam();
		return (!isInCheck(currentTeam) && !canMove(currentTeam))
				|| ((whiteTeam.getPieceCount() == 1) && (blackTeam
						.getPieceCount() == 1));
	}

	@Override
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
		if (showDisplay) {
			IDisplay.Piece displayPiece = new BoardDisplayPiece(
					getImageURL(piece));
			boardDisplay.placePiece(displayPiece, location);
			displayPiece.setPieceLocation(location);
		}
	}
}
