import java.io.*;

public class GameSaver {
    public static void saveGame(int humanPos, int computerPos) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("save.txt"))) {
            writer.write(humanPos + "," + computerPos);
        }
    }

    public static int[] loadGame() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("save.txt"))) {
            String[] parts = reader.readLine().split(",");
            return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
        }
    }

    public static boolean saveExists() {
        return new File("save.txt").exists();
    }
}