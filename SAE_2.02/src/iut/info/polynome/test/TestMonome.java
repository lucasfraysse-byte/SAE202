package iut.info.polynome.test;

import iut.info.polynome.Monome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires de la classe {@link Monome}.
 * <p>
 * La fixture partagée {@code monome} représente 3x² et est recréée avant chaque test.
 * </p>
 */
class TestMonome {

    /** Monôme 3x² utilisé dans la majorité des tests. */
    private Monome monome;

    @BeforeEach
    void initialiser() {
        monome = new Monome(3.0, 2);
    }

    // ── Construction ─────────────────────────────────────────────────────────

    /**
     * Vérifie que le constructeur stocke correctement coefficient et exposant
     * pour un coefficient positif, un coefficient négatif et un exposant nul.
     */
    @Test
    void constructionStockeCoefficientsEtExposant() {
        assertEquals(3.0, monome.getCoefficient(), 1e-9);
        assertEquals(2,   monome.getExposant());

        Monome negatif = new Monome(-2.5, 3);
        assertEquals(-2.5, negatif.getCoefficient(), 1e-9);
        assertEquals(3,    negatif.getExposant());

        Monome constante = new Monome(5.0, 0);
        assertEquals(5.0, constante.getCoefficient(), 1e-9);
        assertEquals(0,   constante.getExposant());
    }

    /**
     * Vérifie qu'un coefficient nul lève {@link IllegalArgumentException}.
     */
    @Test
    void constructionCoefficientNulLeveIAE() {
        assertThrows(IllegalArgumentException.class, () -> new Monome(0.0, 2));
    }

    /**
     * Vérifie qu'un exposant négatif lève {@link IllegalArgumentException}.
     */
    @Test
    void constructionExposantNegatifLeveIAE() {
        assertThrows(IllegalArgumentException.class, () -> new Monome(1.0, -1));
    }

    // ── Évaluation ───────────────────────────────────────────────────────────

    /**
     * Vérifie evaluer() : terme linéaire, quadratique, constant, en zéro et valeur négative.
     * Formule : coefficient * x^exposant.
     */
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
        // x négatif avec exposant pair : résultat positif (3·(-2)² = 12)
        assertEquals(12.0, monome.evaluer(-2.0), 1e-9);
    }

    // ── Multiplication par un scalaire ───────────────────────────────────────

    /**
     * Vérifie multiplierParScalaire() : facteur positif, négatif et identité (×1).
     * L'exposant ne change pas.
     */
    @Test
    void multiplierParScalaireRetourneNouveauMonome() {
        Monome parDeux = monome.multiplierParScalaire(2.0);
        assertEquals(6.0, parDeux.getCoefficient(), 1e-9);
        assertEquals(2,   parDeux.getExposant());

        Monome inverse = monome.multiplierParScalaire(-1.0);
        assertEquals(-3.0, inverse.getCoefficient(), 1e-9);
        assertEquals(2,    inverse.getExposant());

        Monome identite = monome.multiplierParScalaire(1.0);
        assertEquals(3.0, identite.getCoefficient(), 1e-9);
        assertEquals(2,   identite.getExposant());
    }

    /**
     * Vérifie qu'un facteur nul lève {@link IllegalArgumentException}.
     */
    @Test
    void multiplierParScalaireNulLeveIAE() {
        assertThrows(IllegalArgumentException.class, () -> monome.multiplierParScalaire(0.0));
    }

    // ── Multiplication de deux monômes ───────────────────────────────────────

    /**
     * Vérifie multiplier() : les coefficients sont multipliés et les exposants additionnés.
     * Couvre la multiplication par un terme non constant et par une constante.
     */
    @Test
    void multiplierDeuxMonomesFusionneExposants() {
        // (3x²) * (2x) = 6x³
        Monome produit = monome.multiplier(new Monome(2.0, 1));
        assertEquals(6.0, produit.getCoefficient(), 1e-9);
        assertEquals(3,   produit.getExposant());

        // (3x²) * (2) = 6x²  — exposant inchangé
        Monome produitConstante = monome.multiplier(new Monome(2.0, 0));
        assertEquals(6.0, produitConstante.getCoefficient(), 1e-9);
        assertEquals(2,   produitConstante.getExposant());
    }

    /**
     * Vérifie qu'un argument null lève {@link IllegalArgumentException}.
     */
    @Test
    void multiplierAvecNullLeveIAE() {
        assertThrows(IllegalArgumentException.class, () -> monome.multiplier(null));
    }

    // ── Dérivation ───────────────────────────────────────────────────────────

    /**
     * Vérifie deriver() : (c·xⁿ)' = c·n·x^(n-1) pour les degrés 1, 2 et 3.
     */
    @Test
    void deriverRetourneMonomeDerive() {
        // (3x)' = 3
        Monome d1 = new Monome(3.0, 1).deriver();
        assertEquals(3.0, d1.getCoefficient(), 1e-9);
        assertEquals(0,   d1.getExposant());

        // (3x²)' = 6x
        Monome d2 = monome.deriver();
        assertEquals(6.0, d2.getCoefficient(), 1e-9);
        assertEquals(1,   d2.getExposant());

        // (2x³)' = 6x²
        Monome d3 = new Monome(2.0, 3).deriver();
        assertEquals(6.0, d3.getCoefficient(), 1e-9);
        assertEquals(2,   d3.getExposant());
    }

    /**
     * Vérifie que dériver un terme constant lève {@link IllegalStateException}
     * (la dérivée serait nulle et ne peut pas être représentée comme Monome).
     */
    @Test
    void deriverConstanteLeveISE() {
        assertThrows(IllegalStateException.class, () -> new Monome(5.0, 0).deriver());
    }
}
