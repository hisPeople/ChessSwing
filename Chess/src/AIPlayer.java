  import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class AIPlayer extends Player {

	private static final int LOOK_AHEAD_DEPTH = 4;
	private static final long SEARCH_TIME = 4500;
	private ChessBoard board;
	private Team otherTeam;
	
	public AIPlayer(ChessBoard board, Team team, Team otherTeam) {
		super(team);
		this.board = board;
		this.otherTeam = otherTeam;
	}

	public class SearchResult {
		private Move move;
		private int value;
		public SearchResult(Move move, int value) {
			this.move = move;
			this.value = value;
		}
		public Move getMove() {
			return move;
		}
		public int getValue() {
			return value;
		}
	}
	
	private static long now() {
		Date now = new Date();
		return now.getTime();
	}
	
	@Override
	public Move getMove() {
		long endTime = now() + SEARCH_TIME;
		SearchResult result = findBestMove(team, otherTeam, LOOK_AHEAD_DEPTH, endTime);
		return result.getMove();
	}
	
	private SearchResult findBestMove(Team us, Team them, int depth, long endTime) {
		ArrayList<SearchResult> results = null;
		if ((depth > 0) && (now() < endTime)) {
			int bestValue = 0;
			// For each move on the team...
			for (Iterator<Move> i = us.getMoves(board); i.hasNext(); ) {
				Move move = i.next();
				// try the move
				board.tryMove(move);
				// find the best counter move
				SearchResult counter = findBestMove(them, us, depth-1, endTime);
				// undo the move
				board.undoTriedMove();
				// calculate the value of the move
				int moveValue = getValueOfLocation(move.getTo()) - ((counter == null)? 0: counter.getValue());
				// If the value is the best value,
				if ((results == null) || (moveValue > bestValue)) {
					// Save the move and update the best value
					SearchResult result = new SearchResult(move, moveValue);
					results = new ArrayList<SearchResult>();
					results.add(result);
					bestValue = moveValue;
				} else if (moveValue == bestValue) {
					SearchResult result = new SearchResult(move, moveValue);
					results.add(result);
				}
			}
		}
		return ((results == null) || (results.size() == 0))? null: results.get(SingletonRandom.nextInt(results.size()));
	}

	private int getValueOfLocation(Location location) {
		int pieceValue = board.hasPiece(location)? board.getPiece(location).getValue(): 0;
		return pieceValue;
	}
}
