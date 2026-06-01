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
}