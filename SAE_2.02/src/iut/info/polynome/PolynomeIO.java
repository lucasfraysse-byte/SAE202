package iut.info.polynome;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utilitaire de persistance pour les polynômes : sauvegarde et chargement depuis des fichiers texte.
 *
 * <p>Deux formats sont définis par FormatPolynome :</p>
 * <ul>
 *   <li>COEFFICIENTS:c0,c1,...,cn — coefficients par exposant croissant.</li>
 *   <li>RACINES:coeffDom:r1/m1,... — racines avec multiplicités (lecture seule).</li>
 * </ul>
 *
 * <p>Cette classe est non instanciable (utilitaire statique).</p>
 */
public class PolynomeIO {

    private PolynomeIO() {}

    /**
     * Sauvegarde un polynôme dans un fichier texte selon le format spécifié.
     *
     * @param p      polynôme à sauvegarder
     * @param chemin chemin du fichier de destination
     * @param format format de sérialisation (FormatPolynome.COEFFICIENTS uniquement)
     * @throws IOException                   en cas d'erreur d'écriture
     * @throws UnsupportedOperationException si format == RACINES
     */
    public static void sauvegarder(Polynome p, String chemin, FormatPolynome format) throws IOException {
        Files.writeString(Paths.get(chemin), serialiserLigne(p, format));
    }

    /**
     * Charge un polynôme depuis un fichier texte formaté.
     *
     * <p>Le fichier doit contenir une seule ligne au format COEFFICIENTS:...
     * ou RACINES:....</p>
     *
     * @param chemin chemin du fichier source
     * @return le polynôme chargé
     * @throws IOException              en cas d'erreur de lecture
     * @throws IllegalArgumentException si le format du fichier est invalide
     */
    public static Polynome charger(String chemin) throws IOException {
        return chargerDepuisLigne(Files.readString(Paths.get(chemin)).trim());
    }

    /**
     * Sauvegarde une liste de polynômes dans un fichier, un polynôme par ligne.
     *
     * @param polynomes liste de polynômes à sauvegarder
     * @param chemin    chemin du fichier de destination
     * @param format    format de sérialisation ( FormatPolynome.COEFFICIENTS} uniquement)
     * @throws IOException                   en cas d'erreur d'écriture
     * @throws UnsupportedOperationException si format == RACINES
     */
    public static void sauvegarderTous(List<Polynome> polynomes, String chemin, FormatPolynome format) throws IOException {
        String contenu = polynomes.stream()
                .map(p -> serialiserLigne(p, format))
                .collect(Collectors.joining("\n"));
        Files.writeString(Paths.get(chemin), contenu);
    }

    /**
     * Charge tous les polynômes depuis un fichier texte (un polynôme par ligne non vide).
     *
     * @param chemin chemin du fichier source
     * @return liste des polynômes chargés (vide si le fichier est vide)
     * @throws IOException              en cas d'erreur de lecture
     * @throws IllegalArgumentException si une ligne a un format invalide
     */
    public static List<Polynome> chargerTous(String chemin) throws IOException {
        List<Polynome> result = new ArrayList<>();
        for (String ligne : Files.readAllLines(Paths.get(chemin))) {
            if (!ligne.isBlank()) result.add(chargerDepuisLigne(ligne));
        }
        return result;
    }

    /**
     * Sérialise un polynôme en une chaîne de caractères selon le format donné.
     *
     * @param p      polynôme à sérialiser
     * @param format format cible
     * @return ligne sérialisée
     * @throws UnsupportedOperationException si format == RACINES
     */
    public static String serialiser(Polynome p, FormatPolynome format) {
        return serialiserLigne(p, format);
    }

    /**
     * Désérialise une ligne texte en polynôme (version publique de {@link #chargerDepuisLigne}).
     *
     * @param ligne ligne au format COEFFICIENTS:... ou RACINES:...
     * @return le polynôme correspondant
     * @throws IllegalArgumentException si le format n'est pas reconnu
     */
    public static Polynome chargerLigne(String ligne) {
        return chargerDepuisLigne(ligne);
    }

    private static String serialiserLigne(Polynome p, FormatPolynome format) {
        if (format != FormatPolynome.COEFFICIENTS) {
            throw new UnsupportedOperationException("La sauvegarde au format RACINES n'est supportée qu'à la création exacte par racines.");
        }
        int degre = Math.max(0, p.getDegre());
        String coeffs = IntStream.rangeClosed(0, degre)
                .mapToObj(i -> String.valueOf(p.getCoefficient(i)))
                .collect(Collectors.joining(","));
        return "COEFFICIENTS:" + coeffs;
    }

    /**
     * Désérialise une ligne texte en polynôme.
     *
     * @param ligne ligne au format COEFFICIENTS:... ou RACINES:...
     * @return le polynôme correspondant
     * @throws IllegalArgumentException si le format n'est pas reconnu
     */
    private static Polynome chargerDepuisLigne(String ligne) {
        if (ligne.startsWith("COEFFICIENTS:")) return chargerCoefficients(ligne.substring(13));
        if (ligne.startsWith("RACINES:"))      return chargerRacines(ligne.substring(8));
        throw new IllegalArgumentException("Format de fichier non reconnu ou corrompu.");
    }

    /** Désérialise le corps d'une ligne COEFFICIENTS:... (coefficients par exposant croissant). */
    private static Polynome chargerCoefficients(String data) {
        String[] parts = data.split(",");
        List<Monome> monomes = new ArrayList<>();
        for (int i = 0; i < parts.length; i++) {
            double c = Double.parseDouble(parts[i].trim());
            if (c != 0.0) monomes.add(new Monome(c, i));
        }
        return new Polynome(monomes);
    }

    /** Désérialise le corps d'une ligne RACINES:... (coefficient dominant puis racines/multiplicités). */
    private static Polynome chargerRacines(String data) {
        String[] splitMain = data.split(":");
        double coeffDom = Double.parseDouble(splitMain[0].trim());
        if (splitMain.length <= 1 || splitMain[1].isEmpty()) {
            return new Polynome(List.of(new Monome(coeffDom, 0)));
        }
        String[] rootsStr = splitMain[1].split(",");
        double[] racines = new double[rootsStr.length];
        int[] mults = new int[rootsStr.length];
        for (int i = 0; i < rootsStr.length; i++) {
            String[] rm = rootsStr[i].split("/");
            racines[i] = Double.parseDouble(rm[0].trim());
            mults[i] = Integer.parseInt(rm[1].trim());
        }
        return new Polynome(racines, mults, coeffDom);
    }
}
