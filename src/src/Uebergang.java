public class Uebergang {
    int startZustand;
    int endZustand;
    int eingelesen;
    int geschrieben;
    Richtung richtung;

    public Uebergang(int startZustand, int endZustand, int eingelesen, int geschrieben, Richtung richtung) {//Todo: vielleicht Reihenfolge wie bei Übergangsfunktion?
        this.startZustand = startZustand;
        this.endZustand = endZustand;
        this.eingelesen = eingelesen;
        this.geschrieben = geschrieben;
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
            for (Richtung richtung : Richtung.values()) {
                if (richtung.getValue() == value) {
                    return richtung;
                }
            }
            return null;
        }
    }
}