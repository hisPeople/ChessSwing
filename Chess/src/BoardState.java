
public class BoardState {

	private byte[] cells = new byte[32];
	private boolean isWhitesMove;
	
	private static final byte COLOR_BIT = 0x08;
	private static final byte PAWN = 0x01;
	private static final byte ROOK = 0x02;
	private static final byte KNIGHT = 0x03;
	private static final byte BISHOP = 0x04;
	private static final byte QUEEN = 0x05;
	private static final byte KING = 0x06;
	
	public static BoardState createFromBoard(ChessBoard board, boolean isWhitesMove) {
		BoardState state = new BoardState();
		state.isWhitesMove = isWhitesMove;
		for (int row = 0; row < ChessBoard.N_ROWS; row++) {
			for (int column = 0; column < ChessBoard.N_COLS; column++) {
				Location here = new Location(row, column);
				if (board.hasPiece(here)) {
					ChessPiece piece = board.getPiece(here);
					state.setPiece(here, piece);
				}
			}
		}
		return state;
	}
	
	private void setPiece(Location location, ChessPiece piece) {
		byte value = getPieceValue(piece);
		setValue(location, value);
	}
	
	public void setValue(Location location, byte value) {
		int index = getIndex(location);
		int n = index/2;
		cells[n] = (isLowNibble(index))? (byte) ((cells[n] & 0xF0) | value): (byte) ((value << 4) | (cells[n] & 0x0F));
	}
	
	public byte getValue(Location location) {
		int index = getIndex(location);
		int n = index/2;
		byte result = (byte) (isLowNibble(index)? (cells[n] & 0x0F): ((cells[n] >> 4) & 0x0F));
		return result;
	}
	
	private int getIndex(Location location) {
		return (location.getRow() * ChessBoard.N_COLS) + location.getColumn();
	}
	
	private boolean isLowNibble(int index) {
		return ((index & 1) == 0);
	}
	
	private byte getPieceValue(ChessPiece piece) {
		byte result = piece.getTeam().isWhite()? COLOR_BIT: 0x00;
		
		if (piece instanceof Pawn) {
			result |= PAWN;
		} else if (piece instanceof Rook) {
			result |= ROOK;
		} else if (piece instanceof Knight) {
			result |= KNIGHT;
		} else if (piece instanceof Bishop) {
			result |= BISHOP;
		} else if (piece instanceof Queen) {
			result |= QUEEN;
		} else if (piece instanceof King) {
			result |= KING;
		}
		return result;
	}
	
	public boolean equals(Object object) {
		boolean result = false;
		if (object instanceof BoardState) {
			BoardState that = (BoardState) object;
			result = this.isWhitesMove == that.isWhitesMove;
			for (int i = 0; result && (i < cells.length); i++) {
				result = this.cells[i] == that.cells[i];
			}
		}
		return result;
	}
	
	public int hashCode() {
		int result = 0;
		for (int i = 0; i < cells.length; i += 4) {
			result ^= (cells[i+3] | cells[i+2] | cells[i+1] | cells[i]);
		}
		return result+(isWhitesMove?+1:0);
	}
}
