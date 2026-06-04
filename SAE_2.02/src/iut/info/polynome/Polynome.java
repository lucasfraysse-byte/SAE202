package iut.info.polynome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Représente un polynôme à coefficients réels de IR[X].
 *
 * <p>La représentation interne est creuse : seuls les termes non nuls
 * sont stockés dans une liste de Monome, triée par exposant décroissant.
 * Deux termes de même exposant sont automatiquement fusionnés à la construction ;
 * les termes dont le coefficient est inférieur à EPSILON = 1e-9 sont supprimés.</p>
 *
 * <p>Les instances sont immuables : toutes les opérations retournent
 * un nouveau Polynome.</p>+
 */
public class Polynome {

    /** Seuil en dessous duquel un coefficient est considéré nul (arithmétique flottante). */
    private static final double EPSILON = 1e-9;

    private final List<Monome> termes;

    /**
     * Construit un polynôme à partir d'une liste de monômes.
     *
     * <p>Les monômes de même exposant sont additionnés ; les termes quasi-nuls
     * (|coefficient| &lt; EPSILON) sont supprimés. La liste interne est triée
     * par exposant décroissant et rendue immuable.</p>
     *
     * @param termes liste de monômes (non null, peut être vide pour le polynôme nul)
     * @throws IllegalArgumentException si termes est null
     */
    public Polynome(List<Monome> termes) {
        if (termes == null) {
        	throw new IllegalArgumentException("La liste de monômes ne peut pas être null.");
        }
        List<Monome> termesNormalises = new ArrayList<>();
        for (Monome monome : termes) {
            int indiceExistant = chercherExposant(termesNormalises, monome.getExposant());
            if (indiceExistant < 0) {
                termesNormalises.add(monome);
                continue;
            }
            double sommeFusion = termesNormalises.get(indiceExistant).getCoefficient() + monome.getCoefficient();
            if (Math.abs(sommeFusion) > EPSILON) {
                termesNormalises.set(indiceExistant, new Monome(sommeFusion, monome.getExposant()));
            } else {
                termesNormalises.remove(indiceExistant);
            }
        }
        termesNormalises.sort(Comparator.comparingInt(Monome::getExposant).reversed());
        this.termes = Collections.unmodifiableList(termesNormalises);
    }

    /**
     * Construit un polynôme à partir de ses racines réelles et de son coefficient dominant.
     *
     * <p>Le polynôme résultant est : coeffDominant · ∏(x - racines[i])^multiplicites[i].</p>
     *
     * @param racines        tableau des racines réelles
     * @param multiplicites  multiplicité de chaque racine (même taille que racines)
     * @param coeffDominant  coefficient dominant (non nul)
     * @throws IllegalArgumentException si coeffDominant == 0
     */
    public Polynome(double[] racines, int[] multiplicites, double coeffDominant) {
        if (coeffDominant == 0.0) {
        	throw new IllegalArgumentException("Le coefficient dominant ne peut être nul.");
        }
        Polynome produit = new Polynome(List.of(new Monome(coeffDominant, 0)));
        for (int i = 0; i < racines.length; i++) {
            Polynome facteur = new Polynome(List.of(new Monome(1.0, 1), new Monome(-racines[i], 0)));
            for (int m = 0; m < multiplicites[i]; m++) {
                produit = produit.multiplier(facteur);
            }
        }
        this.termes = produit.termes;
    }

