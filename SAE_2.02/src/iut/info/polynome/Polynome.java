package iut.info.polynome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Représente un polynôme à coefficients réels de IR[X].
 * Stocké comme une liste triée (degré décroissant) de {@link Monome} non nuls.
 * Le polynôme nul correspond à une liste vide.
 *
 * <p>Opérations disponibles : addition, multiplication par un polynôme,
 * multiplication par un scalaire (produit scalaire), division euclidienne.</p>
 */
public class Polynome {

    /** Seuil en dessous duquel un coefficient est considéré comme nul. */
    private static final double EPSILON = 1e-9;

    /** Liste immuable de monômes triés par exposant décroissant. */
    private final List<Monome> termes;

    // ── Constructeur ──────────────────────────────────────────────────────────

    /**
     * Construit un polynôme à partir d'une liste de monômes.
     *
     * <p>Les monômes de même exposant sont fusionnés (somme des coefficients).
     * Les termes dont le coefficient résultant est quasi-nul ({@code |c| < 1e-9}) sont supprimés.
     * La liste interne est triée par degré décroissant.</p>
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
     *
     * @param liste    la liste dans laquelle chercher
     * @param exposant l'exposant recherché
     * @return l'indice trouvé ou {@code -1}
     */
    private static int chercherExposant(List<Monome> liste, int exposant) {
        for (int i = 0; i < liste.size(); i++) {
            if (liste.get(i).getExposant() == exposant) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Trie la liste par exposant décroissant (tri à bulles).
     *
     * @param liste la liste à trier en place
     */
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
     * Retourne le coefficient du terme d'exposant donné,
     * ou {@code 0.0} si ce terme est absent.
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
     * <p>Principe : on regroupe tous les monômes des deux polynômes dans une
     * seule liste, puis on laisse le constructeur fusionner les termes de même
     * exposant et supprimer ceux dont la somme est nulle.</p>
     *
     * <p>Exemples :</p>
     * <pre>
     *   (X^2 + 3X) + (2X + 1)  =  X^2 + 5X + 1
     *   (X^2 + X)  + (-X^2)    =  X
     * </pre>
     *
     * @param autre le polynôme à additionner (non {@code null})
     * @return un nouveau polynôme égal à {@code this + autre}
     * @throws IllegalArgumentException si {@code autre} est {@code null}
     */
    public Polynome additionner(Polynome autre) {
        if (autre == null) {
            throw new IllegalArgumentException("Le polynôme à additionner ne peut pas être null.");
        }
        // On rassemble tous les monômes des deux polynômes
        List<Monome> tousLesTermes = new ArrayList<>();
        tousLesTermes.addAll(this.termes);
        tousLesTermes.addAll(autre.termes);
        // Le constructeur se charge de la fusion et du tri
        return new Polynome(tousLesTermes);
    }

    /**
     * Retourne le produit de ce polynôme par le polynôme {@code autre}.
     *
     * <p>Principe : chaque monôme de {@code this} est multiplié par chaque
     * monôme de {@code autre} (règle : on multiplie les coefficients et on
     * additionne les exposants). Les produits partiels sont ensuite
     * rassemblés ; le constructeur fusionne les termes de même degré.</p>
     *
     * <p>Exemples :</p>
     * <pre>
     *   (X + 1) * (X - 1)  =  X^2 - 1
     *   (2X)    * (3X^2)   =  6X^3
     * </pre>
     *
     * @param autre le polynôme multiplicateur (non {@code null})
     * @return un nouveau polynôme égal à {@code this * autre}
     * @throws IllegalArgumentException si {@code autre} est {@code null}
     */
    public Polynome multiplier(Polynome autre) {
        if (autre == null) {
            throw new IllegalArgumentException("Le polynôme à multiplier ne peut pas être null.");
        }
        // Cas particulier : si l'un des deux est nul, le produit est nul
        if (this.estNul() || autre.estNul()) {
            return new Polynome(new ArrayList<>());
        }
        List<Monome> produits = new ArrayList<>();
        // On multiplie chaque monôme de this par chaque monôme de autre
        for (Monome mThis : this.termes) {
            for (Monome mAutre : autre.termes) {
                produits.add(mThis.multiplier(mAutre));
            }
        }
        // Le constructeur fusionne les termes de même exposant
        return new Polynome(produits);
    }

    /**
     * Retourne le produit de ce polynôme par un scalaire réel.
     *
     * <p>Principe : chaque coefficient du polynôme est multiplié par le scalaire.
     * On réutilise la méthode {@link Monome#multiplierParScalaire(double)} déjà
     * disponible sur chaque monôme.</p>
     *
     * <p>Exemples :</p>
     * <pre>
     *   (x^2 + 3x) * 2    =  2x^2 + 6x
     *   (x^2 - 1)  * (-1) =  -x^2 + 1
     * </pre>
     *
     * @param scalaire le facteur réel (non nul)
     * @return un nouveau polynôme égal à {@code this * scalaire}
     * @throws IllegalArgumentException si {@code scalaire} est nul
     */
    public Polynome multiplierParScalaire(double scalaire) {
        if (scalaire == 0.0) {
            throw new IllegalArgumentException("Le scalaire ne peut pas être nul.");
        }
        // Cas particulier : polynôme nul * scalaire = polynôme nul
        if (this.estNul()) {
            return new Polynome(new ArrayList<>());
        }
        List<Monome> nouveauxTermes = new ArrayList<>();
        // On multiplie chaque monôme par le scalaire
        for (Monome m : this.termes) {
            nouveauxTermes.add(m.multiplierParScalaire(scalaire));
        }
        return new Polynome(nouveauxTermes);
    }

    /**
     * Effectue la division euclidienne de ce polynôme par le polynôme {@code diviseur}
     * et retourne un tableau contenant {@code [quotient, reste]}.
     *
     * <p>La division euclidienne garantit que :</p>
     * <pre>
     *   this = diviseur * quotient + reste
     * </pre>
     * <p>avec {@code degré(reste) < degré(diviseur)}, ou {@code reste} est le
     * polynôme nul.</p>
     *
     * <p>Algorithme utilisé : division « à la main » suivant les puissances
     * décroissantes (identique à la division euclidienne des entiers).</p>
     * <ol>
     *   <li>On travaille sur un dividende courant (initialement une copie de {@code this}).</li>
     *   <li>Tant que le degré du dividende courant est ≥ degré du diviseur :<br>
     *       – on calcule le monôme quotient partiel = terme dominant du dividende
     *         divisé par le terme dominant du diviseur ;<br>
     *       – on soustrait {@code diviseur * monôme_partiel} du dividende courant.</li>
     *   <li>Ce qui reste est le reste de la division.</li>
     * </ol>
     *
     * <p>Exemples :</p>
     * <pre>
     *   (X^2 - 1) / (X + 1)  →  quotient = X - 1,  reste = 0
     *   (X^2 + 1) / (X)      →  quotient = X,       reste = 1
     * </pre>
     *
     * @param diviseur le polynôme diviseur (non {@code null}, non nul)
     * @return un tableau {@code Polynome[2]} tel que {@code [0]} = quotient et {@code [1]} = reste
     * @throws IllegalArgumentException si {@code diviseur} est {@code null} ou est le polynôme nul
     */
    public Polynome[] diviser(Polynome diviseur) {
        if (diviseur == null) {
            throw new IllegalArgumentException("Le diviseur ne peut pas être null.");
        }
        if (diviseur.estNul()) {
            throw new IllegalArgumentException("La division par le polynôme nul est impossible.");
        }

        // Le quotient est construit terme par terme
        List<Monome> termesQuotient = new ArrayList<>();

        // Le dividende courant démarre comme une copie de this
        // On le représente par sa liste de monômes dans une liste mutable
        // Pour simplifier, on retravaille avec des Polynome à chaque étape
        Polynome dividendeCourant = new Polynome(new ArrayList<>(this.termes));

        // Degré et coefficient dominant du diviseur (ne changent pas)
        int degreeDiviseur        = diviseur.getDegre();
        double coeffDominantDiv   = diviseur.getTermes().get(0).getCoefficient();

        // Tant que le degré du reste courant >= degré du diviseur
        while (!dividendeCourant.estNul() && dividendeCourant.getDegre() >= degreeDiviseur) {

            // Monôme dominant du dividende courant
            Monome dominantDividende = dividendeCourant.getTermes().get(0);

            // Monôme quotient partiel : coeff = coeff_dominant_dividende / coeff_dominant_diviseur
            //                           exp   = exp_dominant_dividende   - exp_dominant_diviseur
            double coeffPartiel  = dominantDividende.getCoefficient() / coeffDominantDiv;
            int    exposantPartiel = dominantDividende.getExposant() - degreeDiviseur;
            Monome monomePartiel = new Monome(coeffPartiel, exposantPartiel);

            // On ajoute ce monôme au quotient
            termesQuotient.add(monomePartiel);

            // On soustrait diviseur * monomePartiel du dividende courant
            // Soustraction = addition de l'opposé
            // On construit -monomePartiel * diviseur
            List<Monome> soustractionTermes = new ArrayList<>();
            for (Monome mDiv : diviseur.getTermes()) {
                // Produit de mDiv par monomePartiel, puis on prend l'opposé (coefficient * -1)
                Monome produit = mDiv.multiplier(monomePartiel);
                soustractionTermes.add(new Monome(-produit.getCoefficient(), produit.getExposant()));
            }
            // On additionne le dividende courant avec ces termes négatifs
            List<Monome> nouveauxTermes = new ArrayList<>(dividendeCourant.getTermes());
            nouveauxTermes.addAll(soustractionTermes);
            dividendeCourant = new Polynome(nouveauxTermes);
        }

        Polynome quotient = new Polynome(termesQuotient);
        Polynome reste    = dividendeCourant; // ce qui reste après la boucle

        return new Polynome[]{quotient, reste};
    }

    // ── Affichage ─────────────────────────────────────────────────────────────

    /**
     * Retourne une représentation lisible du polynôme.
     *
     * <p>Exemples : {@code "x^2 + 3x + 3"}, {@code "-x^2 - 3x"}, {@code "0"}.</p>
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
     * Formate un nombre réel : affiche un entier si la valeur est entière,
     * sinon affiche la valeur décimale complète.
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
     *
     * <p>Formats acceptés : {@code "x^2 + 3x - 5"}, {@code "x^30 + x^9"},
     * {@code "3.1x^2 - 2x + 1"}, {@code "5"}.</p>
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
     * Parse un terme isolé (sans opérateur {@code +}) et retourne le monôme correspondant.
     *
     * @param part la chaîne représentant un terme, par exemple {@code "3x^2"} ou {@code "-5"}
     * @return le monôme correspondant
     * @throws IllegalArgumentException si le terme est invalide ou de coefficient nul
     */
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
                throw new IllegalArgumentException(
                        "Un monôme ne peut pas avoir un coefficient nul : '" + part + "'");
            }
            return new Monome(val, 0);
        }

        throw new IllegalArgumentException("Terme invalide dans l'expression : '" + part + "'");
    }

    /**
     * Interprète une chaîne comme un coefficient numérique.
     *
     * <ul>
     *   <li>Chaîne vide → {@code 1.0} (coefficient implicite)</li>
     *   <li>{@code "-"} → {@code -1.0}</li>
     *   <li>Sinon → conversion numérique standard</li>
     * </ul>
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
