package iut.info.polynome.test;

import iut.info.polynome.Monome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires de Monome.
 * La fixture monome représente 3x².
 */
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

        Monome negatif = new Monome(-2.5, 3);
        assertEquals(-2.5, negatif.getCoefficient(), 1e-9);
        assertEquals(3, negatif.getExposant());

        Monome constante = new Monome(5.0, 0);
        assertEquals(5.0, constante.getCoefficient(), 1e-9);
        assertEquals(0, constante.getExposant());
    }

    @Test
    void constructionCoefficientNulLeveIAE() {
        assertThrows(IllegalArgumentException.class, () -> new Monome(0.0, 2));
    }

    @Test
    void constructionExposantNegatifLeveIAE() {
        assertThrows(IllegalArgumentException.class, () -> new Monome(1.0, -1));
    }

    // ── Évaluation ───────────────────────────────────────────────────────────

    @Test
    void evaluerCalculeValeurExacte() {
        // 2x en 3 = 6
        assertEquals(6.0, new Monome(2.0, 1).evaluer(3.0), 1e-9);
        // 3x² en 2 = 12
        assertEquals(12.0, monome.evaluer(2.0), 1e-9);
        // terme constant indépendant de x
        assertEquals(5.0, new Monome(5.0, 0).evaluer(100.0), 1e-9);
        // en 0, résultat nul pour exposant > 0
        assertEquals(0.0, monome.evaluer(0.0), 1e-9);
        // x négatif avec exposant pair : 3·(-2)² = 12
        assertEquals(12.0, monome.evaluer(-2.0), 1e-9);
    }

    // ── Multiplication par scalaire ───────────────────────────────────────────

    @Test
    void multiplierParScalaireRetourneNouveauMonome() {
        Monome parDeux = monome.multiplierParScalaire(2.0);
        assertEquals(6.0, parDeux.getCoefficient(), 1e-9);
        assertEquals(2, parDeux.getExposant());

        Monome inverse = monome.multiplierParScalaire(-1.0);
        assertEquals(-3.0, inverse.getCoefficient(), 1e-9);
        assertEquals(2, inverse.getExposant());

        Monome identite = monome.multiplierParScalaire(1.0);
        assertEquals(3.0, identite.getCoefficient(), 1e-9);
        assertEquals(2, identite.getExposant());
    }

    @Test
    void multiplierParScalaireNulLeveIAE() {
        assertThrows(IllegalArgumentException.class, () -> monome.multiplierParScalaire(0.0));
    }

    // ── Multiplication de deux monômes ───────────────────────────────────────

    @Test
    void multiplierDeuxMonomesFusionneExposants() {
        // (3x²) * (2x) = 6x³
        Monome produit = monome.multiplier(new Monome(2.0, 1));
        assertEquals(6.0, produit.getCoefficient(), 1e-9);
        assertEquals(3, produit.getExposant());

        // (3x²) * (2) = 6x²
        Monome produitConstante = monome.multiplier(new Monome(2.0, 0));
        assertEquals(6.0, produitConstante.getCoefficient(), 1e-9);
        assertEquals(2, produitConstante.getExposant());
    }

    @Test
    void multiplierAvecNullLeveIAE() {
        assertThrows(IllegalArgumentException.class, () -> monome.multiplier(null));
    }

    // ── Dérivation ───────────────────────────────────────────────────────────

    @Test
    void deriverRetourneMonomeDerive() {
        // (3x)' = 3
        Monome d1 = new Monome(3.0, 1).deriver();
        assertEquals(3.0, d1.getCoefficient(), 1e-9);
        assertEquals(0, d1.getExposant());

        // (3x²)' = 6x
        Monome d2 = monome.deriver();
        assertEquals(6.0, d2.getCoefficient(), 1e-9);
        assertEquals(1, d2.getExposant());

        // (2x³)' = 6x²
        Monome d3 = new Monome(2.0, 3).deriver();
        assertEquals(6.0, d3.getCoefficient(), 1e-9);
        assertEquals(2, d3.getExposant());
    }

    @Test
    void deriverConstanteLeveISE() {
        assertThrows(IllegalStateException.class, () -> new Monome(5.0, 0).deriver());
    }
}
