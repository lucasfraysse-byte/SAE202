package iut.info.polynome.test;

import iut.info.polynome.Monome;
import iut.info.polynome.Polynome;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires de Polynome.
 */
class TestPolynome {

    // ── Construction ──────────────────────────────────────────────────────────

    @Test
    void constructionStockeLesCoefficients() {
        List<Monome> termes = new ArrayList<>();
        termes.add(new Monome(1.0, 2));
        termes.add(new Monome(3.0, 1));
        termes.add(new Monome(3.0, 0));
        Polynome p = new Polynome(termes);

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
        List<Monome> termes = new ArrayList<>();
        termes.add(new Monome(2.0, 1));
        termes.add(new Monome(3.0, 1));
        Polynome p = new Polynome(termes);

        assertEquals(5.0, p.getCoefficient(1), 1e-9);
        assertEquals(1, p.getDegre());
    }

    @Test
    void constructionElimineLesZerosAptesFusion() {
        // 3x + (-3x) + 1  =>  le terme x disparaît
        List<Monome> termes = new ArrayList<>();
        termes.add(new Monome(3.0, 1));
        termes.add(new Monome(-3.0, 1));
        termes.add(new Monome(1.0, 0));
        Polynome p = new Polynome(termes);

        assertEquals(0, p.getDegre());
        assertEquals(0.0, p.getCoefficient(1), 1e-9);
        assertEquals(1.0, p.getCoefficient(0), 1e-9);
    }

    @Test
    void constructionTrieParDegreDécroissant() {
        List<Monome> termes = new ArrayList<>();
        termes.add(new Monome(1.0, 0));
        termes.add(new Monome(3.0, 2));
        termes.add(new Monome(2.0, 1));
        Polynome p = new Polynome(termes);

        List<Monome> liste = p.getTermes();
        assertEquals(2, liste.get(0).getExposant());
        assertEquals(1, liste.get(1).getExposant());
        assertEquals(0, liste.get(2).getExposant());
    }

    @Test
    void constructionNullLeveIAE() {
        assertThrows(IllegalArgumentException.class, () -> new Polynome(null));
    }

    // ── Degré ─────────────────────────────────────────────────────────────────

    @Test
    void degreRetourneLeDegreMax() {
        List<Monome> termes = new ArrayList<>();
        termes.add(new Monome(1.0, 5));
        termes.add(new Monome(2.0, 3));
        assertEquals(5, new Polynome(termes).getDegre());
    }

    @Test
    void degrePolynomeNulRetourneMoinsUn() {
        assertEquals(-1, new Polynome(new ArrayList<>()).getDegre());
    }

    @Test
    void degrePolynomeConstantEstZero() {
        List<Monome> termes = new ArrayList<>();
        termes.add(new Monome(7.0, 0));
        assertEquals(0, new Polynome(termes).getDegre());
    }

    // ── Coefficient ───────────────────────────────────────────────────────────

    @Test
    void getCoefficientAbsentRetourneZero() {
        List<Monome> termes = new ArrayList<>();
        termes.add(new Monome(3.0, 2));
        Polynome p = new Polynome(termes);

        assertEquals(0.0, p.getCoefficient(5), 1e-9);
        assertEquals(0.0, p.getCoefficient(0), 1e-9);
    }

    // ── toString ──────────────────────────────────────────────────────────────

    @Test
    void toStringPolynomeComplet() {
        List<Monome> termes = new ArrayList<>();
        termes.add(new Monome(1.0, 2));
        termes.add(new Monome(3.0, 1));
        termes.add(new Monome(3.0, 0));
        assertEquals("X^2 + 3X + 3", new Polynome(termes).toString());
    }

    @Test
    void toStringAvecCoefficientsNegatifs() {
        List<Monome> termes = new ArrayList<>();
        termes.add(new Monome(-1.0, 2));
        termes.add(new Monome(-3.0, 1));
        assertEquals("-X^2 - 3X", new Polynome(termes).toString());
    }

    @Test
    void toStringPolynomeNul() {
        assertEquals("0", new Polynome(new ArrayList<>()).toString());
    }

    @Test
    void toStringAvecCoefficientsDecimaux() {
        List<Monome> termes = new ArrayList<>();
        termes.add(new Monome(3.5, 2));
        termes.add(new Monome(2.0, 0));
        assertEquals("3.5X^2 + 2", new Polynome(termes).toString());
    }

    @Test
    void toStringPolynomeConstant() {
        List<Monome> termes = new ArrayList<>();
        termes.add(new Monome(5.0, 0));
        assertEquals("5", new Polynome(termes).toString());
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
        List<Monome> termes = new ArrayList<>();
        termes.add(new Monome(1.0, 2));
        assertFalse(new Polynome(termes).estNul());
    }
}
