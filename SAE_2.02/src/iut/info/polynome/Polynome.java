package iut.info.polynome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Polynome {

    private static final double EPSILON = 1e-9;
    private final List<Monome> termes;

    public Polynome(List<Monome> termes) {
        if (termes == null) throw new IllegalArgumentException("La liste de monômes ne peut pas être null.");
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

    /**
     * Construit un polynôme à partir de ses racines réelles.
     */
    public Polynome(double[] racines, int[] multiplicites, double coeffDominant) {
        if (coeffDominant == 0.0) throw new IllegalArgumentException("Le coefficient dominant ne peut être nul.");
        Polynome p = new Polynome(List.of(new Monome(coeffDominant, 0)));
        for (int i = 0; i < racines.length; i++) {
            Polynome facteur = new Polynome(List.of(new Monome(1.0, 1), new Monome(-racines[i], 0)));
            for (int m = 0; m < multiplicites[i]; m++) {
                p = p.multiplier(facteur);
            }
        }
        this.termes = p.getTermes();
    }

    private static int chercherExposant(List<Monome> liste, int exposant) {
        for (int i = 0; i < liste.size(); i++) {
            if (liste.get(i).getExposant() == exposant) return i;
        }
        return -1;
    }

    private static void trierDecroissant(List<Monome> liste) {
        liste.sort((m1, m2) -> Integer.compare(m2.getExposant(), m1.getExposant()));
    }

    public int getDegre() {
        return termes.isEmpty() ? -1 : termes.get(0).getExposant();
    }

    public double getCoefficient(int exposant) {
        for (Monome m : termes) {
            if (m.getExposant() == exposant) return m.getCoefficient();
        }
        return 0.0;
    }
    
    public double[] getCoefficients() {
        int degre = Math.max(0, getDegre());
        double[] coeffs = new double[degre + 1];
        for (Monome m : termes) {
            coeffs[m.getExposant()] = m.getCoefficient();
        }
        return coeffs;
    }

    public List<Monome> getTermes() { return termes; }
    public boolean estNul() { return termes.isEmpty(); }

    public Polynome additionner(Polynome autre) {
        if (autre == null) throw new IllegalArgumentException("Le polynôme à additionner ne peut pas être null.");
        List<Monome> tousLesTermes = new ArrayList<>(this.termes);
        tousLesTermes.addAll(autre.termes);
        return new Polynome(tousLesTermes);
    }

    public Polynome multiplier(Polynome autre) {
        if (autre == null) throw new IllegalArgumentException("Le polynôme à multiplier ne peut pas être null.");
        if (this.estNul() || autre.estNul()) return new Polynome(new ArrayList<>());
        List<Monome> produits = new ArrayList<>();
        for (Monome mThis : this.termes) {
            for (Monome mAutre : autre.termes) {
                produits.add(mThis.multiplier(mAutre));
            }
        }
        return new Polynome(produits);
    }

    public Polynome multiplierParScalaire(double scalaire) {
        if (Math.abs(scalaire) < EPSILON) return new Polynome(new ArrayList<>());
        List<Monome> nouveauxTermes = new ArrayList<>();
        for (Monome m : this.termes) nouveauxTermes.add(m.multiplierParScalaire(scalaire));
        return new Polynome(nouveauxTermes);
    }

    public DivisionEuclidienneResultat diviser(Polynome diviseur) {
        if (diviseur == null || diviseur.estNul()) throw new IllegalArgumentException("Division par le polynôme nul impossible.");
        List<Monome> termesQuotient = new ArrayList<>();
        Polynome dividendeCourant = new Polynome(new ArrayList<>(this.termes));
        int degreeDiviseur = diviseur.getDegre();
        double coeffDominantDiv = diviseur.getTermes().get(0).getCoefficient();

        while (!dividendeCourant.estNul() && dividendeCourant.getDegre() >= degreeDiviseur) {
            Monome dominantDividende = dividendeCourant.getTermes().get(0);
            double coeffPartiel = dominantDividende.getCoefficient() / coeffDominantDiv;
            int exposantPartiel = dominantDividende.getExposant() - degreeDiviseur;
            Monome monomePartiel = new Monome(coeffPartiel, exposantPartiel);

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
        return new DivisionEuclidienneResultat(new Polynome(termesQuotient), dividendeCourant);
    }

    // ── Analyse Mathématique ──────────────────────────────────────────────────

    /**
     * Évaluation par la méthode de Horner généralisée.
     */
    public double evaluer(double x) {
        if (estNul()) return 0.0;
        double res = 0.0;
        int currentDeg = getDegre();
        int i = 0;
        while (currentDeg >= 0) {
            double c = 0.0;
            if (i < termes.size() && termes.get(i).getExposant() == currentDeg) {
                c = termes.get(i).getCoefficient();
                i++;
            }
            res = res * x + c;
            currentDeg--;
        }
        return res;
    }

    public Polynome deriver() {
        List<Monome> derives = new ArrayList<>();
        for (Monome m : termes) {
            if (m.getExposant() > 0) derives.add(m.deriver());
        }
        return new Polynome(derives);
    }

    public Polynome integrer() {
        List<Monome> primitives = new ArrayList<>();
        for (Monome m : termes) primitives.add(m.integrer());
        return new Polynome(primitives);
    }

    public double valeurMoyenne(double a, double b) {
        if (a == b) throw new IllegalArgumentException("L'intervalle doit être non nul.");
        Polynome primitive = this.integrer();
        return (primitive.evaluer(b) - primitive.evaluer(a)) / (b - a);
    }

    public double getLimiteEnPlusInfini() {
        if (estNul()) return 0.0;
        Monome dominant = termes.get(0);
        return dominant.getCoefficient() > 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
    }

    public double getLimiteEnMoinsInfini() {
        if (estNul()) return 0.0;
        Monome dominant = termes.get(0);
        if (dominant.getExposant() % 2 == 0) {
            return dominant.getCoefficient() > 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        } else {
            return dominant.getCoefficient() > 0 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        }
    }

    // ── Suite de Sturm & Racines ──────────────────────────────────────────────

    public List<Polynome> suiteDeSturm() {
        List<Polynome> suite = new ArrayList<>();
        suite.add(this);
        Polynome pPrime = this.deriver();
        if (pPrime.estNul()) return suite;
        suite.add(pPrime);

        Polynome pPrev = this;
        Polynome pCurr = pPrime;

        while (true) {
            Polynome reste = pPrev.diviser(pCurr).getReste();
            if (reste.estNul()) break;
            Polynome pNext = reste.multiplierParScalaire(-1.0);
            suite.add(pNext);
            pPrev = pCurr;
            pCurr = pNext;
        }
        return suite;
    }

    private int variationsSturm(double x, List<Polynome> suite) {
        int variations = 0;
        Double lastVal = null;
        for (Polynome p : suite) {
            double val = p.evaluer(x);
            if (Math.abs(val) > EPSILON) {
                if (lastVal != null && lastVal * val < 0) variations++;
                lastVal = val;
            }
        }
        return variations;
    }

    public int getNombreRacinesReelles(double a, double b) {
        List<Polynome> suite = suiteDeSturm();
        return Math.abs(variationsSturm(a, suite) - variationsSturm(b, suite));
    }

    // ── Utilitaires et Parsing ────────────────────────────────────────────────

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
        String varStr = (exp == 1) ? "x" : "x^" + exp;
        return (absCoeff == 1.0) ? varStr : formatNombre(absCoeff) + varStr;
    }

    private static String formatNombre(double v) {
        if (v == Math.floor(v) && !Double.isInfinite(v)) return String.valueOf((long) v);
        return String.valueOf(v);
    }

    public static Polynome parser(String expression) {
        if (expression == null || expression.isBlank()) throw new IllegalArgumentException("L'expression est invalide.");
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
        m = Pattern.compile("^(-?\\d*\\.?\\d*)x\\^(\\d+)$").matcher(part);
        if (m.matches()) return new Monome(parseCoeff(m.group(1)), Integer.parseInt(m.group(2)));

        m = Pattern.compile("^(-?\\d*\\.?\\d*)x$").matcher(part);
        if (m.matches()) return new Monome(parseCoeff(m.group(1)), 1);

        m = Pattern.compile("^-?\\d+\\.?\\d*$").matcher(part);
        if (m.matches()) {
            double val = Double.parseDouble(part);
            if (val == 0.0) throw new IllegalArgumentException("Coefficient nul ignoré.");
            return new Monome(val, 0);
        }
        throw new IllegalArgumentException("Terme invalide : '" + part + "'");
    }

    private static double parseCoeff(String s) {
        if (s.isEmpty()) return 1.0;
        if (s.equals("-")) return -1.0;
        return Double.parseDouble(s);
    }
}