package iut.info.polynome;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère la persistance des polynômes (sauvegarde et chargement).
 */
public class PolynomeIO {

    private PolynomeIO() {}

    /**
     * Sauvegarde un polynôme dans un fichier selon le format spécifié.
     */
    public static void sauvegarder(Polynome p, String chemin, FormatPolynome format) throws IOException {
        StringBuilder contenu = new StringBuilder();
        
        if (format == FormatPolynome.COEFFICIENTS) {
            contenu.append("COEFFICIENTS:");
            int degre = Math.max(0, p.getDegre());
            for (int i = 0; i <= degre; i++) {
                contenu.append(p.getCoefficient(i));
                if (i < degre) contenu.append(",");
            }
        } else {
            throw new UnsupportedOperationException("La sauvegarde au format RACINES n'est supportée qu'à la création exacte par racines.");
        }
        
        Files.writeString(Paths.get(chemin), contenu.toString());
    }

    /**
     * Charge un polynôme depuis un fichier texte formaté.
     */
    public static Polynome charger(String chemin) throws IOException {
        String contenu = Files.readString(Paths.get(chemin)).trim();
        
        if (contenu.startsWith("COEFFICIENTS:")) {
            String[] parts = contenu.substring(13).split(",");
            List<Monome> monomes = new ArrayList<>();
            for (int i = 0; i < parts.length; i++) {
                double c = Double.parseDouble(parts[i].trim());
                if (c != 0.0) {
                    monomes.add(new Monome(c, i));
                }
            }
            return new Polynome(monomes);
            
        } else if (contenu.startsWith("RACINES:")) {
            String data = contenu.substring(8);
            String[] splitMain = data.split(":");
            double coeffDom = Double.parseDouble(splitMain[0].trim());
            
            if (splitMain.length > 1 && !splitMain[1].isEmpty()) {
                String[] rootsStr = splitMain[1].split(",");
                double[] racines = new double[rootsStr.length];
                int[] mults = new int[rootsStr.length];
                
                for (int i = 0; i < rootsStr.length; i++) {
                    String[] rm = rootsStr[i].split("/");
                    racines[i] = Double.parseDouble(rm[0].trim());
                    mults[i] = Integer.parseInt(rm[1].trim());
                }
                return new Polynome(racines, mults, coeffDom);
            } else {
                return new Polynome(List.of(new Monome(coeffDom, 0)));
            }
        }
        throw new IllegalArgumentException("Format de fichier non reconnu ou corrompu.");
    }
}