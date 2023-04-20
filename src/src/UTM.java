import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class UTM {
    private String[] band = new String[200];
    private final String[] bandSymbol = {null, "0", "1", " "};
    private int kopfPosition = 99; //half of the band
    private int currentZustand = 1;
    private String input;
    private HashMap<Integer, Zustand> zustaende = new HashMap();
    private int counter = 0;

    /**
     * Gets the input from the user
     */
    private void getInput() {
        System.out.println("Enter a binary string: ");
        input = new Scanner(System.in).nextLine();
        if (input.startsWith("1")) input = input.substring(1); //remove prefix 1
    }

    /**
     * Validates the input
     * @return true if the input is valid
     */
    private boolean validateInput() {
        return input.matches("[01]+");
    }

    /**
     * Converts the input to a configuration
     * 5-Tupel: startZustand, eingabe, endZustand, ausgabe, richtung
     */
    private void convertInputToConfiguration() {
        String[] uebergange = input.split("11");

        for (String s : uebergange) {
            String[] uebergang = s.split("1");
            if (uebergang.length != 5) {
                System.out.println("Invalid input");
                return;
            }
            int startZustand = uebergang[0].length();
            int eingabe = uebergang[1].length();
            int endZustand = uebergang[2].length();
            int ausgabe = uebergang[3].length();
            Uebergang.Richtung richtung = Uebergang.Richtung.getRichtung(uebergang[4].length());

            if (zustaende.get(startZustand) == null) zustaende.put(startZustand, new Zustand(startZustand));
            if (zustaende.get(endZustand) == null) zustaende.put(endZustand, new Zustand(endZustand));

            zustaende.get(startZustand).uebergaenge.add(new Uebergang(startZustand, endZustand, eingabe, ausgabe, richtung));
        }
    }

    /**
     * Runs the UTM
     */
    private boolean step(Modus stepMode) {
        Zustand zustand = zustaende.get(currentZustand);
        String eingabe = band[kopfPosition];
        boolean continueRunning = false;

        for (Uebergang uebergang : zustand.uebergaenge) {
            if (bandSymbol[uebergang.eingabe].equals(eingabe)) {
                counter++;
                continueRunning = true;
                band[kopfPosition] = bandSymbol[uebergang.ausgabe];
                currentZustand = uebergang.endZustand;
                kopfPosition += uebergang.richtung.move();
                if (stepMode.getModus()) print();
                break;
            }
        }

        return continueRunning;
    }

    /**
     * Prints the band
     */
    private void print(){
        int startBand = kopfPosition - 15;
        int endBand = kopfPosition + 15;

        System.out.println("Zustand: " + currentZustand);

        String output = "";
        for (int i = startBand; i < endBand; i++) {
            output += "| " + band[i] + " ";
        }
        output += "|";

        System.out.println("-".repeat(output.length()));
        System.out.println(output);
        System.out.println("-".repeat(output.length()));

        System.out.println("Kopfposition: " + kopfPosition);

        System.out.println("Berechnungen: " + counter);
        System.out.println();
    }

    /**
     * Starts the UTM
     */
    public void start(Modus stepMode) {
        Arrays.fill(band, " ");

        do {
            getInput();
        } while (!validateInput());

        convertInputToConfiguration();
        print();

        while (step(stepMode));
    }

    /**
     * The enum for the Mode
     */
    private enum Modus {
        STEP(true),
        LAUF(false);

        private final boolean modus;

        Modus(boolean modus) {
            this.modus = modus;
        }

        public boolean getModus() {
            return modus;
        }
    }

    /**
     * The main method
     * @param args default parameter
     */
    public static void main(String[] args) {
        new UTM().start(Modus.STEP);
    }

    /**
     * Test cases
     */
    //010010001010011000101010010110001001001010011000100010001010
    //1010010100100110101000101001100010010100100110001010010100
}