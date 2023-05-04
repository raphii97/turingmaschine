import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class UTM {
    private String[] band = new String[200];
    private final String[] bandSymbol = {null, "0", "1", " ", "x", "y", "z"};
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
     * Validates the input to be binary
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
        //split user input into TM configuration and TM input
        String[] code = input.split("111");

        //fill band with TM input
        for (int i = 0; i < code[1].length(); i++) {
            band[kopfPosition + i] = String.valueOf(code[1].charAt(i));
        }

        //get TM configurations
        String[] uebergange = code[0].split("11");

        //add all Configurations to the HashMap Zustaende
        for (String s : uebergange) {
            String[] uebergang = s.split("1");

            //has to be a 5-tupel
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
     * Prints:
     * the band, 15 symbols before and after the head
     * current state
     * current head position
     * number of calculations
     */
    private void print(){
        int startBand = kopfPosition - 15;
        int endBand = kopfPosition + 16;

        System.out.println("Zustand: " + currentZustand);

        String output = "";
        for (int i = startBand; i < endBand; i++) {
            output += "| " + band[i] + " ";
        }
        output += "|";

        System.out.println("-".repeat(output.length()));
        System.out.println(output);
        System.out.println("-".repeat(output.length()));
        System.out.println(" ".repeat(output.length() / 2) + "^");

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
     * Multiplikation TM and input
     *
     * Input:
     * Unäre codierung: 0
     * Separator symbol: 1
     * z.b. 2x3 => 001000
     *
     * TM Uebergaenge:
     * (q1, 0) = (q2,  , R)
     * (q1, 1) = (q7,  , R)
     *
     * (q2, 0) = (q2, 0, R)
     * (q2, 1) = (q3, 1, R)
     *
     * (q3, 0) = (q4, x, R)
     * (q3, 1) = (q6, 1, L)
     *
     * (q4, 0) = (q4, 0, R)
     * (q4, 1) = (q4, 1, R)
     * (q4,  ) = (q5, 0, L)
     *
     * (q5, 0) = (q5, 0, L)
     * (q5, 1) = (q5, 1, L)
     * (q5, x) = (q3, x, R)
     *
     * (q6, 0) = (q6, 0, L)
     * (q6, 1) = (q6, 1, L)
     * (q6, x) = (q6, 0, L)
     * (q6,  ) = (q1,  , R)
     *
     * (q7, 1) = (q8,  , R)
     * (q7, 0) = (q7,  , R)
     *
     * TM code:
     * 0101001000100
     * 0100100000001000100
     *
     * 001010010100
     * 001001000100100
     *
     * 000101000010000100
     * 000100100000010010
     *
     * 0000101000010100
     * 00001001000010010
     * 000010001000001010
     *
     * 00000101000001010
     * 0000010010000010010
     * 0000010000100010000100
     *
     * 0000001010000001010
     * 000000100100000010010
     * 0000001000010000001010
     * 0000001000101000100
     *
     * 00000001001000000001000100
     * 000000010100000001000100
     *
     * TM code merged:
     * 010100100010011010010000000100010011001010010100110010010001001001100010100001000010011000100100000010010110000101000010100110000100100001001011000010001000001010110000010100000101011000001001000001001011000001000010001000010011000000101000000101011000000100100000010010110000001000010000001010110000001000101000100110000000100100000000100010011000000010100000001000100
     *
     * Test cases:
     * 2*4 => 0010000
     * 13*17 => 0000000000000100000000000000000
     * 1*27 => 01000000000000000000000000000
     * 23*0 => 000000000000000000000001
     *
     * Test cases with TM code:
     * 0101001000100110100100000001000100110010100101001100100100010010011000101000010000100110001001000000100101100001010000101001100001001000010010110000100010000010101100000101000001010110000010010000010010110000010000100010000100110000001010000001010110000001001000000100101100000010000100000010101100000010001010001001100000001001000000001000100110000000101000000010001001110010000
     */
}