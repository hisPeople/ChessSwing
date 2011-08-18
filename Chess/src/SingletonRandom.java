import java.util.Random;


public class SingletonRandom {

	public static Random random = new Random(37L);
	
	public static int nextInt(int maxValue) {
		return random.nextInt(maxValue);
	}
}
