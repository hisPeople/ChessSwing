package model;

public class NibbleBoard {

	private byte[] gameBoard;
	
	//looking at 4 bit sections
	public NibbleBoard(byte[] buffer)
	{
		gameBoard = buffer;
	}
	
	public int get(int row, int col)
	{
		int location = getBufferIndex(row, col);
		int value = (isHi(row, col)? gameBoard[location] >> 4 : gameBoard[location]) & 0x0f ;
		return value;
	}
	
	public void set(int row, int col, int value) {
		int location = getBufferIndex(row, col);
		if (isHi(row, col)) {
			gameBoard[location] = (byte) ((gameBoard[location] & 0x0f) | ((value << 4) & 0xf0));
		} else {
			gameBoard[location] = (byte) ((gameBoard[location] & 0xf0) | (value & 0x0f));
		}
	}
	
	private int getBufferIndex(int row, int col) {
		return (row * 8 + col) / 2;
	}
	
	// tells whether you are looking at the first 4 bits in the byte or the second 4 bits.
	private boolean isHi(int row, int col) {

		return ((row * 8 + col) % 2) > 0;
		//0100 0000
		//false produces higher 4 bits (0000) and true produces lower 4 bits (0100)
	}
}
