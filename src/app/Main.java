package app;

import electronique.CircuitParallele;
import electronique.CircuitSerie;
import electronique.Composant;
import electronique.Resistance;

import java.util.ArrayList;

public class Main {

    static void main(String[] args) {
        Resistance r1 = new Resistance(10);
        Resistance r2 = new Resistance(20);

        ArrayList<Composant> composants = new ArrayList<>();
        composants.add(r1);
        composants.add(r2);
        CircuitSerie s = new CircuitSerie(composants);
        CircuitParallele p = new CircuitParallele(composants);

        System.out.println("R1 = " + r1.calculerResistance() + " ohms");
        System.out.println("R2 = " + r2.calculerResistance() + " ohms");
        System.out.println("Série = " + s.calculerResistance() + " ohms");
        System.out.println("Parallèle = " + p.calculerResistance() + " ohms");
    }
}
