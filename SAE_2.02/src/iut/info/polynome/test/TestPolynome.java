package iut.info.polynome.test;

import iut.info.polynome.Monome;
import iut.info.polynome.Polynome;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires de la classe {@link Polynome} — écrits en TDD.
 */
class TestPolynome {

    // ── Construction ──────────────────────────────────────────────────────────

    @Test
    void constructionStockeLesCoefficients() {
        Polynome p = new Polynome(Arrays.asList(
                new Monome(1.0, 2), new Monome(3.0, 1), new Monome(3.0, 0)));
        assertEquals(1.0, p.getCoefficient(2), 1e-9);
        assertEquals(3.0, p.getCoefficient(1), 1e-9);
        assertEquals(3.0, p.getCoefficient(0), 1e-9);
    }

    @Test
    void constructionListeVideDonnePolynomeNul() {
        assertTrue(new Polynome(new ArrayList<>()).estNul());
    }

    @Test
    void constructionFusionneTermesDeMemeExposant() {
        // 2x + 3x = 5x
        Polynome p = new Polynome(Arrays.asList(new Monome(2.0, 1), new Monome(3.0, 1)));
        assertEquals(5.0, p.getCoefficient(1), 1e-9);
        assertEquals(1, p.getDegre());
    }

    @Test
    void constructionElimineLesZerosAptesFusion() {
        // 3x + (-3x) + 1  =>  le terme x disparaît
        Polynome p = new Polynome(Arrays.asList(
                new Monome(3.0, 1), new Monome(-3.0, 1), new Monome(1.0, 0)));
        assertEquals(0, p.getDegre());
        assertEquals(0.0, p.getCoefficient(1), 1e-9);
        assertEquals(1.0, p.getCoefficient(0), 1e-9);
    }

    @Test
    void constructionTrieParDegreDécroissant() {
        Polynome p = new Polynome(Arrays.asList(
                new Monome(1.0, 0), new Monome(3.0, 2), new Monome(2.0, 1)));
        List<Monome> termes = p.getTermes();
        assertEquals(2, termes.get(0).getExposant());
        assertEquals(1, termes.get(1).getExposant());
        assertEquals(0, termes.get(2).getExposant());
    }

    @Test
    void constructionNullLeveIAE() {
        assertThrows(IllegalArgumentException.class, () -> new Polynome(null));
    }

    // ── Degré ─────────────────────────────────────────────────────────────────

    @Test
    void degreRetourneLeDegreMax() {
        assertEquals(5, new Polynome(Arrays.asList(
                new Monome(1.0, 5), new Monome(2.0, 3))).getDegre());
    }

    @Test
    void degrePolynomeNulRetourneMoinsUn() {
        assertEquals(-1, new Polynome(new ArrayList<>()).getDegre());
    }

    @Test
    void degrePolynomeConstantEstZero() {
        assertEquals(0, new Polynome(Arrays.asList(new Monome(7.0, 0))).getDegre());
    }

    // ── Coefficient ───────────────────────────────────────────────────────────

    @Test
    void getCoefficientAbsentRetourneZero() {
        Polynome p = new Polynome(Arrays.asList(new Monome(3.0, 2)));
        assertEquals(0.0, p.getCoefficient(5), 1e-9);
        assertEquals(0.0, p.getCoefficient(0), 1e-9);
    }

    // ── toString ──────────────────────────────────────────────────────────────

    @Test
    void toStringPolynomeComplet() {
        Polynome p = new Polynome(Arrays.asList(
                new Monome(1.0, 2), new Monome(3.0, 1), new Monome(3.0, 0)));
        assertEquals("X^2 + 3X + 3", p.toString());
    }

    @Test
    void toStringAvecCoefficientsNegatifs() {
        Polynome p = new Polynome(Arrays.asList(
                new Monome(-1.0, 2), new Monome(-3.0, 1)));
        assertEquals("-X^2 - 3X", p.toString());
    }

    @Test
    void toStringPolynomeNul() {
        assertEquals("0", new Polynome(new ArrayList<>()).toString());
    }

    @Test
    void toStringAvecCoefficientsDecimaux() {
        Polynome p = new Polynome(Arrays.asList(new Monome(3.5, 2), new Monome(2.0, 0)));
        assertEquals("3.5X^2 + 2", p.toString());
    }

    @Test
    void toStringPolynomeConstant() {
        assertEquals("5", new Polynome(Arrays.asList(new Monome(5.0, 0))).toString());
    }

    // ── Parser ────────────────────────────────────────────────────────────────

    @Test
    void parserPolynomeComplet() {
        Polynome p = Polynome.parser("x^2 + 3x + 3");
        assertEquals(2, p.getDegre());
        assertEquals(1.0, p.getCoefficient(2), 1e-9);
        assertEquals(3.0, p.getCoefficient(1), 1e-9);
        assertEquals(3.0, p.getCoefficient(0), 1e-9);
    }

    @Test
    void parserGrandDegre() {
        Polynome p = Polynome.parser("x^30 + x^9");
        assertEquals(30, p.getDegre());
        assertEquals(1.0, p.getCoefficient(30), 1e-9);
        assertEquals(1.0, p.getCoefficient(9), 1e-9);
    }

    @Test
    void parserAvecSoustractions() {
        Polynome p = Polynome.parser("x^2 - 3x + 1");
        assertEquals(1.0, p.getCoefficient(2), 1e-9);
        assertEquals(-3.0, p.getCoefficient(1), 1e-9);
        assertEquals(1.0, p.getCoefficient(0), 1e-9);
    }

    @Test
    void parserSigneNegatifEnTete() {
        Polynome p = Polynome.parser("-x^2 + 3x - 1");
        assertEquals(-1.0, p.getCoefficient(2), 1e-9);
        assertEquals(3.0, p.getCoefficient(1), 1e-9);
        assertEquals(-1.0, p.getCoefficient(0), 1e-9);
    }

    @Test
    void parserConstanteSeule() {
        Polynome p = Polynome.parser("5");
        assertEquals(0, p.getDegre());
        assertEquals(5.0, p.getCoefficient(0), 1e-9);
    }

    @Test
    void parserCoefficientsDecimaux() {
        Polynome p = Polynome.parser("3.1x^2 + 2.5x");
        assertEquals(3.1, p.getCoefficient(2), 1e-9);
        assertEquals(2.5, p.getCoefficient(1), 1e-9);
    }

    @Test
    void parserTermeSeulSansCoefficient() {
        // "x" seul doit donner 1*x^1
        Polynome p = Polynome.parser("x");
        assertEquals(1, p.getDegre());
        assertEquals(1.0, p.getCoefficient(1), 1e-9);
    }

    @Test
    void parserExpressionInvalideLeveIAE() {
        assertThrows(IllegalArgumentException.class, () -> Polynome.parser("abc"));
    }

    @Test
    void parserExpressionVideLeveIAE() {
        assertThrows(IllegalArgumentException.class, () -> Polynome.parser(""));
    }

    // ── estNul ────────────────────────────────────────────────────────────────

    @Test
    void estNulRetourneFalsePourPolynomeNonVide() {
        assertFalse(new Polynome(Arrays.asList(new Monome(1.0, 2))).estNul());
    }
}
