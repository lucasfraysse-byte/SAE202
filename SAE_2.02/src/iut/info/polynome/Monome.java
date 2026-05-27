package iut.info.polynome;

/**
 * Représente un terme non nul d'un polynôme : {@code coefficient * x^exposant}.
 * Un monôme est immuable. Son coefficient ne peut pas être nul
 * et son exposant ne peut pas être négatif.
 */
public class Monome {

    private final double coefficient;
    private final int exposant;

    /**
     * Construit un monôme avec le coefficient et l'exposant donnés.
     *
     * @param coefficient le coefficient (non nul)
     * @param exposant    l'exposant (non négatif)
     * @throws IllegalArgumentException si le coefficient est nul ou l'exposant négatif
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

    /** @return le coefficient de ce monôme */
    public double getCoefficient() {
        return coefficient;
    }

    /** @return l'exposant de ce monôme */
    public int getExposant() {
        return exposant;
    }

    /**
     * Évalue ce monôme en {@code valeurX} : {@code coefficient * valeurX^exposant}.
     *
     * @param valeurX la valeur de x
     * @return le résultat de l'évaluation
     */
    public double evaluer(double valeurX) {
        return coefficient * Math.pow(valeurX, exposant);
    }

    /**
     * Retourne le produit de ce monôme par un autre monôme.
     *
     * @param autre le monôme multiplicateur (non {@code null})
     * @return un nouveau monôme résultat du produit
     * @throws IllegalArgumentException si {@code autre} est {@code null}
     */
    public Monome multiplier(Monome autre) {
        if (autre == null) {
            throw new IllegalArgumentException("Le monôme à multiplier ne peut pas être null.");
        }
        return new Monome(coefficient * autre.coefficient, exposant + autre.exposant);
    }

    /**
     * Retourne un nouveau monôme dont le coefficient est multiplié par le facteur donné.
     *
     * @param facteur le facteur scalaire (non nul)
     * @return un nouveau monôme résultat de la multiplication
     * @throws IllegalArgumentException si le facteur est nul
     */
    public Monome multiplierParScalaire(double facteur) {
        if (facteur == 0.0) {
            throw new IllegalArgumentException("Le facteur scalaire ne peut pas être nul.");
        }
        return new Monome(coefficient * facteur, exposant);
    }

    /**
     * Retourne le monôme dérivé : {@code (coefficient * exposant) * x^(exposant - 1)}.
     *
     * @return le monôme dérivé
     * @throws IllegalStateException si l'exposant est 0 (la dérivée serait nulle)
     */
    public Monome deriver() {
        if (exposant == 0) {
            throw new IllegalStateException(
                    "La dérivée d'un terme constant est nulle et ne peut pas être représentée comme un monôme.");
        }
        return new Monome(coefficient * exposant, exposant - 1);
    }
}