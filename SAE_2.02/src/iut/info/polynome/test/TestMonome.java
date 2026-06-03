package iut.info.polynome.test;

import iut.info.polynome.Monome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestMonome {

    private Monome monome;

    @BeforeEach
    void initialiser() {
        monome = new Monome(3.0, 2);
    }

    // ── Construction ─────────────────────────────────────────────────────────

    @Test
    void constructionStockeCoefficientsEtExposant() {
        assertEquals(3.0, monome.getCoefficient(), 1e-9);
        assertEquals(2, monome.getExposant());
    }

    @Test
    void constructionValeursExtremes() {
        Monome max = new Monome(Double.MAX_VALUE, 100);
        assertEquals(Double.MAX_VALUE, max.getCoefficient());
        assertEquals(100, max.getExposant());

        Monome min = new Monome(Double.MIN_VALUE, 0);
        assertEquals(Double.MIN_VALUE, min.getCoefficient());
        assertEquals(0, min.getExposant());
    }

    @Test
    void constructionCoefficientNulLeveIAE() {
        assertThrows(IllegalArgumentException.class, () -> new Monome(0.0, 2));
    }

    @Test
    void constructionExposantNegatifLeveIAE() {
        assertThrows(IllegalArgumentException.class, () -> new Monome(2.0, -3));
        assertThrows(IllegalArgumentException.class, () -> new Monome(1.0, -1));
    }

    @Test
    void deriverExposantZeroLeveISE() {
        assertThrows(IllegalStateException.class, () -> new Monome(5.0, 0).deriver());
    }

    @Test
    void multiplierParScalaireZeroLeveIAE() {
        assertThrows(IllegalArgumentException.class, () -> monome.multiplierParScalaire(0.0));
    }

    // ── Évaluation ───────────────────────────────────────────────────────────

    @Test
    void evaluerCalculeValeurExacte() {
        assertEquals(12.0, monome.evaluer(2.0), 1e-9);
        assertEquals(0.0, monome.evaluer(0.0), 1e-9);
        assertEquals(12.0, monome.evaluer(-2.0), 1e-9);
    }

    @Test
    void evaluerValeursExtremes() {
        // Un grand exposant avec une grande valeur doit tendre vers l'infini (gestion IEEE 754)
        Monome extreme = new Monome(1.0, 1000);
        assertTrue(Double.isInfinite(extreme.evaluer(1e100)));
        
        // Valeur très proche de zéro
        assertEquals(0.0, extreme.evaluer(1e-100), 1e-9);
    }

    // ── Opérations ────────────────────────────────────────────────────────────

    @Test
    void multiplierParScalaireRetourneNouveauMonome() {
        Monome parDeux = monome.multiplierParScalaire(2.0);
        assertEquals(6.0, parDeux.getCoefficient(), 1e-9);
        assertEquals(2, parDeux.getExposant());
    }

    @Test
    void multiplierDeuxMonomesFusionneExposants() {
        Monome produit = monome.multiplier(new Monome(2.0, 1));
        assertEquals(6.0, produit.getCoefficient(), 1e-9);
        assertEquals(3, produit.getExposant());
    }

    @Test
    void deriverRetourneMonomeDerive() {
        Monome d2 = monome.deriver();
        assertEquals(6.0, d2.getCoefficient(), 1e-9);
        assertEquals(1, d2.getExposant());
    }

    @Test
    void integrerRetourneMonomePrimitif() {
        Monome p = monome.integrer();
        assertEquals(1.0, p.getCoefficient(), 1e-9); // 3 / (2+1) = 1
        assertEquals(3, p.getExposant()); // 2 + 1 = 3
    }

    @Test
    void integrerValeursExtremes() {
        Monome p = new Monome(1e50, 99).integrer();
        assertEquals(1e50 / 100.0, p.getCoefficient(), 1e40);
        assertEquals(100, p.getExposant());
    }
}