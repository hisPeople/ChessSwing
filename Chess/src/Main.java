


public class Main {

	public static void main(String[] args) {

		GameController.PlayerType white;
		GameController.PlayerType black;
		if ((args.length == 0) || (args[0].equalsIgnoreCase("white"))) {
			white = GameController.PlayerType.AI;
			black = GameController.PlayerType.Human;
		} else {
			white = GameController.PlayerType.Human;
			black = GameController.PlayerType.AI;
		}
		GameController game = new GameController(white, black);
		game.play();
		game.close();
	}
	
	public static void old_main(String[] args) {

		TextCommandProcessorOutput output = new TextCommandProcessorOutput(System.out);
		TextCommandProcessor processor = new TextCommandProcessor();
		try {
			processor.processCommands(System.in, output);
		}
		catch (Throwable e) {
			System.out.println(e.getMessage());
		}
	}
}
