package iut.info.polynome;

public class Monome {

    private final double coefficient;
    private final int exposant;

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

    public double getCoefficient() { return coefficient; }
    public int getExposant() { return exposant; }

    public double evaluer(double valeurX) {
        return coefficient * Math.pow(valeurX, exposant);
    }

    public Monome multiplier(Monome autre) {
        if (autre == null) throw new IllegalArgumentException("Le monôme à multiplier ne peut pas être null.");
        return new Monome(coefficient * autre.coefficient, exposant + autre.exposant);
    }

    public Monome multiplierParScalaire(double facteur) {
        if (facteur == 0.0) throw new IllegalArgumentException("Le facteur scalaire ne peut pas être nul.");
        return new Monome(coefficient * facteur, exposant);
    }

    public Monome deriver() {
        if (exposant == 0) {
            throw new IllegalStateException("La dérivée d'un terme constant est nulle.");
        }
        return new Monome(coefficient * exposant, exposant - 1);
    }

    public Monome integrer() {
        return new Monome(coefficient / (exposant + 1), exposant + 1);
    }
}