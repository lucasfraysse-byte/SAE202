package iut.info.polynome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Représente un polynôme à coefficients réels de IR[X].
 * Stocké sous forme d'une liste triée par degré décroissant de {@link Monome} non nuls.
 * Le polynôme nul correspond à une liste vide.
 */
public class Polynome {

    /** Seuil en dessous duquel un coefficient est considéré comme nul. */
    private static final double EPSILON = 1e-9;

    /** Liste immuable de monômes triés par exposant décroissant. */
    private final List<Monome> termes;

    // ── Constructeur ──────────────────────────────────────────────────────────

    /**
     * Construit un polynôme à partir d'une liste de monômes.
     * Les termes de même exposant sont fusionnés et les termes quasi-nuls supprimés.
     * La liste est ensuite triée par degré décroissant.
     *
     * @param termes liste de monômes (vide pour le polynôme nul)
     * @throws IllegalArgumentException si {@code termes} est {@code null}
     */
    public Polynome(List<Monome> termes) {
        if (termes == null) {
            throw new IllegalArgumentException("La liste de monômes ne peut pas être null.");
        }
        List<Monome> liste = new ArrayList<>();
        for (Monome m : termes) {
            int i = chercherExposant(liste, m.getExposant());
            if (i >= 0) {
                double somme = liste.get(i).getCoefficient() + m.getCoefficient();
                if (Math.abs(somme) > EPSILON) {
                    liste.set(i, new Monome(somme, m.getExposant()));
                } else {
                    liste.remove(i);
                }
            } else {
                liste.add(m);
            }
        }
        trierDecroissant(liste);
        this.termes = Collections.unmodifiableList(liste);
    }

    // ── Méthodes privées utilitaires ──────────────────────────────────────────

    /**
     * Retourne l'indice du monôme ayant l'exposant donné dans la liste,
     * ou {@code -1} si aucun monôme ne possède cet exposant.
     */
    private static int chercherExposant(List<Monome> liste, int exposant) {
        for (int i = 0; i < liste.size(); i++) {
            if (liste.get(i).getExposant() == exposant) {
                return i;
            }
        }
        return -1;
    }

    /** Trie la liste par exposant décroissant (tri à bulles). */
    private static void trierDecroissant(List<Monome> liste) {
        for (int i = 0; i < liste.size() - 1; i++) {
            for (int j = 0; j < liste.size() - 1 - i; j++) {
                if (liste.get(j).getExposant() < liste.get(j + 1).getExposant()) {
                    Monome tmp = liste.get(j);
                    liste.set(j, liste.get(j + 1));
                    liste.set(j + 1, tmp);
                }
            }
        }
    }

    // ── Accesseurs ────────────────────────────────────────────────────────────

    /**
     * Retourne le degré du polynôme, ou {@code -1} si le polynôme est nul.
     *
     * @return le degré du polynôme
     */
    public int getDegre() {
        return termes.isEmpty() ? -1 : termes.get(0).getExposant();
    }

    /**
     * Retourne le coefficient du terme d'exposant donné, ou {@code 0.0} si absent.
     *
     * @param exposant l'exposant du terme recherché
     * @return le coefficient correspondant, ou {@code 0.0}
     */
    public double getCoefficient(int exposant) {
        for (Monome m : termes) {
            if (m.getExposant() == exposant) {
                return m.getCoefficient();
            }
        }
        return 0.0;
    }

    /**
     * Retourne la liste immuable des monômes triés par degré décroissant.
     *
     * @return la liste des monômes
     */
    public List<Monome> getTermes() {
        return termes;
    }

    /**
     * Retourne {@code true} si ce polynôme est le polynôme nul.
     *
     * @return {@code true} si le polynôme est nul
     */
    public boolean estNul() {
        return termes.isEmpty();
    }

    // ── Opérations arithmétiques ──────────────────────────────────────────────

    /**
     * Retourne la somme de ce polynôme et du polynôme {@code autre}.
     *
     * @param autre le polynôme à additionner (non {@code null})
     * @return un nouveau polynôme égal à {@code this + autre}
     * @throws IllegalArgumentException si {@code autre} est {@code null}
     */
    public Polynome additionner(Polynome autre) {
        if (autre == null) {
            throw new IllegalArgumentException("Le polynôme à additionner ne peut pas être null.");
        }
        List<Monome> tousLesTermes = new ArrayList<>();
        tousLesTermes.addAll(this.termes);
        tousLesTermes.addAll(autre.termes);
        return new Polynome(tousLesTermes);
    }

