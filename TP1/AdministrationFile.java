import java.io.*;
import java.util.*;

public class AdministrationFile {

    private int particlesQuantity;

    public AdministrationFile(){
        Random random = new Random(Constants.RANDOM_SEED);
        particlesQuantity = random.nextInt(Constants.PARTICLES_QUANTITY + 1); //create random between 10 and 300
    }

    private void createRandomParticlesFile(BufferedWriter file,double multiple,String format) throws IOException {
        for (int i = 0; i < particlesQuantity; i++) {
            file.write(String.format(Locale.ENGLISH,format,Math.random() * multiple));
            file.write(" ");
            file.write(String.format(Locale.ENGLISH,format,Math.random() * multiple));
            file.newLine();
        }
    }

    public void generatorFile(){
        try {
                BufferedWriter bw = new BufferedWriter(new FileWriter("Dynamic.txt"));
                bw.write(String.valueOf(0)); // tiempo = 0
                bw.newLine();
                createRandomParticlesFile(bw, Constants.BOX_LENGTH, "%6.8e");
                bw.close();

                bw = new BufferedWriter(new FileWriter("Static.txt"));
                bw.write(String.valueOf(particlesQuantity));
                bw.newLine();
                bw.write(String.valueOf(Constants.BOX_LENGTH));
                bw.newLine();
                bw.write(String.valueOf(Constants.NEIGHBOUR_RADIUS));
                bw.newLine();
                createRandomParticlesFile(bw, Constants.MAX_PARTICLE_RADIUS, "%.4f");
                bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