    /**
     * Calcule le polynôme d'interpolation de Lagrange passant par les points donnés.
     *
     * <p>Pour n points (x[i], y[i]) à abscisses distinctes, retourne
     * l'unique polynôme de degré &lt; n vérifiant P(x[i]) = y[i].</p>
     *
     * @param x tableau des abscisses (toutes distinctes, non null, non vide)
     * @param y tableau des ordonnées (même taille que x)
     * @return polynôme d'interpolation
     * @throws IllegalArgumentException si les tableaux sont null, vides, de tailles différentes,
     *                                  ou si deux abscisses sont égales
     */
    public static Polynome interpoler(double[] x, double[] y) {
        if (x == null || y == null || x.length != y.length || x.length == 0) {
            throw new IllegalArgumentException("Tableaux invalides ou de tailles différentes.");
        }
        int nombrePoints = x.length;
        Polynome resultat = new Polynome(new ArrayList<>());

        for (int i = 0; i < nombrePoints; i++) {
            Polynome polynomeLagrange = new Polynome(List.of(new Monome(1.0, 0)));
            for (int j = 0; j < nombrePoints; j++) {
                if (i == j) {
                	continue;
                }
                double ecart = x[i] - x[j];
                if (Math.abs(ecart) < EPSILON) {
                    throw new IllegalArgumentException("Les abscisses doivent toutes être distinctes.");
                }
                Polynome facteur = new Polynome(List.of(
                        new Monome(1.0 / ecart, 1),
                        new Monome(-x[j] / ecart, 0)));
                polynomeLagrange = polynomeLagrange.multiplier(facteur);
            }
            resultat = resultat.additionner(polynomeLagrange.multiplierParScalaire(y[i]));
        }
        return resultat;
    }


    private static int chercherExposant(List<Monome> liste, int exposant) {
        for (int i = 0; i < liste.size(); i++) {
            if (liste.get(i).getExposant() == exposant) {
            	return i;
            }
        }
        return -1;
    }


    /**
     * Retourne le degré du polynôme.
     *
     * @return degré (exposant du terme dominant), ou -1 si le polynôme est nul
     */
    public int getDegre() {
        return termes.isEmpty() ? -1 : termes.get(0).getExposant();
    }

    /**
     * Retourne le coefficient du terme de degré exposant.
     *
     * @param exposant exposant cherché
     * @return le coefficient correspondant, ou 0.0 si absent
     */
    public double getCoefficient(int exposant) {
        for (Monome monome : termes) {
            if (monome.getExposant() == exposant) {
            	return monome.getCoefficient();
            }
        }
        return 0.0;
    }

    /**
     * Retourne un tableau dense des coefficients, indexé par exposant croissant.
     *
     * <p>Le tableau a une taille de {@code degré + 1} ; les exposants absents valent {@code 0.0}.</p>
     *
     * @return tableau {@code double[]} de taille {@code max(1, degré + 1)}
     */
    public double[] getCoefficients() {
        int degre = Math.max(0, getDegre());
        double[] coefficients = new double[degre + 1];
        for (Monome monome : termes) {
            int exp = monome.getExposant();
            if (exp >= 0) {
            	coefficients[exp] = monome.getCoefficient();
            }
        }
        return coefficients;
    }

    /**
     * Retourne la liste immuable des monômes (termes non nuls, triés par exposant décroissant).
     *
     * @return liste non modifiable de Monome
     */
    public List<Monome> getTermes() { 
    	return termes; 
    }

    /**
     * Indique si ce polynôme est le polynôme nul (aucun terme non nul).
     *
     * @return true si nul
     */
    public boolean estNul() { 
    	return termes.isEmpty(); 
    }


    /**
     * Retourne la somme {@code this + autre}.
     *
     * @param autre polynôme à additionner (non null)
     * @return nouveau polynôme somme
     * @throws IllegalArgumentException si autre est null
     */
    public Polynome additionner(Polynome autre) {
        if (autre == null) {
        	throw new IllegalArgumentException("Le polynôme à additionner ne peut pas être null.");
        }
        List<Monome> tousLesTermes = new ArrayList<>(this.termes.size() + autre.termes.size());
        tousLesTermes.addAll(this.termes);
        tousLesTermes.addAll(autre.termes);
        return new Polynome(tousLesTermes);
    }

    /**
     * Retourne le produit {@code this * autre}.
     *
     * @param autre polynôme à multiplier (non null)
     * @return nouveau polynôme produit (polynôme nul si l'un des opérandes est nul)
     * @throws IllegalArgumentException si autre est null
     */
    public Polynome multiplier(Polynome autre) {
        if (autre == null) {
        	throw new IllegalArgumentException("Le polynôme à multiplier ne peut pas être null.");
        }
        if (this.estNul() || autre.estNul()) {
        	return new Polynome(new ArrayList<>());
        }
        List<Monome> produits = new ArrayList<>(this.termes.size() * autre.termes.size());
        for (Monome monomeCourant : this.termes) {
            for (Monome monomeAutre : autre.termes) {
                produits.add(monomeCourant.multiplier(monomeAutre));
            }
        }
        return new Polynome(produits);
    }

