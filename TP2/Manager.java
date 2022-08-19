import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class Manager {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        Population population = new Population();

        population.createOutputFile("out0");

        population.nextIteration();
        population.createOutputFile("out1");
    }

}
