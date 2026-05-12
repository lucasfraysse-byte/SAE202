package iut.info.polynome;

/**
 * Représente un terme non nul d'un polynôme : coefficient * x^exposant.
 * Un monôme est immuable. Son coefficient ne peut pas être nul
 * et son exposant ne peut pas être négatif.
 */
public class Monome {

    private final double coefficient;
    private final int exposant;

    /**
     * Construit un monôme avec le coefficient et l'exposant donnés.
     *
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

    public double getCoefficient() {
        return coefficient;
    }

    public int getExposant() {
        return exposant;
    }

    /**
     * Évalue ce monôme en valeurX : coefficient * valeurX^exposant.
     */
    public double evaluer(double valeurX) {
        return coefficient * Math.pow(valeurX, exposant);
    }

    /**
     * Retourne le produit de ce monôme par un autre monôme.
     * Les coefficients sont multipliés et les exposants additionnés.
     *
     * @throws IllegalArgumentException si autre est null
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
     * @throws IllegalArgumentException si le facteur est nul
     */
    public Monome multiplierParScalaire(double facteur) {
        if (facteur == 0.0) {
            throw new IllegalArgumentException("Le facteur scalaire ne peut pas être nul.");
        }
        return new Monome(coefficient * facteur, exposant);
    }

    /**
     * Retourne le monôme dérivé : (coefficient * exposant) * x^(exposant-1).
     *
     * @throws IllegalStateException si l'exposant est 0 (dérivée nulle, non représentable)
     */
    public Monome deriver() {
        if (exposant == 0) {
            throw new IllegalStateException("La dérivée d'un terme constant est nulle et ne peut pas être représentée comme un monôme.");
        }
        return new Monome(coefficient * exposant, exposant - 1);
    }
}
