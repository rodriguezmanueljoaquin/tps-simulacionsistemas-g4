import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class Manager {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        new File("results").mkdir();

        Population population = new Population();

        population.runSimulation("out0");
        population.runSimulation("out1");
    }

}
