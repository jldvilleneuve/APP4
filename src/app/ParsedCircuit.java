package app;

import electronique.Composant;

import java.nio.file.Path;

public class ParsedCircuit {
    private final Path path;
    private final Composant composant;

    public ParsedCircuit(Path path, Composant composant) {
        this.path = path;
        this.composant = composant;
    }

    public Path getPath() {
        return path;
    }

    public Composant getComposant() {
        return composant;
    }
}
