package iut.info.polynome;

/**
 * Encapsule le résultat d'une division euclidienne de polynômes.
 *
 * <p>Pour deux polynômes {@code A} et {@code B} (B non nul), la division euclidienne
 * garantit l'existence unique de {@code Q} et {@code R} tels que :</p>
 * <pre>  A = B · Q + R,   deg(R) &lt; deg(B)</pre>
 *
 * <p>Les instances sont immuables.</p>
 *
 * @see Polynome#diviser(Polynome)
 */
public class DivisionEuclidienneResultat {

    private final Polynome quotient;
    private final Polynome reste;

    /**
     * Construit un résultat de division euclidienne.
     *
     * @param quotient polynôme quotient Q (non null)
     * @param reste    polynôme reste R (non null, deg(R) &lt; deg(diviseur))
     */
    public DivisionEuclidienneResultat(Polynome quotient, Polynome reste) {
        this.quotient = quotient;
        this.reste = reste;
    }

    /**
     * Retourne le quotient Q de la division.
     *
     * @return le polynôme quotient
     */
    public Polynome getQuotient() {
        return quotient;
    }

    /**
     * Retourne le reste R de la division.
     *
     * @return le polynôme reste (nul si la division est exacte)
     */
    public Polynome getReste() {
        return reste;
    }
}
