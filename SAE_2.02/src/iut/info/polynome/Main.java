package iut.info.polynome;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    private Main() {}

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        afficherBienvenue();

        boolean continuer = true;
        while (continuer) {
            afficherMenu();
            System.out.print("Votre choix : ");
            String choix = scanner.nextLine().trim();

            try {
                switch (choix) {
                    case "1" -> afficherInfosPolynome(scanner);
                    case "2" -> effectuerAddition(scanner);
                    case "3" -> effectuerMultiplication(scanner);
                    case "4" -> effectuerDivision(scanner);
                    case "5" -> evaluerPolynome(scanner);
                    case "6" -> analyserPolynome(scanner);
                    case "7" -> analyserRacinesSturm(scanner);
                    case "8" -> gererPersistance(scanner);
                    case "0" -> {
                        System.out.println("Au revoir !");
                        continuer = false;
                    }
                    default -> System.out.println("Choix invalide.");
                }
            } catch (Exception e) {
                System.out.println("  Erreur inattendue : " + e.getMessage());
            }
            System.out.println();
        }
        scanner.close();
    }

    private static void afficherBienvenue() {
        System.out.println("==============================");
        System.out.println("   Bibliotheque IR[X] v3.0");
        System.out.println("==============================");
    }

    private static void afficherMenu() {
        System.out.println("--- Menu ---");
        System.out.println("  1. Afficher infos (Identite, Limites)");
        System.out.println("  2. Additionner");
        System.out.println("  3. Multiplier");
        System.out.println("  4. Diviser (Euclidienne)");
        System.out.println("  5. Evaluer (Horner)");
        System.out.println("  6. Analyser (Derivee, Primitive, Moyenne)");
        System.out.println("  7. Racines (Suite de Sturm)");
        System.out.println("  8. Fichiers (Sauvegarder / Charger)");
        System.out.println("  0. Quitter");
    }

    private static void afficherInfosPolynome(Scanner scanner) {
        Polynome p = saisirPolynome(scanner, "P");
        if (p == null) return;
        System.out.println("  p(x)    = " + p);
        System.out.println("  Degre   : " + p.getDegre());
        System.out.println("  Lim -oo : " + p.getLimiteEnMoinsInfini());
        System.out.println("  Lim +oo : " + p.getLimiteEnPlusInfini());
    }

    private static void effectuerAddition(Scanner scanner) {
        Polynome p = saisirPolynome(scanner, "P");
        Polynome q = saisirPolynome(scanner, "Q");
        if (p != null && q != null) System.out.println("  Somme = " + p.additionner(q));
    }

    private static void effectuerMultiplication(Scanner scanner) {
        Polynome p = saisirPolynome(scanner, "P");
        Polynome q = saisirPolynome(scanner, "Q");
        if (p != null && q != null) System.out.println("  Produit = " + p.multiplier(q));
    }

    private static void effectuerDivision(Scanner scanner) {
        Polynome p = saisirPolynome(scanner, "Dividende");
        Polynome q = saisirPolynome(scanner, "Diviseur");
        if (p != null && q != null) {
            DivisionEuclidienneResultat res = p.diviser(q);
            System.out.println("  Quotient : " + res.getQuotient());
            System.out.println("  Reste    : " + res.getReste());
        }
    }

    private static void evaluerPolynome(Scanner scanner) {
        Polynome p = saisirPolynome(scanner, "P");
        if (p == null) return;
        System.out.print("  Valeur de x : ");
        double x = Double.parseDouble(scanner.nextLine().trim());
        System.out.println("  p(" + x + ") = " + p.evaluer(x));
    }

    private static void analyserPolynome(Scanner scanner) {
        Polynome p = saisirPolynome(scanner, "P");
        if (p == null) return;
        System.out.println("  Derivee p'(x)   = " + p.deriver());
        System.out.println("  Primitive P(x)  = " + p.integrer() + " + C");
        System.out.print("  Calcul moyenne - Borne A : ");
        double a = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("  Calcul moyenne - Borne B : ");
        double b = Double.parseDouble(scanner.nextLine().trim());
        System.out.println("  Moyenne sur ["+a+", "+b+"] = " + p.valeurMoyenne(a, b));
    }

    private static void analyserRacinesSturm(Scanner scanner) {
        Polynome p = saisirPolynome(scanner, "P");
        if (p == null) return;
        System.out.print("  Borne Inf (a) : ");
        double a = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("  Borne Sup (b) : ");
        double b = Double.parseDouble(scanner.nextLine().trim());
        System.out.println("  Nombre de racines sur ["+a+", "+b+"] = " + p.getNombreRacinesReelles(a, b));
    }

    private static void gererPersistance(Scanner scanner) {
        System.out.println("  1. Sauvegarder\n  2. Charger");
        System.out.print("  Choix : ");
        String choix = scanner.nextLine().trim();
        System.out.print("  Nom du fichier (ex: poly.txt) : ");
        String fichier = scanner.nextLine().trim();

        try {
            if ("1".equals(choix)) {
                Polynome p = saisirPolynome(scanner, "P à sauvegarder");
                if (p != null) {
                    PolynomeIO.sauvegarder(p, fichier, FormatPolynome.COEFFICIENTS);
                    System.out.println("  Sauvegarde réussie !");
                }
            } else if ("2".equals(choix)) {
                Polynome p = PolynomeIO.charger(fichier);
                System.out.println("  Polynome chargé : " + p);
            }
        } catch (IOException e) {
            System.out.println("  Erreur d'accès au fichier : " + e.getMessage());
        }
    }

    private static Polynome saisirPolynome(Scanner scanner, String nom) {
        System.out.print("  Entrer " + nom + "(x) : ");
        try {
            return Polynome.parser(scanner.nextLine().trim());
        } catch (IllegalArgumentException e) {
            System.out.println("  Erreur : " + e.getMessage());
            return null;
        }
    }
}