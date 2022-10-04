import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class HorizonResultsReader {
    private static final String POSITIONSANDVELOCITIESSEGMENT_ID = "$$SOE";

    private static Pair<Double, Double> GetDoublesAfterNLinesOnSegment(String path, String segmentIdentifier, Integer skips){
        try {
            File file = new File(path);
            Scanner scanner = new Scanner(file);
            String line;
            String[] tokens;
            do {
                line = scanner.nextLine();
                tokens = line.split(" ");
            }while (tokens.length == 0 || !tokens[0].equals(segmentIdentifier));
            for (int i = 0; i < skips; i++)
                line = scanner.nextLine();

            tokens = line.split("=");
            double first = Double.parseDouble(tokens[0].split(" ")[0]);
            double second = Double.parseDouble(tokens[1].split(" ")[0]);

            return new Pair<>(first, second);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (NoSuchElementException e) {
            System.out.println("Element not found");
            throw e;
        }
    }

    public static Pair<Double, Double> GetPosition(String path) {
        return GetDoublesAfterNLinesOnSegment(path, POSITIONSANDVELOCITIESSEGMENT_ID, 2);
    }

    public static Pair<Double, Double> GetVelocity(String path) {
        return GetDoublesAfterNLinesOnSegment(path, POSITIONSANDVELOCITIESSEGMENT_ID, 3);
    }

}
