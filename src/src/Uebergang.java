public class Uebergang {
    int startZustand;
    int endZustand;
    int eingabe;
    int ausgabe;
    Richtung richtung;

    public Uebergang(int startZustand, int endZustand, int eingabe, int ausgabe, Richtung richtung) {
        this.startZustand = startZustand;
        this.endZustand = endZustand;
        this.eingabe = eingabe;
        this.ausgabe = ausgabe;
        this.richtung = richtung;
    }

    enum Richtung {
        L(1), R(2);

        private final int value;

        Richtung(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
        public int move() {
            return value == 1 ? -1 : 1;
        }

        public static Richtung getRichtung(int value) {
            for (Richtung r : Richtung.values()) {
                if (r.getValue() == value) {
                    return r;
                }
            }
            return null;
        }
    }
}