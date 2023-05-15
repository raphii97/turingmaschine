import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class UniverselleTuringmaschine {
    private String[] band = new String[500];
    private final String[] bandSymbole = {null, "0", "1", " ", "x"}; //x ist Marker
    private int kopfPosition = 50; // mehr als 15 aber weniger als hälfte der bandlänge, damit grosse Zahlen auch funktionieren, 50 sicher genug
    private int momentanerZustand = 1;
    private String turingMachineInBinary;//ganze Turingmaschine in Binärrepräsentation
    private HashMap<Integer, Zustand> zustaende = new HashMap(); //Die Zustandsnummer gibt einem das Zustandsobjekt zurück
    private int berechnungsschritt = 0;
    private static final String TURING_CODE = "010100100001001101001000000010001001100101001010011001001000100100110001010000100001001100010010000001001011000010100001010011000010010000100100110000100010000010101100000101000001010110000010010000010010110000010000100010100110000001010000001010110000001001000000100101100000010000101000100110000000101000000010001001100000001001000000001000100";

    /**
     * Gets the whole binary encoded Turing Machine from the user
     */
    private void getTuringMachineInBinaryFormInput() {
        System.out.println("Enter a binary string: ");
        turingMachineInBinary = new Scanner(System.in).nextLine();
        if (turingMachineInBinary.contains("*")) {
            turingMachineInBinary = translate(turingMachineInBinary);
        }
        else if (turingMachineInBinary.startsWith("1")) turingMachineInBinary = turingMachineInBinary.substring(1); //remove prefix 1
    }

    /**
     * Validates the input to be binary and gives feedback, if the String provided is empty
     * @return true if the input is valid
     */
    private boolean validateInput() {
        if(turingMachineInBinary.length()==0){
            System.out.println("Can not process the Turing Machine without any binary code. Please enter your Turing " +
                    "machine in binary");
        }
        return turingMachineInBinary.matches("[01]+");
    }

    /**
     * Translates the decimal input to unary binary representation
     * @param input the input provided by the user
     */
    public static String translate(String input) {
        String[] numbers = input.split("\\*");
        int firstNumber = Integer.parseInt(numbers[0]);
        int secondNumber = Integer.parseInt(numbers[1]);
        return "0".repeat(firstNumber) + "1" + "0".repeat(secondNumber) + "1";
    }

    /**
     * Converts the binary input to an executable Turing Machine
     * The transmissions need to be in the following form:
     * 5-Tupel: startZustand, eingabe, endZustand, ausgabe, richtung
     */
    private void setUpTuringMachineBasedOnBinaryInput() {
        //split user input into TM configuration and TM input
        String[] turingMachineAndInput = new String[2];

        if (turingMachineInBinary.contains("111")) {
            turingMachineAndInput = turingMachineInBinary.split("111");
        } else {
            turingMachineAndInput[0] = TURING_CODE;
            turingMachineAndInput[1] = turingMachineInBinary;
        }

        //fill band with Turing Machine input
        fillBandWith(turingMachineAndInput[1]);

        //Übergänge sind getrennt durch doppelte 1-en
        String[] uebergaenge = turingMachineAndInput[0].split("11");

        //add all transmissions to the HashMap Zustaende
        for (String uebergang : uebergaenge) {
            String[] uebergangsfunktionsTeile = uebergang.split("1");

            //has to be a 5-tupel
            if (!(hasFiveElements(uebergangsfunktionsTeile))) {return;}

            int startZustand = uebergangsfunktionsTeile[0].length(); //in decimal übersetzt
            int eingelesen = uebergangsfunktionsTeile[1].length();//in decimal übersetzt
            int endZustand = uebergangsfunktionsTeile[2].length();//in decimal übersetzt
            int neuGeschrieben = uebergangsfunktionsTeile[3].length();// holt decimal Repräsentation des Zeichens
            Uebergang.Richtung richtung = Uebergang.Richtung.getRichtung(uebergangsfunktionsTeile[4].length());// Anzahl Nullen in Integer übersetzt
            //neue Zustände werden initialisiert
            if (zustaende.get(startZustand) == null) zustaende.put(startZustand, new Zustand(startZustand));
            if (zustaende.get(endZustand) == null) zustaende.put(endZustand, new Zustand(endZustand));//Falls ein Zustand keine Übergänge mehr hat, wird er sonst nicht initialisiert

            zustaende.get(startZustand).uebergaenge.add(new Uebergang(startZustand, endZustand, eingelesen, neuGeschrieben, richtung)); //fügt der Liste der Übergänge des entsprechenden Zustands, den neuen Übergang hinzu
        }
    }

    private void fillBandWith(String input){
        for (int i = 0; i < input.length(); i++) {
            band[kopfPosition + i] = String.valueOf(input.charAt(i));
        }
    }

    private boolean hasFiveElements(String[] uebergangsfunktionsTeile){
        if (uebergangsfunktionsTeile.length != 5) {
            System.out.println("Invalid input");
            return false;
        }
        return true;
    }
    /**
     * Goes through one calculation step of the Turing machine
     */
    private boolean calculationStep(Modus stepMode) {
        Zustand zustand = zustaende.get(momentanerZustand); // Fängt mit Keyvalue 1 an
        String eingabe = band[kopfPosition]; // Fängt bei 50 an
        boolean continueRunning = false;

        for (Uebergang uebergang : zustand.uebergaenge) {//geht durch alle Übergänge durch des momentanen Zustands
            if (bandSymbole[uebergang.eingelesen].equals(eingabe)) { // liest Integer Repräsentation des Zeichens ein und vergleicht, welcher Übergang passt
                berechnungsschritt++;
                continueRunning = true;
                band[kopfPosition] = bandSymbole[uebergang.geschrieben]; //Band wird neuer Wert eingetragen
                momentanerZustand = uebergang.endZustand;// Zustand wird aktualisiert
                kopfPosition += uebergang.richtung.move();//Lese-/ Schreib-Kopf wird verschoben, wie im Übergang angegeben
                if (stepMode.getModus()) printBandAroundHead();
                break;
            }
        }

        return continueRunning;//False, falls kein weiterer passender Übergang gefunden werden kann
    }

    /**
     * Prints:
     * the band, 15 symbols before and after the head
     * current state
     * current head position
     * number of calculations
     */
    private void printBandAroundHead(){
        int startBand = kopfPosition - 15;
        int endBand = kopfPosition + 16;

        System.out.println("Zustand: " + momentanerZustand);

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

        System.out.println("Berechnungen: " + berechnungsschritt);
        System.out.println();
    }

    /**
     * Starts the Universal Turing Machine
     */
    private void start(Modus stepMode) {
        Arrays.fill(band, " "); // Füllt das Band mit leerzeichen

        do {
            getTuringMachineInBinaryFormInput();
        } while (!validateInput());//ist Input nur 0en und 1en

        setUpTuringMachineBasedOnBinaryInput();
        if (stepMode.getModus()) printBandAroundHead();

        while (calculationStep(stepMode));

        if (!stepMode.getModus()) printBandAroundHead();
        System.out.println("Ergebnis: " + (Arrays.toString(band)).replaceAll("[^0]", "").length());//Am Schluss werden alle nicht Null-Zeichen vom Band entfernt und die String-Länge genommen, welch gleich der Anzahl Nullen sein sollte, dies gibt einem die Dezimal Repräsentation der Zahl
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
     * The main method which starts the whole application
     * @param args default parameter
     */
    public static void main(String[] args) {
        new UniverselleTuringmaschine().start(Modus.LAUF);
    }

    /**
     Multiplikation TM and input

     Input:
     Unäre codierung: 0
     Separator symbol: 1
     z.B. 2x3 => 0010001

     Test cases:
     2*4 => 00100001
     13*17 => 00000000000001000000000000000001
     1*27 => 010000000000000000000000000001
     23*0 => 0000000000000000000000011




     TM code:
     (q1, 0) = (q2, x, R)
     (q1, 1) = (q7,  , R)

     (q2, 0) = (q2, 0, R)
     (q2, 1) = (q3, 1, R)

     (q3, 0) = (q4, x, R)
     (q3, 1) = (q6, 1, L)

     (q4, 0) = (q4, 0, R)
     (q4, 1) = (q4, 1, R)
     (q4,  ) = (q5, 0, L)

     (q5, 0) = (q5, 0, L)
     (q5, 1) = (q5, 1, L)
     (q5, x) = (q3, 0, R)

     (q6, 0) = (q6, 0, L)
     (q6, 1) = (q6, 1, L)
     (q6, x) = (q1,  , R)

     (q7, 0) = (q7,  , R)
     (q7, 1) = (q8,  , R)

     TM code translated to binary:
     01010010000100
     0100100000001000100

     001010010100
     001001000100100

     000101000010000100
     000100100000010010

     0000101000010100
     000010010000100100
     000010001000001010

     00000101000001010
     0000010010000010010
     0000010000100010100

     0000001010000001010
     000000100100000010010
     00000010000101000100

     000000010100000001000100
     00000001001000000001000100

     TM code merged:
     010100100001001101001000000010001001100101001010011001001000100100110001010000100001001100010010000001001011000010100001010011000010010000100100110000100010000010101100000101000001010110000010010000010010110000010000100010100110000001010000001010110000001001000000100101100000010000101000100110000000101000000010001001100000001001000000001000100



     TM code with input:

     2*4:
     01010010000100110100100000001000100110010100101001100100100010010011000101000010000100110001001000000100101100001010000101001100001001000010010011000010001000001010110000010100000101011000001001000001001011000001000010001010011000000101000000101011000000100100000010010110000001000010100010011000000010100000001000100110000000100100000000100010011100100001

     13*17:
     01010010000100110100100000001000100110010100101001100100100010010011000101000010000100110001001000000100101100001010000101001100001001000010010011000010001000001010110000010100000101011000001001000001001011000001000010001010011000000101000000101011000000100100000010010110000001000010100010011000000010100000001000100110000000100100000000100010011100000000000001000000000000000001

     1*27:
     010100100001001101001000000010001001100101001010011001001000100100110001010000100001001100010010000001001011000010100001010011000010010000100100110000100010000010101100000101000001010110000010010000010010110000010000100010100110000001010000001010110000001001000000100101100000010000101000100110000000101000000010001001100000001001000000001000100111010000000000000000000000000001

     23*0:
     0101001000010011010010000000100010011001010010100110010010001001001100010100001000010011000100100000010010110000101000010100110000100100001001001100001000100000101011000001010000010101100000100100000100101100000100001000101001100000010100000010101100000010010000001001011000000100001010001001100000001010000000100010011000000010010000000010001001110000000000000000000000011
    */
}