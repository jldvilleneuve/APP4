package electronique;

import java.security.InvalidParameterException;

public class Resistance implements Composant {
    private double resistance;

    public Resistance(double resistanceOhms) {
        this.setResistance(resistanceOhms);
    }

    public double getResistance() {
        return resistance;
    }

    public void setResistance(double resistance) {
        if (resistance < 0) {
            throw new InvalidParameterException("La résistance doit être supérieure à 0.");
        }
        this.resistance = resistance;
    }

    @Override
    public double calculerResistance() {
        return this.getResistance();
    }
}
