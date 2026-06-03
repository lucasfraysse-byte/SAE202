package iut.info.polynome;

/**
 * Représente un monôme non nul de la forme {@code c · x^n},
 * où {@code c} est le coefficient (réel non nul) et {@code n} l'exposant (entier >= 0).
 *
 * <p>Les instances sont immuables : toutes les opérations retournent un nouveau {@code Monome}.</p>
 */
public class Monome {

    private final double coefficient;
    private final int exposant;

    /**
     * Construit un monôme {@code coefficient · x^exposant}.
     *
     * @param coefficient valeur réelle non nulle
     * @param exposant    entier naturel (&gt;= 0)
     * @throws IllegalArgumentException si {@code coefficient == 0} ou {@code exposant < 0}
     */
    public Monome(double coefficient, int exposant) {
        if (coefficient == 0.0) {
            throw new IllegalArgumentException("Le coefficient d'un monôme ne peut pas être nul.");
        }
        if (exposant < 0) {
            throw new IllegalArgumentException("L'exposant d'un monôme ne peut pas être négatif.");
        }
        this.coefficient = coefficient;
        this.exposant = exposant;
    }

    /**
     * Retourne le coefficient {@code c} du monôme {@code c · x^n}.
     *
     * @return le coefficient (jamais nul)
     */
    public double getCoefficient() { return coefficient; }

    /**
     * Retourne l'exposant {@code n} du monôme {@code c · x^n}.
     *
     * @return l'exposant (entier naturel, toujours &gt;= 0)
     */
    public int getExposant() { return exposant; }

    /**
     * Évalue le monôme en {@code x} : retourne {@code c · x^n}.
     *
     * @param valeurX valeur de la variable
     * @return {@code coefficient * Math.pow(valeurX, exposant)}
     */
    public double evaluer(double valeurX) {
        return coefficient * Math.pow(valeurX, exposant);
    }

    /**
     * Retourne le produit de ce monôme par {@code autre} :
     * {@code (c1·x^n1) * (c2·x^n2) = (c1·c2)·x^(n1+n2)}.
     *
     * @param autre monôme à multiplier (non null)
     * @return nouveau monôme produit
     * @throws IllegalArgumentException si {@code autre} est null
     */
    public Monome multiplier(Monome autre) {
        if (autre == null) throw new IllegalArgumentException("Le monôme à multiplier ne peut pas être null.");
        return new Monome(coefficient * autre.coefficient, exposant + autre.exposant);
    }

    /**
     * Retourne ce monôme multiplié par un scalaire :
     * {@code facteur · (c·x^n) = (facteur·c)·x^n}.
     *
     * @param facteur scalaire non nul
     * @return nouveau monôme
     * @throws IllegalArgumentException si {@code facteur == 0}
     */
    public Monome multiplierParScalaire(double facteur) {
        if (facteur == 0.0) throw new IllegalArgumentException("Le facteur scalaire ne peut pas être nul.");
        return new Monome(coefficient * facteur, exposant);
    }

    /**
     * Retourne la dérivée de ce monôme :
     * {@code d/dx(c·x^n) = (n·c)·x^(n-1)}.
     *
     * @return monôme dérivé
     * @throws IllegalStateException si l'exposant est 0 (la dérivée d'une constante est nulle,
     *                               non représentable comme un {@code Monome})
     */
    public Monome deriver() {
        if (exposant == 0) {
            throw new IllegalStateException("La dérivée d'un terme constant est nulle.");
        }
        return new Monome(coefficient * exposant, exposant - 1);
    }

    /**
     * Retourne une primitive de ce monôme (sans constante d'intégration) :
     * {@code ∫c·x^n dx = c/(n+1) · x^(n+1)}.
     *
     * @return monôme primitif
     */
    public Monome integrer() {
        return new Monome(coefficient / (exposant + 1), exposant + 1);
    }
}
