package electronique;

import java.util.ArrayList;

public abstract class Circuit implements Composant {

    private final ArrayList<Composant> composants;

    public Circuit(ArrayList<Composant> composants) {
        this.composants = composants;
    }

    protected ArrayList<Composant> getComposants() {
        return composants;
    }
}
