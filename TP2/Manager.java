import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

public class Manager {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        new File("results").mkdir();

        Population population = new Population(1000, 1., 10.);

        population.runSimulation("out1");
    }

}
