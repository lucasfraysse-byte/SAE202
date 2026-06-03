package iut.info.polynome.test;

import iut.info.polynome.DivisionEuclidienneResultat;
import iut.info.polynome.Polynome;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestDivisionEuclidienneResultat {

    @Test
    void encapsulationEtGetters() {
        Polynome q = Polynome.parser("x");
        Polynome r = Polynome.parser("1");

        DivisionEuclidienneResultat resultat = new DivisionEuclidienneResultat(q, r);

        assertNotNull(resultat.getQuotient());
        assertNotNull(resultat.getReste());
        assertEquals("x", resultat.getQuotient().toString());
        assertEquals("1", resultat.getReste().toString());
    }

    @Test
    void invariantDivisionA_egal_BQ_plus_R() {
        // X^2 - 1 = (X+1)(X-1) + 0
        Polynome a = Polynome.parser("x^2 - 1");
        Polynome b = Polynome.parser("x + 1");
        DivisionEuclidienneResultat res = a.diviser(b);

        // Recompose A = B*Q + R et compare terme à terme
        Polynome recompose = b.multiplier(res.getQuotient()).additionner(res.getReste());
        assertEquals(a.getDegre(), recompose.getDegre());
        for (int i = 0; i <= a.getDegre(); i++) {
            assertEquals(a.getCoefficient(i), recompose.getCoefficient(i), 1e-9);
        }
    }

    @Test
    void degreResteStrictementInferieurDiviseur() {
        // X^3 + 2X^2 + 1 divisé par X^2 : reste de degré < 2
        Polynome a = Polynome.parser("x^3 + 2x^2 + 1");
        Polynome b = Polynome.parser("x^2");
        DivisionEuclidienneResultat res = a.diviser(b);

        assertTrue(res.getReste().getDegre() < b.getDegre());
    }

    @Test
    void divisionExacteDonneResteNul() {
        Polynome a = Polynome.parser("x^2 - 4");
        Polynome b = Polynome.parser("x - 2");
        DivisionEuclidienneResultat res = a.diviser(b);

        assertTrue(res.getReste().estNul());
        assertEquals("x + 2", res.getQuotient().toString());
    }
}