    /**
     * Retourne le produit de ce polynôme par le polynôme {@code autre}.
     *
     * @param autre le polynôme multiplicateur (non {@code null})
     * @return un nouveau polynôme égal à {@code this * autre}
     * @throws IllegalArgumentException si {@code autre} est {@code null}
     */
    public Polynome multiplier(Polynome autre) {
        if (autre == null) {
            throw new IllegalArgumentException("Le polynôme à multiplier ne peut pas être null.");
        }
        if (this.estNul() || autre.estNul()) {
            return new Polynome(new ArrayList<>());
        }
        List<Monome> produits = new ArrayList<>();
        for (Monome mThis : this.termes) {
            for (Monome mAutre : autre.termes) {
                produits.add(mThis.multiplier(mAutre));
            }
        }
        return new Polynome(produits);
    }

    /**
     * Retourne le produit de ce polynôme par un scalaire réel.
     *
     * @param scalaire le facteur réel (non nul)
     * @return un nouveau polynôme égal à {@code this * scalaire}
     * @throws IllegalArgumentException si {@code scalaire} est nul
     */
    public Polynome multiplierParScalaire(double scalaire) {
        if (scalaire == 0.0) {
            throw new IllegalArgumentException("Le scalaire ne peut pas être nul.");
        }
        if (this.estNul()) {
            return new Polynome(new ArrayList<>());
        }
        List<Monome> nouveauxTermes = new ArrayList<>();
        for (Monome m : this.termes) {
            nouveauxTermes.add(m.multiplierParScalaire(scalaire));
        }
        return new Polynome(nouveauxTermes);
    }

    /**
     * Effectue la division euclidienne de ce polynôme par {@code diviseur}.
     * Retourne un tableau {@code [quotient, reste]} tel que
     * {@code this = diviseur * quotient + reste}.
     *
     * @param diviseur le polynôme diviseur (non {@code null}, non nul)
     * @return un tableau {@code Polynome[2]} : {@code [0]} = quotient, {@code [1]} = reste
     * @throws IllegalArgumentException si {@code diviseur} est {@code null} ou est le polynôme nul
     */
    public Polynome[] diviser(Polynome diviseur) {
        if (diviseur == null) {
            throw new IllegalArgumentException("Le diviseur ne peut pas être null.");
        }
        if (diviseur.estNul()) {
            throw new IllegalArgumentException("La division par le polynôme nul est impossible.");
        }

        List<Monome> termesQuotient = new ArrayList<>();
        Polynome dividendeCourant = new Polynome(new ArrayList<>(this.termes));

        int degreeDiviseur      = diviseur.getDegre();
        double coeffDominantDiv = diviseur.getTermes().get(0).getCoefficient();

        while (!dividendeCourant.estNul() && dividendeCourant.getDegre() >= degreeDiviseur) {
            Monome dominantDividende = dividendeCourant.getTermes().get(0);
            double coeffPartiel    = dominantDividende.getCoefficient() / coeffDominantDiv;
            int    exposantPartiel = dominantDividende.getExposant() - degreeDiviseur;
            Monome monomePartiel   = new Monome(coeffPartiel, exposantPartiel);

            termesQuotient.add(monomePartiel);

            List<Monome> soustractionTermes = new ArrayList<>();
            for (Monome mDiv : diviseur.getTermes()) {
                Monome produit = mDiv.multiplier(monomePartiel);
                soustractionTermes.add(new Monome(-produit.getCoefficient(), produit.getExposant()));
            }
            List<Monome> nouveauxTermes = new ArrayList<>(dividendeCourant.getTermes());
            nouveauxTermes.addAll(soustractionTermes);
            dividendeCourant = new Polynome(nouveauxTermes);
        }

        return new Polynome[]{new Polynome(termesQuotient), dividendeCourant};
    }

    // ── Affichage ─────────────────────────────────────────────────────────────

