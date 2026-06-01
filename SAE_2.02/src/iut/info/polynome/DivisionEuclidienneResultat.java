package iut.info.polynome;

/**
 * Encapsule le résultat d'une division euclidienne de polynômes.
 */
public class DivisionEuclidienneResultat {
    
    private final Polynome quotient;
    private final Polynome reste;

    public DivisionEuclidienneResultat(Polynome quotient, Polynome reste) {
        this.quotient = quotient;
        this.reste = reste;
    }

    public Polynome getQuotient() {
        return quotient;
    }

    public Polynome getReste() {
        return reste;
    }
}