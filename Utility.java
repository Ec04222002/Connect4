import java.util.Random;

public class Utility {

    // exclusive of max
    public static Random random = new Random();

    public static int getRandomNumber(int min, int max) {
        return random.nextInt((max - min) + min);
    }

    public static int log2(int n) {
        return (n == 1) ? 0 : 1 + log2(n / 2);
    }
}