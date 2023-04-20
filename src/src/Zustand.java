import java.util.ArrayList;

public class Zustand {
    private int index;
    public ArrayList<Uebergang> uebergaenge = new ArrayList<>();

    public Zustand(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}