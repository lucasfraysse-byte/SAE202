package iut.info.polynome;

import java.util.Scanner;

/**
 * Interface terminale de la bibliothèque IR[X].
 *
 * <p>Permet de saisir des polynômes en format naturel et d'effectuer
 * les opérations suivantes : addition, multiplication, division euclidienne,
 * ainsi que l'affichage des caractéristiques d'un polynôme.</p>
 *
 * <p>Format d'entrée accepté : {@code x^2 + 3x - 5} | {@code x^30 + x^9}</p>
 */
public class Main {

    /** Empêche l'instanciation de cette classe utilitaire. */
    private Main() {}

    /**
     * Point d'entrée de l'application.
     *
     * @param args arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        afficherBienvenue();

        boolean continuer = true;
        while (continuer) {
            afficherMenu();
            System.out.print("Votre choix : ");
            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1":
                    afficherInfosPolynome(scanner);
                    break;
                case "2":
                    effectuerAddition(scanner);
                    break;
                case "3":
                    effectuerMultiplication(scanner);
                    break;
                case "4":
                    effectuerDivision(scanner);
                    break;
                case "5":
                    effectuerProduitScalaire(scanner);
                    break;
                case "0":
                    System.out.println("Au revoir !");
                    continuer = false;
                    break;
                default:
                    System.out.println("  Choix invalide, veuillez réessayer.");
            }
            System.out.println();
        }

        scanner.close();
    }

    // ── Affichage ─────────────────────────────────────────────────────────────

    /** Affiche le message de bienvenue. */
    private static void afficherBienvenue() {
        System.out.println("==============================");
        System.out.println("   Bibliotheque IR[X] v2.0");
        System.out.println("==============================");
        System.out.println("Format accepte : x^2 + 3x - 5   |   x^30 + x^9");
        System.out.println();
    }

    /** Affiche le menu principal. */
    private static void afficherMenu() {
        System.out.println("--- Menu ---");
        System.out.println("  1. Afficher les infos d'un polynome");
        System.out.println("  2. Additionner deux polynomes");
        System.out.println("  3. Multiplier deux polynomes");
        System.out.println("  4. Diviser deux polynomes (division euclidienne)");
        System.out.println("  5. Multiplier un polynome par un scalaire");
        System.out.println("  0. Quitter");
    }

    // ── Actions du menu ───────────────────────────────────────────────────────

    /**
     * Saisit un polynôme et affiche son degré et son nombre de termes.
     *
     * @param scanner le scanner de la console
     */
    private static void afficherInfosPolynome(Scanner scanner) {
        Polynome p = saisirPolynome(scanner, "P");
        if (p == null) {
            return;
        }
        System.out.println("  p(x)   = " + p);
        System.out.println("  Degre  : " + p.getDegre());
        System.out.println("  Termes : " + p.getTermes().size());
    }

    /**
     * Saisit deux polynômes, les additionne et affiche le résultat.
     *
     * @param scanner le scanner de la console
     */
    private static void effectuerAddition(Scanner scanner) {
        Polynome p = saisirPolynome(scanner, "P");
        if (p == null) {
            return;
        }
        Polynome q = saisirPolynome(scanner, "Q");
        if (q == null) {
            return;
        }
        Polynome somme = p.additionner(q);
        System.out.println("  p(x) = " + p);
        System.out.println("  q(x) = " + q);
        System.out.println("  p(x) + q(x) = " + somme);
    }

    /**
     * Saisit deux polynômes, les multiplie et affiche le résultat.
     *
     * @param scanner le scanner de la console
     */
    private static void effectuerMultiplication(Scanner scanner) {
        Polynome p = saisirPolynome(scanner, "P");
        if (p == null) {
            return;
        }
        Polynome q = saisirPolynome(scanner, "Q");
        if (q == null) {
            return;
        }
        Polynome produit = p.multiplier(q);
        System.out.println("  p(x) = " + p);
        System.out.println("  q(x) = " + q);
        System.out.println("  p(x) * q(x) = " + produit);
    }

    /**
     * Saisit deux polynômes, effectue la division euclidienne du premier par
     * le second et affiche le quotient et le reste.
     *
     * <p>Rappel : {@code P = Q * quotient + reste}</p>
     *
     * @param scanner le scanner de la console
     */
    private static void effectuerDivision(Scanner scanner) {
        Polynome p = saisirPolynome(scanner, "P (dividende)");
        if (p == null) {
            return;
        }
        Polynome q = saisirPolynome(scanner, "Q (diviseur)");
        if (q == null) {
            return;
        }
        try {
            Polynome[] resultat = p.diviser(q);
            System.out.println("  p(x) = " + p);
            System.out.println("  q(x) = " + q);
            System.out.println("  Quotient : " + resultat[0]);
            System.out.println("  Reste    : " + resultat[1]);
            System.out.println("  Verification : q * quotient + reste = "
                    + q.multiplier(resultat[0]).additionner(resultat[1]));
        } catch (IllegalArgumentException e) {
            System.out.println("  Erreur : " + e.getMessage());
        }
    }

    /**
     * Saisit un polynôme et un scalaire, multiplie le polynôme par le scalaire
     * et affiche le résultat.
     *
     * @param scanner le scanner de la console
     */
    private static void effectuerProduitScalaire(Scanner scanner) {
        Polynome p = saisirPolynome(scanner, "P");
        if (p == null) {
            return;
        }
        System.out.print("  Entrer le scalaire : ");
        String ligneScalaire = scanner.nextLine().trim();
        try {
            double scalaire = Double.parseDouble(ligneScalaire);
            Polynome resultat = p.multiplierParScalaire(scalaire);
            System.out.println("  p(x) = " + p);
            System.out.println("  Scalaire = " + formatNombre(scalaire));
            System.out.println("  p(x) * scalaire = " + resultat);
        } catch (NumberFormatException e) {
            System.out.println("  Erreur : scalaire invalide, veuillez entrer un nombre réel.");
        } catch (IllegalArgumentException e) {
            System.out.println("  Erreur : " + e.getMessage());
        }
    }

    /**
     * Formate un scalaire pour l'affichage : entier si valeur entière, décimal sinon.
     *
     * @param v la valeur à formater
     * @return la représentation textuelle
     */
    private static String formatNombre(double v) {
        if (v == Math.floor(v) && !Double.isInfinite(v)) {
            return String.valueOf((long) v);
        }
        return String.valueOf(v);
    }

    // ── Utilitaire de saisie ──────────────────────────────────────────────────

    /**
     * Invite l'utilisateur à saisir un polynôme et retourne le polynôme parsé.
     * En cas d'expression invalide, affiche le message d'erreur et retourne {@code null}.
     *
     * @param scanner le scanner de la console
     * @param nom     le nom du polynôme à afficher dans l'invite (ex. {@code "P"})
     * @return le polynôme parsé, ou {@code null} si la saisie est invalide
     */
    private static Polynome saisirPolynome(Scanner scanner, String nom) {
        System.out.print("  Entrer " + nom + "(x) : ");
        String ligne = scanner.nextLine().trim();
        try {
            return Polynome.parser(ligne);
        } catch (IllegalArgumentException e) {
            System.out.println("  Erreur de saisie : " + e.getMessage());
            return null;
        }
    }
}
