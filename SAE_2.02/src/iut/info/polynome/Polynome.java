package iut.info.polynome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Représente un polynôme à coefficients réels de IR[X].
 * Stocké comme une liste triée (degré décroissant) de Monome non nuls.
 * Le polynôme nul correspond à une liste vide.
 */
public class Polynome {

    private static final double EPSILON = 1e-9;

    private final List<Monome> termes;

    /**
     * Construit un polynôme à partir d'une liste de monômes.
     * Les monômes de même exposant sont fusionnés (somme des coefficients).
     * Les termes dont le coefficient résultant est quasi-nul (|c| < 1e-9) sont supprimés.
     * La liste interne est triée par degré décroissant.
     *
     * @param termes liste de monômes (vide pour le polynôme nul)
     * @throws IllegalArgumentException si termes est null
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

    /** Retourne l'indice du monôme ayant l'exposant donné, ou -1 si absent. */
    private static int chercherExposant(List<Monome> liste, int exposant) {
        for (int i = 0; i < liste.size(); i++) {
            if (liste.get(i).getExposant() == exposant) return i;
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

    /**
     * Retourne le degré du polynôme, ou -1 si le polynôme est nul.
     */
    public int getDegre() {
        return termes.isEmpty() ? -1 : termes.get(0).getExposant();
    }

    /**
     * Retourne le coefficient du terme d'exposant donné, ou 0.0 s'il est absent.
     */
    public double getCoefficient(int exposant) {
        for (Monome m : termes) {
            if (m.getExposant() == exposant) return m.getCoefficient();
        }
        return 0.0;
    }

    /**
     * Retourne la liste immuable des monômes triés par degré décroissant.
     */
    public List<Monome> getTermes() {
        return termes;
    }

    /**
     * Retourne true si ce polynôme est le polynôme nul.
     */
    public boolean estNul() {
        return termes.isEmpty();
    }

    /**
     * Retourne une représentation lisible du polynôme.
     * Exemples : "X^2 + 3X + 3", "-X^2 - 3X", "0".
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

    private static String formatTerme(double absCoeff, int exp) {
        if (exp == 0) return formatNombre(absCoeff);
        String varStr = (exp == 1) ? "X" : "X^" + exp;
        return (absCoeff == 1.0) ? varStr : formatNombre(absCoeff) + varStr;
    }

    private static String formatNombre(double v) {
        if (v == Math.floor(v) && !Double.isInfinite(v)) {
            return String.valueOf((long) v);
        }
        return String.valueOf(v);
    }

    // ── Parser ────────────────────────────────────────────────────────────────

    /**
     * Parse une expression textuelle et retourne le polynôme correspondant.
     * Formats acceptés : "x^2 + 3x - 5", "x^30 + x^9", "3.1x^2 - 2x + 1", "5".
     *
     * @param expression l'expression à parser
     * @return le polynôme correspondant
     * @throws IllegalArgumentException si l'expression est nulle, vide ou invalide
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
            if (part.isEmpty()) continue;
            liste.add(parserTerme(part));
        }
        return new Polynome(liste);
    }

    private static Monome parserTerme(String part) {
        Matcher m;

        // Forme [coeff]x^[exp] : "3x^2", "x^30", "-x^2"
        m = Pattern.compile("^(-?\\d*\\.?\\d*)x\\^(\\d+)$").matcher(part);
        if (m.matches()) {
            return new Monome(parseCoeff(m.group(1)), Integer.parseInt(m.group(2)));
        }

        // Forme [coeff]x : "3x", "x", "-x"
        m = Pattern.compile("^(-?\\d*\\.?\\d*)x$").matcher(part);
        if (m.matches()) {
            return new Monome(parseCoeff(m.group(1)), 1);
        }

        // Forme constante : "5", "-3", "2.7"
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

    private static double parseCoeff(String s) {
        if (s.isEmpty()) return 1.0;
        if (s.equals("-")) return -1.0;
        return Double.parseDouble(s);
    }
}
