


public class HumanPlayer extends Player implements IDisplay.IMoveHandler {

	private ChessBoard board;
	private ICheckChecker checkChecker;
	private boolean awaitingMove = false;
	private Move awaitedMove = null;
	
	public HumanPlayer(Team team, ChessBoard board, ICheckChecker checkChecker) {
		super(team);
		this.board = board;
		this.checkChecker = checkChecker;
	}
	
	@Override
	public synchronized Move getMove() {
		awaitingMove = true;
		try {
			wait();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		Move tempMove = awaitedMove;
		awaitedMove = null;
		awaitingMove = false;
		return tempMove;
	}

	@Override
	public synchronized boolean handleMove(Move move) {
		boolean canHandleMove = true;
		if (awaitingMove) {
			if (isLegalMove(move)) {
				canHandleMove = true;
				awaitedMove = move;
				notify();
			} else {
				canHandleMove = false;
			}
		}
		return canHandleMove;
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
	
//	public boolean doesCheckApply() {
//		return checkChecker.isInCheck(currentTeam);
//	}
}