    /**
     * Retourne ce polynôme multiplié par un scalaire : scalaire * this.
     *
     * @param scalaire valeur réelle
     * @return nouveau polynôme (polynôme nul si |scalaire| &lt; EPSILON)
     */
    public Polynome multiplierParScalaire(double scalaire) {
        if (Math.abs(scalaire) < EPSILON) {
        	return new Polynome(new ArrayList<>());
        }
        List<Monome> nouveauxTermes = new ArrayList<>(termes.size());
        for (Monome monome : termes) {
        	nouveauxTermes.add(monome.multiplierParScalaire(scalaire));
        }
        return new Polynome(nouveauxTermes);
    }

    /**
     * Effectue la division euclidienne {@code this ÷ diviseur}.
     *
     * <p>Garantie : this == diviseur * Q + R} avec deg(R) < deg(diviseur).</p>
     *
     * @param diviseur polynôme diviseur (non null, non nul)
     * @return DivisionEuclidienneResultat contenant le quotient et le reste
     * @throws IllegalArgumentException si diviseur est null ou est le polynôme nul
     */
    public DivisionEuclidienneResultat diviser(Polynome diviseur) {
        if (diviseur == null || diviseur.estNul()) throw new IllegalArgumentException("Division par le polynôme nul impossible.");
        List<Monome> termesQuotient = new ArrayList<>();
        Polynome dividendeCourant = this;
        int degreDiviseur = diviseur.getDegre();
        double coefficientDominantDiv = diviseur.termes.get(0).getCoefficient();

        while (!dividendeCourant.estNul() && dividendeCourant.getDegre() >= degreDiviseur) {
            Monome dominantDividende = dividendeCourant.termes.get(0);
            Monome monomePartiel = new Monome(
                    dominantDividende.getCoefficient() / coefficientDominantDiv,
                    dominantDividende.getExposant() - degreDiviseur);
            termesQuotient.add(monomePartiel);

            List<Monome> nouveauxTermes = new ArrayList<>(dividendeCourant.termes);
            for (Monome monomeDiviseur : diviseur.termes) {
                Monome produit = monomeDiviseur.multiplier(monomePartiel);
                nouveauxTermes.add(new Monome(-produit.getCoefficient(), produit.getExposant()));
            }
            dividendeCourant = new Polynome(nouveauxTermes);
        }
        return new DivisionEuclidienneResultat(new Polynome(termesQuotient), dividendeCourant);
    }


    /**
     * Évalue ce polynôme en x par l'algorithme de Horner adapté aux listes creuses.
     *
     * @param x valeur de la variable
     * @return P(x)
     */
    public double evaluer(double x) {
        if (estNul()) return 0.0;
        double resultat = 0.0;
        int degreCourant = getDegre();
        int indiceCourant = 0;
        while (degreCourant >= 0) {
            double coefficientCourant = 0.0;
            if (indiceCourant < termes.size() && termes.get(indiceCourant).getExposant() == degreCourant) {
                coefficientCourant = termes.get(indiceCourant).getCoefficient();
                indiceCourant++;
            }
            resultat = resultat * x + coefficientCourant;
            degreCourant--;
        }
        return resultat;
    }

    /**
     * Retourne la dérivée P' de ce polynôme.
     *
     * <p>Les termes constants (exposant 0) sont supprimés ; les autres sont dérivés
     * via Monome deriver().</p>
     *
     * @return polynôme dérivé (polynôme nul si this est constant)
     */
    public Polynome deriver() {
        List<Monome> termesDerivee = new ArrayList<>();
        for (Monome monome : termes) {
            if (monome.getExposant() != 0) termesDerivee.add(monome.deriver());
        }
        return new Polynome(termesDerivee);
    }

