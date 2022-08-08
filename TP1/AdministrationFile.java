import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class AdministrationFile {

    int N;
    int boxLength;

    public AdministrationFile(){
        Random random = new Random();
        N = random.nextInt(300 - 10 + 1) + 10; //create random between 10 and 300
        boxLength= random.nextInt(15 - 3 + 1) + 3; //create random between 3 and 15
    }

    private void createRandomParticlesFile(BufferedWriter file,int multiple,String format) throws IOException {
        for (int i = 0; i < N; i++) {
            file.write(String.format(format,Math.random() * multiple));
            file.write(" ");
            file.write(String.format(format,Math.random() * multiple));
            file.newLine();
        }

    }


    public void generatorFile(){
        try {
                BufferedWriter bw = new BufferedWriter(new FileWriter("Dynamic.txt"));
                bw.write(String.valueOf(Math.random()));
                bw.newLine();
                createRandomParticlesFile(bw, boxLength, "%6.8e");
                bw.close();


                bw = new BufferedWriter(new FileWriter("Static.txt"));
                bw.write(String.valueOf(N));
                bw.newLine();
                bw.write(String.valueOf(boxLength));
                bw.newLine();
                createRandomParticlesFile(bw, boxLength, "%.4f");
                bw.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        AdministrationFile administrationFile = new AdministrationFile();
        administrationFile.generatorFile();
    }


}
