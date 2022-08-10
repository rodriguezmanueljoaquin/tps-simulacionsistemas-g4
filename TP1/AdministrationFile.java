import java.io.*;
import java.util.*;

public class AdministrationFile {

    private int particlesQuantity;
    private int boxLength;
    private static final int MAX_PARTICLES_QUANTITY = 10000;
    private static final int MIN_PARTICLES_QUANTITY = 9000;
    private static final int MAX_BOX_LENGTH = 10;
    private static final int MIN_BOX_LENGTH = 10;

    public AdministrationFile(){
        Random random = new Random();
        particlesQuantity = random.nextInt(MAX_PARTICLES_QUANTITY - MIN_PARTICLES_QUANTITY + 1) + MIN_PARTICLES_QUANTITY; //create random between 10 and 300
        boxLength= random.nextInt(MAX_BOX_LENGTH - MIN_BOX_LENGTH + 1) + MIN_BOX_LENGTH; //create random between 3 and 15
    }

    private void createRandomParticlesFile(BufferedWriter file,int multiple,String format) throws IOException {
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
                createRandomParticlesFile(bw, boxLength, "%6.8e");
                bw.close();

                bw = new BufferedWriter(new FileWriter("Static.txt"));
                bw.write(String.valueOf(particlesQuantity));
                bw.newLine();
                bw.write(String.valueOf(boxLength));
                bw.newLine();
                createRandomParticlesFile(bw, boxLength / 10, "%.4f");
                bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
