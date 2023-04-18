import java.util.HashMap;
import java.util.Scanner;

public class UTM {
    private String band = "";
    private String[] bandSymbol = {null, "0", "1", " "};
    private int kopfPosition = 0;
    private int currentZustand = 1;
    private String input;
    private HashMap<Integer, Zustand> zustaende = new HashMap<Integer, Zustand>();

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

            zustaende.get(startZustand).uebergaenge.add(new Uebergang(startZustand, endZustand, eingabe, ausgabe, richtung));
        }
    }

    /**
     * Runs the UTM
     */
    private boolean step(Modus step) {
        Zustand zustand = zustaende.get(currentZustand);
        String eingabe = bandSymbol[kopfPosition];
        boolean continueRunning = false;

        for (Uebergang uebergang : zustand.uebergaenge) {
            if (bandSymbol[uebergang.eingabe].equals(eingabe)) {
                continueRunning = true;
                bandSymbol[kopfPosition] = String.valueOf(uebergang.ausgabe);
                currentZustand = uebergang.endZustand;
                kopfPosition += uebergang.richtung.move();
                break;
            }
        }

        if (step.getModus()) print();

        return continueRunning;
    }

    /**
     * Prints the band
     */
    private void print(){
        System.out.println(band);
    }

    /**
     * Starts the UTM
     */
    public void start(Modus step) {
        do {
            getInput();
        } while (!validateInput());

        convertInputToConfiguration();

        while (step(step));
    }

    /**
     * The enum for the Mode
     */
    private enum Modus {
        STEP(true),
        LAUF(false);

        private boolean modus;

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
}