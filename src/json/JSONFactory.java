package json;

import java.util.ArrayList;
import java.util.List;

import model.Location;
import model.Move;

public class JSONFactory 
{

	public static Move createMoveFromJSON(String jsonString)
	{
		Move moveGeneratedFromJSON = null;
		try 
		{
			JSONObject moveJSON = new JSONObject(jsonString);
			
			//Make the from location object by getting the row and columns values from the json object.
			Location fromJSON = new Location(
					moveJSON.getJSONObject("move").getJSONObject("from").getJSONArray("location").getInt(1),
					moveJSON.getJSONObject("move").getJSONObject("from").getJSONArray("location").getInt(0));
			
			//Make the to location object by getting the row and columns values from the json object.
			Location toJSON = new Location(
					moveJSON.getJSONObject("move").getJSONObject("to").getJSONArray("location").getInt(1),
					moveJSON.getJSONObject("move").getJSONObject("to").getJSONArray("location").getInt(0));
			
			//Create the move object by passing in the two created location objects.
			moveGeneratedFromJSON = new Move(fromJSON, toJSON);
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		return moveGeneratedFromJSON;
	}
	
	public static String createJSONFromMove(Move move)
	{
		//Make the same move using JSON
		JSONObject jsonHolder = new JSONObject();
		
		JSONObject moveObject = new JSONObject();
		
		JSONObject fromObject = new JSONObject();
		JSONObject toObject = new JSONObject();
		
		JSONArray fromLocationArray = new JSONArray();
		JSONArray toLocationArray = new JSONArray();
		
		try 
		{
			//Set the column of the location to 0 and the row to 1 in the JSONArray
			fromLocationArray.put(0, move.getFrom().getColumn());
			fromLocationArray.put(1, move.getFrom().getRow());
			
			//Add the location object to the from location object.
			fromObject.put("location", fromLocationArray);
			
			//Set the column of the location to 0 and the row to 1 in the JSONArray
			toLocationArray.put(0, move.getTo().getColumn());
			toLocationArray.put(1, move.getTo().getRow());
			
			//Add the location object to the to location object.
			toObject.put("location", toLocationArray);
			
			//Put both to and from into the move object
			moveObject.put("from", fromObject);
			moveObject.put("to", toObject);
			
			//Put the move object into a holder class
			jsonHolder.put("move", moveObject);
		}
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		
		return jsonHolder.toString();
	}
	
	public static JSONState createJSONStateFromJSON(String jsonString)
	{
		JSONState jsonStateMadeFromJSON = null;
		try
		{
			//Made the jsonHolder class using the jsonString passed into the method
			JSONObject jsonStateHolder = new JSONObject(jsonString);
			
			//Get the JSONState object from the holder
			JSONObject jsonState = jsonStateHolder.getJSONObject("jsonState");
			
			//Create a JSONState object using the generated JSON
			//First create a PieceState[] to pass into the constructor of the JSONState object.
			PieceState[] jsonCreatedPieceStateArray = new PieceState[32];
			
			//Get the pieceStates array from the jsonStateObject
			JSONArray pieceStateArray = jsonState.getJSONArray("pieceStates");
			
			//Make the piece state objects
			for (int i = 0; i < jsonState.getInt("numberOfPieceStates"); i++) 
			{
				System.out.println(i);
				//Get the pieceState JSONObject from the array of pieceStates
				JSONObject pieceState = pieceStateArray.getJSONObject(i);
				
				//If a piece state existed at the current index...
				if(pieceState != null)
				{
					//Create the piece name string
					String pieceName = pieceState.getString("name");
					
					//Create the PieceState and add it to the PieceState[]
					if(pieceName.equals("Pawn"))
					{
						jsonCreatedPieceStateArray[i] = generatePieceStateWithoutSpecialBooleanFromJSON(pieceState, pieceName);
					}
					else if(pieceName.equals("Rook"))
					{
						jsonCreatedPieceStateArray[i] = generatePieceStateWithSpecialBooleanFromJSON(pieceState, pieceName);
					}
					else if(pieceName.equals("Knight"))
					{
						jsonCreatedPieceStateArray[i] = generatePieceStateWithoutSpecialBooleanFromJSON(pieceState, pieceName);
					}
					else if(pieceName.equals("Bishop"))
					{
						jsonCreatedPieceStateArray[i] = generatePieceStateWithoutSpecialBooleanFromJSON(pieceState, pieceName);
					}
					else if(pieceName.equals("Queen"))
					{
						jsonCreatedPieceStateArray[i] = generatePieceStateWithoutSpecialBooleanFromJSON(pieceState, pieceName);
					}
					else if(pieceName.equals("King"))
					{
						jsonCreatedPieceStateArray[i] = generatePieceStateWithSpecialBooleanFromJSON(pieceState, pieceName);
					}
					else
					{
						System.out.println("There was an error in creating the JSONState object by using the JSONString. The pieceState name " + pieceName + " did not match any of the options.");
					}
				}
			}
			
			//Get the isWhitePlayerTurn boolean
			boolean jsonGeneratedIsWhiteTurn = jsonState.getBoolean("isWhiteTurn");
			
			//Get the pawnJumped boolean
			boolean jsonGeneratedPawnJumped = jsonState.getBoolean("pawnJumped");
			
			//Get the pawnJumpedLocation if pawnJumped is true
			Location jsonGeneratedPawnJumpedLocation = null;
			if(jsonGeneratedPawnJumped)
			{
				JSONArray pawnJumpedLocation = jsonState.getJSONArray("pieceJumpedLocation");
				int row = pawnJumpedLocation.getInt(1);
				int col = pawnJumpedLocation.getInt(0);
				jsonGeneratedPawnJumpedLocation = new Location(row,col);
			}
			
			//Create the JSONState object.
			jsonStateMadeFromJSON = new JSONState(jsonCreatedPieceStateArray, jsonGeneratedIsWhiteTurn, jsonGeneratedPawnJumped, jsonGeneratedPawnJumpedLocation );
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		
		return jsonStateMadeFromJSON;
	}
	
	public static String createJSONFromJSONState(JSONState jsonState)
	{
		//Set up needed JSON objects
		JSONObject jsonStateHolder = new JSONObject();
		JSONObject jsonStateObject = new JSONObject();
		JSONArray jsonStatePiecesArray = new JSONArray();
		
		try 
		{
			int numberOfPieceStates = 0;
			//Loop over the jsonState's piece states.
			for (PieceState pieceState : jsonState.pieceStates) 
			{
				if(pieceState != null)
				{
					//Add each piece state to the array of piece states
					jsonStatePiecesArray.put(generateJSONFromPieceStateObject(pieceState));
					
					//Keep track of the size of the array
					numberOfPieceStates++;
				}
			}
			
			//Make the pieceJumpedLocation array
			if(jsonState.pawnJumped && jsonState.pawnJumpedLocation != null)
			{
				JSONArray pieceJumpedLocation = new JSONArray();
				pieceJumpedLocation.put(0,jsonState.pawnJumpedLocation.getColumn());
				pieceJumpedLocation.put(1,jsonState.pawnJumpedLocation.getRow());
				jsonStateObject.put("pieceJumpedLocation", pieceJumpedLocation);
			}
			
			//Add the pieceStates array and isWhiteTurn boolean to the jsonStateObject
			jsonStateObject.put("pieceStates", jsonStatePiecesArray);
			jsonStateObject.put("isWhiteTurn", jsonState.isWhiteTurn);
			jsonStateObject.put("numberOfPieceStates", numberOfPieceStates);
			jsonStateObject.put("pawnJumped", jsonState.pawnJumped);
			
			
			//Add the jsonStateObject to the holder 
			jsonStateHolder.put("jsonState", jsonStateObject);
			
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		
		return jsonStateHolder.toString();
	}
	
	
	public static List<Move> createMoveHistoryFromJSON(String jsonString)
	{
		ArrayList<Move> moveArrayList = new ArrayList<Move>();
		try
		{
			//Made the jsonHolder class using the jsonString passed into the method
			JSONObject moveHistoryHolder = new JSONObject(jsonString);
			JSONObject jsonMoveObject = moveHistoryHolder.getJSONObject("moveHistory");
			JSONArray jsonArray = jsonMoveObject.getJSONArray("moveArray");
			
			int numberOfMoves = jsonMoveObject.getInt("numberOfMoves");
			
			for(int i = 0; i < numberOfMoves; i++)
			{
				moveArrayList.add(createMoveFromJSON(jsonArray.getString(i)));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return moveArrayList;
	}
	
	public static String createJSONFromMoveHistory(List<Move> moveHistory)
	{
		JSONObject moveHistoryHolder = new JSONObject();
		JSONObject moveHistoryObject = new JSONObject();
		JSONArray moveArray = new JSONArray();
		
		try
		{
			int numberOfMoves = 0;
			//Loop over the jsonState's piece states.
			for (Move move : moveHistory) 
			{
				if(move != null)
				{
					//Add each piece state to the array of piece states
					moveArray.put(new JSONObject((move)));
					
					//Keep track of the size of the array
					numberOfMoves++;
				}
			}
			moveHistoryObject.put("moveArray", moveArray);
			moveHistoryObject.put("numberOfMoves", numberOfMoves);
			
			moveHistoryHolder.put("moveHistory", moveHistoryObject);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return moveHistoryHolder.toString();
	}
	
	
	
	private static JSONObject generateJSONFromPieceStateObject(PieceState pieceState)
	{
		JSONObject pieceStateObject = null;
		if(pieceState != null)
		{
			String pieceName = pieceState.pieceName;
			if(pieceName.equals("Pawn"))
			{
				pieceStateObject = generateJSONObjectWithoutSpecialBooleanFromPieceState(pieceState, pieceName);
			}
			if(pieceName.equals("Rook"))
			{
				pieceStateObject = generateJSONObjectWithSpecialBooleanFromPieceState(pieceState, pieceName);
			}
			else if(pieceName.equals("Knight"))
			{
				pieceStateObject = generateJSONObjectWithoutSpecialBooleanFromPieceState(pieceState, pieceName);
			}
			else if(pieceName.equals("Bishop"))
			{
				pieceStateObject = generateJSONObjectWithoutSpecialBooleanFromPieceState(pieceState, pieceName);
			}
			else if(pieceName.equals("Queen"))
			{
				pieceStateObject = generateJSONObjectWithoutSpecialBooleanFromPieceState(pieceState, pieceName);
			}
			else if(pieceName.equals("King"))
			{
				pieceStateObject = generateJSONObjectWithSpecialBooleanFromPieceState(pieceState, pieceName);
			}
			else
			{
				System.out.println("There was an error in generating JSON from the PieceState object. The pieceState name \"" + pieceName + "\" did not match any of the options.");
			}
		}
		
		return pieceStateObject;
	}
	
	private static JSONObject generateJSONObjectWithoutSpecialBooleanFromPieceState(PieceState pieceState, String name)
	{
		JSONObject pieceStateObject = null;
		try
		{
//			Set up the JSON location array values (Column, then Row)
			JSONArray pieceStateLocationArray = generateJSONLocationArrayFromIntValues(pieceState.location.getColumn(), pieceState.location.getRow());
			
			//Create the PieceStateObject with the created pieces.
			pieceStateObject = new JSONObject();
			pieceStateObject.put("name", pieceState.pieceName);
			pieceStateObject.put("location", pieceStateLocationArray);
			pieceStateObject.put("isWhite", pieceState.isWhite);
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		
		return pieceStateObject;
	}
	
	private static JSONObject generateJSONObjectWithSpecialBooleanFromPieceState(PieceState pieceState, String name)
	{
		JSONObject pieceStateObject = null;
		try
		{
//			Set up the JSON location array values (Column, then Row)
			JSONArray pieceStateLocationArray = generateJSONLocationArrayFromIntValues(pieceState.location.getColumn(), pieceState.location.getRow());
			
			//Create the PieceStateObject with the created pieces.
			pieceStateObject = new JSONObject();
			pieceStateObject.put("name", pieceState.pieceName);
			pieceStateObject.put(getSpecialBooleanType(name), pieceState.specialMoveInformation);
			pieceStateObject.put("location", pieceStateLocationArray);
			pieceStateObject.put("isWhite", pieceState.isWhite);
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		
		return pieceStateObject;
	}
	
	private static JSONArray generateJSONLocationArrayFromIntValues(int column, int row)
	{
		JSONArray pieceStateLocationArray = new JSONArray();
		pieceStateLocationArray.put(column);
		pieceStateLocationArray.put(row);
		return pieceStateLocationArray;
	}
	
	private static PieceState generatePieceStateWithoutSpecialBooleanFromJSON(JSONObject pieceState, String name)
	{
		PieceState pieceStateObject = null;
		try
		{
			//Create the Location object
			Location generatedLocation = generateLocationObject(pieceState);
			pieceStateObject = new PieceState(name,generatedLocation, false, pieceState.getBoolean("isWhite"));
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		return pieceStateObject;
	}
	
	private static PieceState generatePieceStateWithSpecialBooleanFromJSON(JSONObject pieceState, String name)
	{
		PieceState pieceStateObject = null;
		Location generatedLocation = null;
		boolean special = false;
		try
		{
			//Create the Location object
			generatedLocation = generateLocationObject(pieceState);
			
			//Get special boolean
			special = pieceState.getBoolean(getSpecialBooleanType(name));
			pieceStateObject = new PieceState(name,generatedLocation, special, pieceState.getBoolean("isWhite"));
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
		
		return pieceStateObject;
	}
	
	private static String getSpecialBooleanType(String name)
	{
		String special = "";
		if(name.equals("Pawn"))
			special = "jumped";
		else
			special = "moved";
		return special;
	}
	
	private static Location generateLocationObject(JSONObject pieceState)
	{
		//Get location Array
		JSONArray locationArray = null;
		
		//Define the location object to be created
		Location generatedLocation = null;
		try 
		{
			//Get the location array
			locationArray = pieceState.getJSONArray("location");
			
			//Get the column and row
			int column = locationArray.getInt(0);
			int row = locationArray.getInt(1);
			
			//Create the location object
			generatedLocation = new Location(row, column);
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		return generatedLocation;
	}
}