    /**
     * Retourne une représentation lisible du polynôme.
     * Exemples : {@code "x^2 + 3x + 3"}, {@code "-x^2 - 3x"}, {@code "0"}.
     *
     * @return la représentation textuelle du polynôme
     */
    @Override
    public String toString() {
        if (termes.isEmpty()) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < termes.size(); i++) {
            Monome m = termes.get(i);
            double coeff = m.getCoefficient();
            int exp = m.getExposant();
            if (i == 0) {
                if (coeff < 0) {
                    sb.append("-");
                }
                sb.append(formatTerme(Math.abs(coeff), exp));
            } else {
                sb.append(coeff < 0 ? " - " : " + ");
                sb.append(formatTerme(Math.abs(coeff), exp));
            }
        }
        return sb.toString();
    }

    /**
     * Formate un terme du polynôme (sans le signe) pour l'affichage.
     *
     * @param absCoeff la valeur absolue du coefficient
     * @param exp      l'exposant
     * @return la chaîne formatée, par exemple {@code "3x^2"} ou {@code "x"}
     */
    private static String formatTerme(double absCoeff, int exp) {
        if (exp == 0) {
            return formatNombre(absCoeff);
        }
        String varStr = (exp == 1) ? "x" : "x^" + exp;
        return (absCoeff == 1.0) ? varStr : formatNombre(absCoeff) + varStr;
    }

    /**
     * Formate un nombre réel : entier si la valeur est entière, décimal sinon.
     *
     * @param v la valeur à formater
     * @return la représentation textuelle du nombre
     */
    private static String formatNombre(double v) {
        if (v == Math.floor(v) && !Double.isInfinite(v)) {
            return String.valueOf((long) v);
        }
        return String.valueOf(v);
    }

    // ── Parser ────────────────────────────────────────────────────────────────

    /**
     * Parse une expression textuelle et retourne le polynôme correspondant.
     * Formats acceptés : {@code "x^2 + 3x - 5"}, {@code "x^30 + x^9"},
     * {@code "3.1x^2 - 2x + 1"}, {@code "5"}.
     *
     * @param expression l'expression à parser
     * @return le polynôme correspondant
     * @throws IllegalArgumentException si l'expression est {@code null}, vide ou invalide
     */
    public static Polynome parser(String expression) {
        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException("L'expression ne peut pas être nulle ou vide.");
        }
        String expr = expression.replaceAll("\\s+", "").toLowerCase();
        expr = expr.replaceAll("(?<=[\\dx])-", "+-");
        String[] parts = expr.split("\\+");
        List<Monome> liste = new ArrayList<>();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            liste.add(parserTerme(part));
        }
        return new Polynome(liste);
    }

    /**
     * Parse un terme isolé et retourne le monôme correspondant.
     *
     * @param part la chaîne représentant un terme, par exemple {@code "3x^2"} ou {@code "-5"}
     * @return le monôme correspondant
     * @throws IllegalArgumentException si le terme est invalide
     */
    private static Monome parserTerme(String part) {
        Matcher m;

        m = Pattern.compile("^(-?\\d*\\.?\\d*)x\\^(\\d+)$").matcher(part);
        if (m.matches()) {
            return new Monome(parseCoeff(m.group(1)), Integer.parseInt(m.group(2)));
        }

        m = Pattern.compile("^(-?\\d*\\.?\\d*)x$").matcher(part);
        if (m.matches()) {
            return new Monome(parseCoeff(m.group(1)), 1);
        }

        m = Pattern.compile("^-?\\d+\\.?\\d*$").matcher(part);
        if (m.matches()) {
            double val = Double.parseDouble(part);
            if (val == 0.0) {
                throw new IllegalArgumentException(
                        "Un monôme ne peut pas avoir un coefficient nul : '" + part + "'");
            }
            return new Monome(val, 0);
        }

        throw new IllegalArgumentException("Terme invalide dans l'expression : '" + part + "'");
    }

    /**
     * Interprète une chaîne comme un coefficient numérique.
     * Chaîne vide → {@code 1.0}, {@code "-"} → {@code -1.0}, sinon conversion numérique.
     *
     * @param s la chaîne à interpréter
     * @return le coefficient correspondant
     */
    private static double parseCoeff(String s) {
        if (s.isEmpty()) {
            return 1.0;
        }
        if (s.equals("-")) {
            return -1.0;
        }
        return Double.parseDouble(s);
    }
}