    /**
     * Retourne une primitive de ce polynôme (sans constante d'intégration).
     *
     * <p>Chaque terme c*x^n devient c/(n+1)*x^(n+1).</p>
     *
     * @return polynôme primitif
     */
    public Polynome integrer() {
        List<Monome> termesPrimitive = new ArrayList<>(termes.size());
        for (Monome monome : termes) termesPrimitive.add(monome.integrer());
        return new Polynome(termesPrimitive);
    }

    /**
     * Calcule la valeur moyenne de ce polynôme sur l'intervalle  [a, b].
     *
     * <p>Formule : (P_int(b) - P_int(a)) / (b - a), où P_int est la primitive.</p>
     *
     * @param a borne inférieure
     * @param b borne supérieure (différente de a)
     * @return valeur moyenne
     * @throws IllegalArgumentException si a == b
     */
    public double valeurMoyenne(double a, double b) {
        if (a == b) throw new IllegalArgumentException("L'intervalle doit être non nul.");
        Polynome primitive = this.integrer();
        return (primitive.evaluer(b) - primitive.evaluer(a)) / (b - a);
    }

    /**
     * Retourne la limite de ce polynôme en +∞.
     *
     * <p>Déterminée par le signe du coefficient dominant et la parité du degré.</p>
     *
     * @return Double POSITIVE_INFINITY ou Double NEGATIVE_INFINITY,
     *         ou 0.0 si le polynôme est nul
     */
    public double getLimiteEnPlusInfini() {
        if (estNul()) return 0.0;
        Monome termeDominant = termes.get(0);
        return termeDominant.getCoefficient() > 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
    }

    /**
     * Retourne la limite de ce polynôme en -∞.
     *
     * <p>Égale à la limite en +∞ si le degré est pair, opposée si impair.</p>
     *
     * @return Double POSITIVE_INFINITY ou Double NEGATIVE_INFINITY,
     *         ou 0.0 si le polynôme est nul
     */
    public double getLimiteEnMoinsInfini() {
        if (estNul()) return 0.0;
        double limitePlusInfini = getLimiteEnPlusInfini();
        boolean degrePair = termes.get(0).getExposant() % 2 == 0;
        return degrePair ? limitePlusInfini : -limitePlusInfini;
    }


    /**
     * Retourne la suite de Sturm de ce polynôme.
     *
     * <p>La suite de Sturm (P0, P1, P2, ...) est construite ainsi :
     * P0 = this, P1 = P', puis P_{k+1} = -(P_{k-1} mod P_k). Elle permet de
     * compter exactement les racines réelles distinctes dans un intervalle.</p>
     *
     * @return liste de polynômes formant la suite de Sturm
     */
    public List<Polynome> suiteDeSturm() {
        List<Polynome> suite = new ArrayList<>();
        suite.add(this);
        Polynome derivee = this.deriver();
        if (derivee.estNul()) return suite;
        suite.add(derivee);

        Polynome precedent = this;
        Polynome courant = derivee;

        while (true) {
            Polynome reste = precedent.diviser(courant).getReste();
            if (reste.estNul()) break;
            Polynome suivant = reste.multiplierParScalaire(-1.0);
            suite.add(suivant);
            precedent = courant;
            courant = suivant;
        }
        return suite;
    }

    /**
     * Compte le nombre de racines réelles distinctes dans l'intervalle ouvert ]a, b[
     * par le théorème de Sturm.
     *
     * @param a borne inférieure de l'intervalle
     * @param b borne supérieure de l'intervalle
     * @return nombre de racines réelles distinctes dans ]a, b[
     */
    public int getNombreRacinesReelles(double a, double b) {
        List<Polynome> suite = suiteDeSturm();
        return Math.abs(variationsSturm(a, suite) - variationsSturm(b, suite));
    }

