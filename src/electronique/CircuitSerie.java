package electronique;

import java.util.ArrayList;

public class CircuitSerie extends Circuit {
    public CircuitSerie(ArrayList<Composant> composants) {
        super(composants);
    }

    @Override
    public double calculerResistance() {
        double sum = 0;
        for (Composant c : this.getComposants()) {
            sum += c.calculerResistance();
        }

        return sum;
    }
}
