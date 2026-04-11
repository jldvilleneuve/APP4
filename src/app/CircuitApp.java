package app;

import electronique.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Stream;

public class CircuitApp {

    public CircuitApp(String[] args) {
        System.out.println("Calculatrice de résistance d'un circuit électrique par Justin Villeneuve");
        System.out.println("Dossier actuel: " + Path.of("").toAbsolutePath().toString());

        ArrayList<ParsedCircuit> circuits = null;
        while (circuits == null) {
            try {
                circuits = fetchAllCircuits(args);
            } catch (IOException e) {
            }
        }

        while (cliCommand(circuits));
    }

    // Affiche le résultat d'un circuit que l'utilisateur choisit. Retourne
    // TRUE si l'utilisateur demande de calculer un autre circuit.
    private static boolean cliCommand(ArrayList<ParsedCircuit> circuits) {
        String[] options = getCircuitOptions(circuits);

        int chosenComposantIndex = askSelectFromOptions(
                "Choisissez le circuit dont vous souhaitez calculer la résistance parmi ces options en inscrivant l'index numérique.",
                options
        );

        ParsedCircuit parsedCircuit = circuits.get(chosenComposantIndex);
        Composant circuitComposant = parsedCircuit.getComposant();
        Path circuitPath = parsedCircuit.getPath();
        System.out.println();
        System.out.printf("Résistance du circuit '%s' = %.2f Ω\n",
                circuitPath.toString(), circuitComposant.calculerResistance());

        int optionChosenRerun = askSelectFromOptionsChar(
                "Relancer le cycle de sélection pour tester un autre fichier.",
                "Quitter l'application."
        );

        return optionChosenRerun == 0;
    }

    // Retourne un tableau contenant toutes les options de circuits que l'utilisateur a.
    private static String[] getCircuitOptions(ArrayList<ParsedCircuit> circuits) {
        String[] options = new String[circuits.size()];
        for (int i = 0; i < circuits.size(); i++) {
            options[i] = circuits.get(i).getPath().toString();
        }
        return options;
    }

    // Lit et "parse" tous les circuits dans le dossier.
    private static ArrayList<ParsedCircuit> fetchAllCircuits(String[] args)
        throws IOException
    {
        String dirPath = fetchDirPath(args);
        ArrayList<ParsedCircuit> circuits = new ArrayList<>();
        try {
            ArrayList<Path> allCircuitFiles = enumerateDirectory(dirPath, ".json");

            for (Path circuitPath : allCircuitFiles) {
                String fileContents = Files.readString(circuitPath);

                Composant composant = CircuitBuilder.construireCircuit(fileContents);
                circuits.add(new ParsedCircuit(circuitPath, composant));
            }

        } catch (IOException e) {
            System.out.println("Le chemin spécifié n'a pas plus être trouvé, ou contient des fichiers en utilisation.\n" +
                    "Chemin absolu: '" + Path.of(dirPath).toAbsolutePath().toString() + "'.\n" +
                    "Exception: " + e.getMessage());
            throw e;
        }

        return circuits;
    }

    // Demande à l'utilisateur le dossier dans lequel se trouve ses circuits.
    // Cette méthode vérifie également si l'utilisateur n'a pas spécifié un
    // chemin de dossier en argument.
    private static String fetchDirPath(String[] args) {
        String dirPath;
        if (args.length > 1) {
            System.out.println(
                    "Veuillez spécifier un seul argument." +
                            "Si vous souhaitez spécifier un chemin contenant des espaces, " +
                            "utilisez des guillemets: \"<chemin vers le dossier>\"."
            );

            System.exit(-1);
            throw new AssertionError("unreachable");

        } else if (args.length == 1) {
            dirPath = args[0];
        } else {
            dirPath = askString("Veuillez spécifier le chemin vers le dossier contenant les circuits.");
        }

        return dirPath;
    }

    // Enumère les fichiers dans un dossier, de façon récursive à travers les dossiers. Cherche
    // pour tous les fichiers finissant avec une extension spécifique.
    private static ArrayList<Path> enumerateDirectory(String dirPathStr, String expectedFileExt)
        throws IOException
    {
        ArrayList<Path> allFiles = new ArrayList<>();
        Path dirPath = Path.of(dirPathStr);

        if (!Files.exists(dirPath)) {
            throw new IOException("directory not found");
        }

        try (Stream<Path> pathStream =
                     Files.walk(dirPath).filter(path -> path.toString().endsWith(expectedFileExt)))
        {
            pathStream.forEach(allFiles::add);
        }

        return allFiles;
    }

    // Demande à l'utilisateur de choisir une valeur parmi plusieurs options, en forme de Strings.
    private static int askSelectFromOptions(String question, String[] options) {
        OptionalInt selected = OptionalInt.empty();
        while (selected.isEmpty()) {
            System.out.println();
            for (int i = 0; i < options.length; i++) {
                System.out.printf("[%d]: %s\n", (i + 1), options[i]);
            }

            System.out.println();
            selected = askInt(question);

            if (selected.isPresent() && (selected.getAsInt() > options.length || selected.getAsInt() < 1)) {
                System.out.printf("\nErreur: veuillez entrez une valeur entre 1 et %d.\n", options.length);
                selected = OptionalInt.empty();
            }
        }

        return selected.getAsInt() - 1;
    }

    // Demande à l'utilisateur de choisir une valeur parmi les options, en forme de Strings,
    // selon le premier caractère du String.
    private static int askSelectFromOptionsChar(String... options) {
        OptionalInt selected = OptionalInt.empty();
        while (selected.isEmpty()) {
            System.out.println();
            for (String option : options) {
                if (option.isEmpty()) {
                    throw new InvalidParameterException("Les options ne doivent pas être vides.");
                }

                System.out.printf("[%c]: %s\n", Character.toUpperCase(option.charAt(0)), option);
            }

            Optional<Character> selectedCharBoxed = askCharCaseInsensitive("Choisissez une option parmi les options suivantes:");
            if (selectedCharBoxed.isEmpty()) {
                continue;
            }

            char selectedChar = selectedCharBoxed.get();
            for (int i = 0; i < options.length; i++) {
                if (Character.toLowerCase(selectedChar) == Character.toLowerCase(options[i].charAt(0))) {
                    selected = OptionalInt.of(i);
                }
            }
        }

        return selected.getAsInt();
    }

    // Demande une question à l'utilisateur. S'attend à trouver un entier;
    // sinon, elle va retourner OptionalInt.empty().
    private static OptionalInt askInt(String question) {
        String text = askString(question);

        try {
            return OptionalInt.of(Integer.parseInt(text));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    // Demande une question à l'utilisateur. S'attend à trouver un seul
    // caractère. Cette méthode ignore la case.
    private static Optional<Character> askCharCaseInsensitive(String question) {
        String text = askString(question);

        if (text.length() != 1) {
            return Optional.empty();
        }

        return Optional.of(text.charAt(0));
    }

    // Demande une question à l'utilisateur. Retourne la réponse en String.
    private static String askString(String question) {
        System.out.print(question.trim() + " ");
        Scanner stdin = new Scanner(System.in);
        String line = stdin.nextLine();
        return line.trim();
    }

    static void main(String[] args) {
        new CircuitApp(args);
    }
}
