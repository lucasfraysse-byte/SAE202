package iut.info.polynome;

import java.util.Scanner;

/**
 * Interface terminale de la bibliothèque IR[X] — prototype fin d'itération 2.
 *
 * <p>Permet de saisir des polynômes en format naturel et d'exercer l'ensemble
 * des fonctionnalités disponibles : informations, opérations arithmétiques
 * (addition, multiplication, division euclidienne, produit scalaire) et
 * interrogation du coefficient d'un terme.</p>
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
                    afficherCoefficient(scanner);
                    break;
                case "3":
                    effectuerAddition(scanner);
                    break;
                case "4":
                    effectuerMultiplication(scanner);
                    break;
                case "5":
                    effectuerDivision(scanner);
                    break;
                case "6":
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
        System.out.println("  2. Obtenir le coefficient d'un terme");
        System.out.println("  3. Additionner deux polynomes");
        System.out.println("  4. Multiplier deux polynomes");
        System.out.println("  5. Diviser deux polynomes (division euclidienne)");
        System.out.println("  6. Multiplier un polynome par un scalaire");
        System.out.println("  0. Quitter");
    }

    // ── Actions du menu ───────────────────────────────────────────────────────

    /**
     * Saisit un polynôme et affiche sa représentation, son degré, son nombre
     * de termes et s'il est nul.
     *
     * @param scanner le scanner de la console
     */
    private static void afficherInfosPolynome(Scanner scanner) {
        Polynome p = saisirPolynome(scanner, "P");
        if (p == null) {
            return;
        }
        System.out.println("  p(x)    = " + p);
        System.out.println("  Degre   : " + p.getDegre());
        System.out.println("  Termes  : " + p.getTermes().size());
        System.out.println("  Est nul : " + p.estNul());
    }

    /**
     * Saisit un polynôme et un exposant, puis affiche le coefficient
     * du terme correspondant (0.0 si absent).
     *
     * @param scanner le scanner de la console
     */
    private static void afficherCoefficient(Scanner scanner) {
        Polynome p = saisirPolynome(scanner, "P");
        if (p == null) {
            return;
        }
        System.out.print("  Exposant du terme recherche : ");
        String ligneExp = scanner.nextLine().trim();
        try {
            int exp = Integer.parseInt(ligneExp);
            if (exp < 0) {
                System.out.println("  Erreur : l'exposant doit etre positif ou nul.");
                return;
            }
            double coeff = p.getCoefficient(exp);
            System.out.println("  p(x) = " + p);
            System.out.println("  Coefficient de x^" + exp + " : " + formatNombre(coeff));
        } catch (NumberFormatException e) {
            System.out.println("  Erreur : exposant invalide, veuillez entrer un entier.");
        }
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
        System.out.println("  p(x)         = " + p);
        System.out.println("  q(x)         = " + q);
        System.out.println("  p(x) + q(x)  = " + somme);
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
        System.out.println("  p(x)         = " + p);
        System.out.println("  q(x)         = " + q);
        System.out.println("  p(x) * q(x)  = " + produit);
    }

    /**
     * Saisit deux polynômes, effectue la division euclidienne du premier par
     * le second et affiche le quotient, le reste et la vérification.
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
            System.out.println("  p(x)      = " + p);
            System.out.println("  q(x)      = " + q);
            System.out.println("  Quotient  : " + resultat[0]);
            System.out.println("  Reste     : " + resultat[1]);
            System.out.println("  Verification (q * quotient + reste) : "
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
            System.out.println("  p(x)             = " + p);
            System.out.println("  Scalaire         = " + formatNombre(scalaire));
            System.out.println("  p(x) * scalaire  = " + resultat);
        } catch (NumberFormatException e) {
            System.out.println("  Erreur : scalaire invalide, veuillez entrer un nombre reel.");
        } catch (IllegalArgumentException e) {
            System.out.println("  Erreur : " + e.getMessage());
        }
    }

    // ── Utilitaires ───────────────────────────────────────────────────────────

    /**
     * Invite l'utilisateur à saisir un polynôme et retourne le polynôme parsé,
     * ou {@code null} en cas de saisie invalide.
     *
     * @param scanner le scanner de la console
     * @param nom     le nom du polynôme affiché dans l'invite (ex. {@code "P"})
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

    /**
     * Formate un nombre réel : entier si la valeur est entière, décimal sinon.
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
}