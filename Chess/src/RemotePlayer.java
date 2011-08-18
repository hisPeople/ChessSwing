import java.util.ArrayList;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class RemotePlayer extends Player implements ChessCommandHandler, ChessBoard.IListener {
	
	public class CommandProcessor extends Thread {
		private TextCommandProcessor commandProcessor;
		private ChessCommandHandler handler;
		
		public CommandProcessor(ChessCommandHandler handler) {
			this.handler = handler;
			commandProcessor = new TextCommandProcessor();
		}
		
		public void run() {
			commandProcessor.processCommands(System.in, handler);
		}
	}

	ArrayList<Move> moves = new ArrayList<Move>();
	
	private ChessBoard board;
	private ICheckChecker checkChecker;
	CommandProcessor commandProcessor;

	public RemotePlayer(Team team, ChessBoard board, ICheckChecker checkChecker) {
		super(team);
		this.board = board;
		board.AddListener(this);
		this.checkChecker = checkChecker;
		startCommandProcessingThread();
	}
	
	private void startCommandProcessingThread() {
		commandProcessor = new CommandProcessor(this);
		commandProcessor.start();
	}

	@Override
	public synchronized Move getMove() {
		if (moves.size() == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		Move tempMove = moves.remove(0);
		return tempMove;
	}

	@Override
	public synchronized void handleMovement(Location from, Location to, boolean takesPiece) {
		Move move = new Move(from, to);
		
		if (isLegalMove(move)) {
			moves.add(move);
			if (moves.size() == 1) {
				notify();
			}
		} else {
			System.out.println("Illegal move!");
		}
	}

	@Override
	public void handlePlacement(String pieceId, boolean isWhite,
			Location location) {
		throw new NotImplementedException();
	}
	
	public boolean isLegalMove(Move move) {
		ChessPiece movingPiece = board.getPiece(move.getFrom());
		Team movingTeam = movingPiece.getTeam();
		return (movingTeam == team) && movingPiece.isLegalMove(board, move) && !causesCheckmate(move);
	}
	
	private boolean causesCheckmate(Move move) {
		board.tryMove(move);
		boolean result = checkChecker.isInCheck(team);
		board.undoTriedMove();
		
		return result;
	}

	@Override
	public void movePiece(Move move, boolean capturePiece) {
		ChessPiece piece = board.getPiece(move.getTo());
		if (piece.getTeam() != team) {
			System.out.println(move.toString()+(capturePiece?"*":""));
		}
	}

	@Override
	public void placePiece(ChessPiece piece, Location location) {
	}
}
