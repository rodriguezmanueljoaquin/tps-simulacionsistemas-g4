import java.io.*;
import java.util.*;

public class AdministrationFile {

    private int particlesQuantity;

    public AdministrationFile(){
        particlesQuantity = Constants.PARTICLES_QUANTITY;
    }

    private void createRandomParticlesFile(BufferedWriter file,double multiple,String format) throws IOException {
        for (int i = 0; i < particlesQuantity; i++) {
            file.write(String.format(Locale.ENGLISH,format,Math.random() * multiple));
            file.write(" ");
            file.write(String.format(Locale.ENGLISH,format,Math.random() * multiple));
            file.newLine();
        }
    }

    public void generatorFile(boolean generateStaticFile){
        try {
                BufferedWriter bw = new BufferedWriter(new FileWriter("Dynamic.txt"));
                bw.write(String.valueOf(0)); // tiempo = 0
                bw.newLine();
                createRandomParticlesFile(bw, Constants.BOX_LENGTH, "%6.8e");
                bw.close();

                if(generateStaticFile){
                    bw = new BufferedWriter(new FileWriter("Static.txt"));
                    bw.write(String.valueOf(particlesQuantity));
                    bw.newLine();
                    bw.write(String.valueOf(Constants.BOX_LENGTH));
                    bw.newLine();
                    createRandomParticlesFile(bw, Constants.MAX_PARTICLE_RADIUS, "%.4f");
                    bw.close();
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
