package model;

public class Location {
	
	private static final int VALUE_OF_A = Character.getNumericValue('a');
	private static final int VALUE_OF_1 = Character.getNumericValue('1');
	
	private int row;
	private int column;
	
	public Location(char rowChar, char columnChar) {
		row = Character.getNumericValue(rowChar) - VALUE_OF_1;
		column = Character.getNumericValue(columnChar) - VALUE_OF_A;
	}
	
	public Location(int row, int column) {
		this.row = row;
		this.column = column;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getColumn() {
		return column;
	}
	
	public String toString() {
		char rowChar = (char) ('1' + row);
		char columnChar = (char) ('a' + column);
		
		return String.valueOf(columnChar) + String.valueOf(rowChar);
	}
	
	public boolean equals(Object o) {
		boolean isEqual = false;
		if (o instanceof Location) {
			Location that = (Location) o;
			isEqual = (this.row == that.row) && (this.column == that.column);
		}
		return isEqual;
	}
	
	public int hashCode() {
		return row^column;
	}
}
