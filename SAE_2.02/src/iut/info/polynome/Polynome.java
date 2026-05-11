package iut.info.polynome;

import java.util.*;
import java.util.regex.*;

/**
 * Représente un polynôme à coefficients réels de IR[X].
 * <p>
 * Stocké comme une liste triée (degré décroissant) de {@link Monome} non nuls,
 * ce qui est économe en mémoire pour les polynômes creux (nombreux coefficients nuls).
 * Le polynôme nul correspond à une liste vide.
 * </p>
 */
public class Polynome {

    private static final double EPSILON = 1e-9;

    private final List<Monome> termes;

    /**
     * Construit un polynôme à partir d'une liste de monômes.
     * <p>
     * Les monômes de même exposant sont fusionnés (somme des coefficients).
     * Les termes dont le coefficient résultant est quasi-nul (|c| &lt; 1e-9) sont supprimés.
     * La liste interne est triée par degré décroissant.
     * </p>
     *
     * @param termes liste de monômes (vide pour le polynôme nul)
     * @throws IllegalArgumentException si {@code termes} est null
     */
    public Polynome(List<Monome> termes) {
        if (termes == null) {
            throw new IllegalArgumentException("La liste de monômes ne peut pas être null.");
        }
        Map<Integer, Double> coeffs = new TreeMap<>(Comparator.reverseOrder());
        for (Monome m : termes) {
            coeffs.merge(m.getExposant(), m.getCoefficient(), Double::sum);
        }
        List<Monome> liste = new ArrayList<>();
        for (Map.Entry<Integer, Double> e : coeffs.entrySet()) {
            if (Math.abs(e.getValue()) > EPSILON) {
                liste.add(new Monome(e.getValue(), e.getKey()));
            }
        }
        this.termes = Collections.unmodifiableList(liste);
    }

    /**
     * Retourne le degré du polynôme.
     *
     * @return le degré maximal, ou {@code -1} si le polynôme est nul
     */
    public int getDegre() {
        return termes.isEmpty() ? -1 : termes.get(0).getExposant();
    }

    /**
     * Retourne le coefficient du monôme d'exposant donné, ou {@code 0.0} s'il est absent.
     *
     * @param exposant l'exposant recherché
     * @return le coefficient correspondant
     */
    public double getCoefficient(int exposant) {
        for (Monome m : termes) {
            if (m.getExposant() == exposant) return m.getCoefficient();
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
     * @return {@code true} si la liste est vide
     */
    public boolean estNul() {
        return termes.isEmpty();
    }

    /**
     * Retourne une représentation lisible du polynôme.
     * Exemples : {@code "X^2 + 3X + 3"}, {@code "-X^2 - 3X"}, {@code "0"}.
     *
     * @return la représentation textuelle
     */
    @Override
    public String toString() {
        if (termes.isEmpty()) return "0";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < termes.size(); i++) {
            Monome m = termes.get(i);
            double coeff = m.getCoefficient();
            int exp = m.getExposant();
            if (i == 0) {
                if (coeff < 0) sb.append("-");
                sb.append(formatTerme(Math.abs(coeff), exp));
            } else {
                sb.append(coeff < 0 ? " - " : " + ");
                sb.append(formatTerme(Math.abs(coeff), exp));
            }
        }
        return sb.toString();
    }

    /**
     * Formate un terme à partir de son coefficient absolu et de son exposant.
     * Exemples : {@code (1.0, 2) -> "X^2"}, {@code (3.0, 1) -> "3X"}, {@code (5.0, 0) -> "5"}.
     */
    private static String formatTerme(double absCoeff, int exp) {
        if (exp == 0) return formatNombre(absCoeff);
        String varStr = (exp == 1) ? "X" : "X^" + exp;
        return (absCoeff == 1.0) ? varStr : formatNombre(absCoeff) + varStr;
    }

    /** Formate un réel en supprimant le ".0" pour les entiers. */
    private static String formatNombre(double v) {
        if (v == Math.floor(v) && !Double.isInfinite(v)) {
            return String.valueOf((long) v);
        }
        return String.valueOf(v);
    }

    // ── Parser ────────────────────────────────────────────────────────────────

    /**
     * Parse une expression textuelle et retourne le polynôme correspondant.
     * <p>
     * Formats acceptés (majuscules ou minuscules, espaces libres) :
     * {@code "x^2 + 3x - 5"}, {@code "x^30 + x^9"}, {@code "3.1x^2 - 2x + 1"}, {@code "5"}.
     * </p>
     *
     * @param expression l'expression à parser
     * @return le polynôme correspondant
     * @throws IllegalArgumentException si l'expression est nulle, vide ou contient un terme invalide
     */
    public static Polynome parser(String expression) {
        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException("L'expression ne peut pas être nulle ou vide.");
        }
        // Supprime les espaces et met en minuscules
        String expr = expression.replaceAll("\\s+", "").toLowerCase();

        // Insère un '+' devant tout '-' qui suit un chiffre ou 'x',
        // pour pouvoir découper uniformément sur '+'
        expr = expr.replaceAll("(?<=[\\dx])-", "+-");

        String[] parts = expr.split("\\+");
        List<Monome> termes = new ArrayList<>();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            termes.add(parserTerme(part));
        }
        return new Polynome(termes);
    }

    /**
     * Parse un seul terme (ex : {@code "3x^2"}, {@code "-x"}, {@code "5"}).
     *
     * @throws IllegalArgumentException si le terme ne correspond à aucun format connu
     */
    private static Monome parserTerme(String part) {
        Matcher m;

        // Forme : [coeff]x^[exp]   ex: "3x^2", "x^30", "-x^2", "-2.5x^3"
        m = Pattern.compile("^(-?\\d*\\.?\\d*)x\\^(\\d+)$").matcher(part);
        if (m.matches()) {
            return new Monome(parseCoeff(m.group(1)), Integer.parseInt(m.group(2)));
        }

        // Forme : [coeff]x        ex: "3x", "x", "-x", "-2.5x"
        m = Pattern.compile("^(-?\\d*\\.?\\d*)x$").matcher(part);
        if (m.matches()) {
            return new Monome(parseCoeff(m.group(1)), 1);
        }

        // Forme : constante        ex: "5", "-3", "2.7"
        m = Pattern.compile("^-?\\d+\\.?\\d*$").matcher(part);
        if (m.matches()) {
            double val = Double.parseDouble(part);
            if (val == 0.0) {
                throw new IllegalArgumentException("Un monôme ne peut pas avoir un coefficient nul : '" + part + "'");
            }
            return new Monome(val, 0);
        }

        throw new IllegalArgumentException("Terme invalide dans l'expression : '" + part + "'");
    }

    /** Interprète la partie coefficient extraite par le parser (vide -> 1, "-" -> -1). */
    private static double parseCoeff(String s) {
        if (s.isEmpty()) return 1.0;
        if (s.equals("-")) return -1.0;
        return Double.parseDouble(s);
    }
}
