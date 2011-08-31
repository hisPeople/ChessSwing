package controller;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import model.ChessCommandHandler;
import edu.neumont.learningChess.api.Location;

public class TextCommandProcessor {

	private static Pattern pattern = Pattern.compile("((\\s*[a-hA-H])([1-8])\\s+([a-hA-H])([1-8])(\\*)?)|((\\s*[KQBNRPkqbnrp])([lLdD])([a-hA-H])([1-8]))");
	private static final int LOCATION_INDEX = 1;
	private static final int FROM_COLUMN = 2;
	private static final int FROM_ROW = 3;
	private static final int TO_COLUMN = 4;
	private static final int TO_ROW = 5;
	private static final int TAKES_PIECE = 6;
	private static final int PLACEMENT_INDEX = 7;
	private static final int PIECE_ID = 8;
	private static final int PIECE_COLOR = 9;
	private static final int PLACEMENT_COLUMN = 10;
	private static final int PLACEMENT_ROW = 11;
	
	public void processCommands(InputStream inputStream, ChessCommandHandler handler) {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		try {
			// For each line in the input stream...
			for (;;) {
				String line = reader.readLine();
				if (line == null) break;
				
				line = line.trim().toLowerCase();
				
				int boundary = 0;
				Matcher matcher = pattern.matcher(line);
				while (matcher.find()) {
					if (matcher.start() != boundary) {
						throw new RuntimeException("Bad epression in line \""+line+"\"");
					}
					boundary = matcher.end();
//					for (int i = 0; i < matcher.groupCount(); i++) {
//						System.out.println("Group "+i+" = "+matcher.group(i));
//					}
					// If this is a placement token,
					if (matcher.group(PLACEMENT_INDEX) != null) {
						Location location = new Location(matcher.group(PLACEMENT_ROW).charAt(0), matcher.group(PLACEMENT_COLUMN).charAt(0));
						handler.handlePlacement(matcher.group(PIECE_ID).trim(), matcher.group(PIECE_COLOR).equals("l"), location);
					} else if (matcher.group(LOCATION_INDEX) != null) {  // Otherwise this is a movement pair of tokens,
						Location from = new Location(matcher.group(FROM_ROW).charAt(0), matcher.group(FROM_COLUMN).trim().charAt(0));
						Location to = new Location(matcher.group(TO_ROW).charAt(0), matcher.group(TO_COLUMN).charAt(0));
						handler.handleMovement(from, to, matcher.group(TAKES_PIECE) != null);
					}
				}
				
				if (boundary != (line.length())) {
					throw new RuntimeException("Bad epression in line \""+line+"\"");
				}
			}
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
