import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

public class Manager {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        new File("results").mkdir();

        Population population;

        for(int i=5; i > 0; i--){

            population = new Population(1000, (double) i, 10.);
            population.runSimulation("out_eta_"+i);

        }

    }

}
