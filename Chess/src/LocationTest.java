import junit.framework.TestCase;


public class LocationTest extends TestCase {

	public void testLocationCharChar() {
		Location location = new Location('1', 'a');
		assertEquals(0, location.getRow());
		assertEquals(0, location.getColumn());
		
		location = new Location('8', 'a');
		assertEquals(7, location.getRow());
		assertEquals(0, location.getColumn());
		
		location = new Location('1', 'h');
		assertEquals(0, location.getRow());
		assertEquals(7, location.getColumn());
	}

	public void testLocationIntInt() {
		Location location = new Location(0, 0);
		assertEquals(0, location.getRow());
		assertEquals(0, location.getColumn());
		
	}

	public void testGetRow() {
		Location location = new Location(1, 2);
		assertEquals(1, location.getRow());
		
	}

	public void testGetColumn() {
		Location location = new Location(1, 2);
		assertEquals(2, location.getColumn());
		
	}
	
	public void testToString() {
		Location location = new Location(1,2);
		assertEquals("c2", location.toString());
	}

}
