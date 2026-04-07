package app;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import electronique.CircuitParallele;
import electronique.CircuitSerie;
import electronique.Composant;
import electronique.Resistance;

import java.util.ArrayList;

public class CircuitBuilder {

    // To static or not to static

    // Construit un circuit à partir de code JSON.
    public static Composant construireCircuit(String jsonText)
            throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jDocument = mapper.readTree(jsonText);
        JsonNode jCircuit = jDocument.get("circuit");
        return lireComposant(jCircuit);
    }

    // Construit un circuit à partir d'un node JSON.
    private static Composant lireComposant(JsonNode jNode) {
        String type = jNode.get("type").asText();

        return switch (type) {
            case "resistance" -> lireComposantResistance(jNode);
            case "parallele" -> lireCircuitParallele(jNode);
            case "serie" -> lireCircuitSerie(jNode);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    // Construit une composante résistance.
    private static Composant lireComposantResistance(JsonNode jNode) {
        return new Resistance(jNode.get("valeur").asDouble());
    }

    // Construit un circuit en parallèle.
    private static Composant lireCircuitParallele(JsonNode jNode) {
        return new CircuitParallele(lireComposantList(jNode.get("composants")));
    }

    // Construit un circuit en série.
    private static Composant lireCircuitSerie(JsonNode jNode) {
        return new CircuitSerie(lireComposantList(jNode.get("composants")));
    }

    // Construit un tableau de composantes à partir d'un node tableau.
    private static ArrayList<Composant> lireComposantList(JsonNode jArrayNode) {
        int arraySize = jArrayNode.size();
        ArrayList<Composant> out = new ArrayList<>(arraySize);

        for (int i = 0; i < arraySize; i++) {
            out.add(lireComposant(jArrayNode.get(i)));
        }

        return out;
    }
}
