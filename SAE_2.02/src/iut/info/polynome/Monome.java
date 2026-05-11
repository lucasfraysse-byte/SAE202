package iut.info.polynome;

/**
 * Représente un terme non nul d'un polynôme de la forme : coefficient * x^exposant.
 * <p>
 * Un monôme est immuable. Son coefficient ne peut pas être nul et son exposant ne peut
 * pas être négatif.
 * </p>
 */
public class Monome {

    private final double coefficient;
    private final int exposant;

    /**
     * Construit un monôme avec le coefficient et l'exposant donnés.
     *
     * @param coefficient le coefficient du terme, doit être non nul
     * @param exposant    l'exposant du terme, doit être positif ou nul
     * @throws IllegalArgumentException si le coefficient est nul ou si l'exposant est négatif
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
     * Retourne le coefficient de ce monôme.
     *
     * @return le coefficient
     */
    public double getCoefficient() {
        return coefficient;
    }

    /**
     * Retourne l'exposant de ce monôme.
     *
     * @return l'exposant
     */
    public int getExposant() {
        return exposant;
    }

    /**
     * Évalue ce monôme en la valeur donnée : {@code coefficient * valeurX^exposant}.
     *
     * @param valeurX la valeur en laquelle évaluer le monôme
     * @return la valeur du monôme en {@code valeurX}
     */
    public double evaluer(double valeurX) {
        return coefficient * Math.pow(valeurX, exposant);
    }

    /**
     * Retourne le produit de ce monôme par un autre monôme.
     * <p>
     * Les coefficients sont multipliés et les exposants additionnés.
     * </p>
     *
     * @param autre le monôme multiplicateur, ne peut pas être null
     * @return un nouveau monôme représentant le produit
     * @throws IllegalArgumentException si {@code autre} est null
     */
    public Monome multiplier(Monome autre) {
        if (autre == null) {
            throw new IllegalArgumentException("Le monôme à multiplier ne peut pas être null.");
        }
        return new Monome(coefficient * autre.coefficient, exposant + autre.exposant);
    }

    /**
     * Retourne le monôme dérivé de ce monôme : {@code (coefficient * exposant) * x^(exposant-1)}.
     * <p>
     * Ne peut pas être appelé sur un terme constant (exposant = 0) car la dérivée serait nulle
     * et ne peut pas être représentée comme un monôme. {@code Polynome.deriver()} filtre ces
     * termes avant d'appeler cette méthode.
     * </p>
     *
     * @return le monôme dérivé
     * @throws IllegalStateException si l'exposant est 0 (terme constant)
     */
    public Monome deriver() {
        if (exposant == 0) {
            throw new IllegalStateException("La dérivée d'un terme constant est nulle et ne peut pas être représentée comme un monôme.");
        }
        return new Monome(coefficient * exposant, exposant - 1);
    }

    /**
     * Retourne un nouveau monôme dont le coefficient est multiplié par le facteur donné.
     *
     * @param facteur le scalaire multiplicateur, doit être non nul
     * @return un nouveau monôme résultant de la multiplication
     * @throws IllegalArgumentException si le facteur est nul
     */
    public Monome multiplierParScalaire(double facteur) {
        if (facteur == 0.0) {
            throw new IllegalArgumentException("Le facteur scalaire ne peut pas être nul.");
        }
        return new Monome(coefficient * facteur, exposant);
    }
}
