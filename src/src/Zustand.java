import java.util.ArrayList;

public class Zustand {
    int index;
    ArrayList<Uebergang> uebergaenge = new ArrayList<>();

    public Zustand(int index) {
        this.index = index;
    }
}