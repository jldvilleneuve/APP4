package electronique;

import java.util.ArrayList;

public class CircuitParallele extends Circuit {
    public CircuitParallele(ArrayList<Composant> composants) {
        super(composants);
    }

    @Override
    public double calculerResistance() {
        double sum = 0;
        for (Composant c : this.getComposants()) {
            sum += 1.0 / c.calculerResistance();
        }

        return sum != 0.0 ? 1.0 / sum : 0.0;
    }
}
