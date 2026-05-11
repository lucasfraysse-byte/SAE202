package iut.info.polynome;

import java.util.Scanner;

/**
 * Interface terminale de la bibliothèque IR[X].
 * Permet de saisir un polynôme en format naturel et d'en afficher les informations.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("==============================");
        System.out.println("   Bibliotheque IR[X] v1.0");
        System.out.println("==============================");
        System.out.println("Format accepte : x^2 + 3x - 5   |   x^30+x^9");
        System.out.println("Taper 'fin' pour quitter.");
        System.out.println();

        while (true) {
            System.out.print("Entrer un polynome : ");
            String ligne = scanner.nextLine().trim();

            if (ligne.equalsIgnoreCase("fin")) {
                System.out.println("Au revoir !");
                break;
            }

            try {
                Polynome p = Polynome.parser(ligne);
                System.out.println("  P(X) = " + p);
                System.out.println("  Degre  : " + p.getDegre());
                System.out.println("  Termes : " + p.getTermes().size());
                System.out.println();
            } catch (IllegalArgumentException e) {
                System.out.println("  Erreur : " + e.getMessage());
                System.out.println();
            }
        }

        scanner.close();
    }
}