    /**
     * Retourne la liste des racines réelles distinctes de ce polynôme.
     *
     * <p>L'algorithme utilise la borne de Cauchy pour définir l'intervalle initial,
     * puis applique Sturm pour isoler chaque racine et la raffiner :</p>
     * <ul>
     *   <li>racine simple (changement de signe) : bissection (60 itérations, précision ~1e-18)</li>
     *   <li>racine de multiplicité paire : méthode de Newton depuis le milieu de l'intervalle</li>
     * </ul>
     *
     * @return liste des racines triées par ordre croissant d'abscisse (vide si aucune)
     */
    public List<Double> getRacinesReelles() {
        if (estNul() || getDegre() == 0) return new ArrayList<>();
        double borneMaximale = borneCauchy();
        List<Double> racines = new ArrayList<>();
        isolerRacines(-borneMaximale, borneMaximale, racines);
        return racines;
    }

    /**
     * Retourne les multiplicités de chaque racine réelle distincte.
     *
     * <p>Chaque élément du tableau est {racine, multiplicité}.
     * La multiplicité de {@code r} est le plus petit entier  k >= 1 tel que
     * {@code P^(k)(r) != 0} (première dérivée non nulle en r).</p>
     *
     * @return liste de paires [racine, multiplicité], dans l'ordre des racines
     */
    public List<double[]> getMultiplicitesRacines() {
        List<double[]> resultats = new ArrayList<>();
        for (double racine : getRacinesReelles()) {
            Polynome polynomeCourant = this;
            int multiplicite = 0;
            while (!polynomeCourant.estNul() && Math.abs(polynomeCourant.evaluer(racine)) < 1e-7) {
                multiplicite++;
                polynomeCourant = polynomeCourant.deriver();
            }
            resultats.add(new double[]{racine, multiplicite});
        }
        return resultats;
    }

    /**
     * Borne de Cauchy : toutes les racines réelles sont dans [-M, M]
     * avec M = 1 + max(|ai|) / |an|.
     */
    private double borneCauchy() {
        double coefficientDominant = Math.abs(termes.get(0).getCoefficient());
        double maxCoefficientsAutres = termes.stream()
                .skip(1)
                .mapToDouble(monome -> Math.abs(monome.getCoefficient()))
                .max()
                .orElse(0.0);
        return 1.0 + maxCoefficientsAutres / coefficientDominant;
    }

    /**
     * Isole récursivement les racines dans [a, b] par dichotomie sur la suite de Sturm.
     * Quand un sous-intervalle contient exactement 1 racine, la raffine et l'ajoute à la liste.
     */
    private void isolerRacines(double a, double b, List<Double> racines) {
        int nombreRacines = getNombreRacinesReelles(a, b);
        if (nombreRacines == 0) return;
        if (nombreRacines == 1) {
            racines.add(trouverRacine(a, b));
            return;
        }
        double milieu = (a + b) / 2.0;
        isolerRacines(a, milieu, racines);
        isolerRacines(milieu, b, racines);
    }

    /**
     * Trouve la racine unique dans [a, b].
     * Utilise la bissection si f change de signe, Newton sinon (racine de multiplicité paire).
     */
    private double trouverRacine(double a, double b) {
        if (evaluer(a) * evaluer(b) < 0) {
            for (int i = 0; i < 60; i++) {
                double milieu = (a + b) / 2.0;
                if (evaluer(a) * evaluer(milieu) <= 0) b = milieu; else a = milieu;
            }
            return (a + b) / 2.0;
        }
        // Racine de multiplicité paire : Newton depuis le milieu
        double x = (a + b) / 2.0;
        Polynome derivee = deriver();
        for (int i = 0; i < 60; i++) {
            double valeurDerivee = derivee.evaluer(x);
            if (Math.abs(valeurDerivee) < EPSILON) break;
            x -= evaluer(x) / valeurDerivee;
        }
        return x;
    }

    private int variationsSturm(double x, List<Polynome> suite) {
        int nombreVariations = 0;
        Double valeurPrecedente = null;
        for (Polynome polynome : suite) {
            double valeurCourante = polynome.evaluer(x);
            if (Math.abs(valeurCourante) > EPSILON) {
                if (valeurPrecedente != null && valeurPrecedente * valeurCourante < 0) nombreVariations++;
                valeurPrecedente = valeurCourante;
            }
        }
        return nombreVariations;
    }


    /**
     * Retourne la représentation textuelle du polynôme en notation mathématique standard.
     *
     * <p>Exemples : "3x^2 - x + 5", "x", "0" (polynôme nul).</p>
     *
     * @return chaîne représentant le polynôme
     */
    @Override
    public String toString() {
        if (termes.isEmpty()) return "0";
        StringBuilder chaine = new StringBuilder();
        for (int i = 0; i < termes.size(); i++) {
            Monome monome = termes.get(i);
            double coefficient = monome.getCoefficient();
            if (i == 0) {
                if (coefficient < 0) chaine.append("-");
            } else {
                chaine.append(coefficient < 0 ? " - " : " + ");
            }
            chaine.append(formatTerme(Math.abs(coefficient), monome.getExposant()));
        }
        return chaine.toString();
    }

    private static String formatTerme(double coefficientAbsolu, int exposant) {
        if (exposant == 0) return formatNombre(coefficientAbsolu);
        String partieVariable = (exposant == 1) ? "x" : "x^" + exposant;
        return (coefficientAbsolu == 1.0) ? partieVariable : formatNombre(coefficientAbsolu) + partieVariable;
    }

    private static String formatNombre(double valeur) {
        if (valeur == Math.floor(valeur) && !Double.isInfinite(valeur)) return String.valueOf((long) valeur);
        return String.valueOf(valeur);
    }

    /**
     * Analyse une expression textuelle et retourne le polynôme correspondant.
     *
     * <p>Formats acceptés pour chaque terme (insensible à la casse, espaces ignorés) :</p>
     * <ul>
     *   <li>[c]x^n ex : "3x^2", "-x^3"</li>
     *   <li>[c]x   ex : "2x", "-x"</li>
     *   <li> c     ex : "5", "-3.14"</li>
     * </ul>
     *
     * @param expression expression textuelle du polynôme (non null, non vide)
     * @return polynôme parsé
     * @throws IllegalArgumentException si l'expression est null, vide ou contient un terme invalide
     */
    public static Polynome parser(String expression) {
        if (expression == null || expression.isBlank()) throw new IllegalArgumentException("L'expression est invalide.");
        String normalized = expression.replaceAll("\\s+", "").toLowerCase()
                                      .replaceAll("(?<=[\\dx])-", "+-");
        List<Monome> liste = new ArrayList<>();
        Scanner sc = new Scanner(normalized).useDelimiter("\\+");
        while (sc.hasNext()) {
            String terme = sc.next();
            if (!terme.isEmpty()) liste.add(parserTerme(terme));
        }
        sc.close();
        return new Polynome(liste);
    }

    private static Monome parserTerme(String terme) {
        int idxX = terme.indexOf('x');
        if (idxX == -1) {
            Scanner sc = new Scanner(terme).useLocale(Locale.ENGLISH);
            if (!sc.hasNextDouble()) throw new IllegalArgumentException("Terme invalide : '" + terme + "'");
            double valeur = sc.nextDouble();
            sc.close();
            if (valeur == 0.0) throw new IllegalArgumentException("Coefficient nul ignoré.");
            return new Monome(valeur, 0);
        }
        String coeffPart = terme.substring(0, idxX);
        String expPart   = terme.substring(idxX + 1);
        double coeff;
        if (coeffPart.isEmpty()) {
            coeff = 1.0;
        } else if (coeffPart.equals("-")) {
            coeff = -1.0;
        } else {
            Scanner sc = new Scanner(coeffPart).useLocale(Locale.ENGLISH);
            if (!sc.hasNextDouble()) throw new IllegalArgumentException("Coefficient invalide : '" + coeffPart + "'");
            coeff = sc.nextDouble();
            sc.close();
        }
        int exposant;
        if (expPart.isEmpty()) {
            exposant = 1;
        } else if (expPart.startsWith("^")) {
            Scanner sc = new Scanner(expPart.substring(1));
            if (!sc.hasNextInt()) throw new IllegalArgumentException("Exposant invalide : '" + expPart + "'");
            exposant = sc.nextInt();
            sc.close();
        } else {
            throw new IllegalArgumentException("Terme invalide après 'x' : '" + terme + "'");
        }
        return new Monome(coeff, exposant);
    }
}
