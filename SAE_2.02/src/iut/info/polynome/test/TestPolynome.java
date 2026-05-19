package iut.info.polynome.test;

import iut.info.polynome.Monome;
import iut.info.polynome.Polynome;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires de {@link Polynome}.
 *
 * <p>Couvre : construction, degré, coefficient, {@code toString}, parser,
 * {@code estNul}, {@code additionner}, {@code multiplier} et {@code diviser}.</p>
 */
class TestPolynome {

    // ── Méthodes utilitaires privées ──────────────────────────────────────────

    /**
     * Construit un polynôme à partir de paires (coefficient, exposant).
     * Les paires doivent être données dans l'ordre : coeff0, exp0, coeff1, exp1, …
     */
    private static Polynome creer(double... args) {
        List<Monome> termes = new ArrayList<>();
        for (int i = 0; i < args.length; i += 2) {
            termes.add(new Monome(args[i], (int) args[i + 1]));
        }
        return new Polynome(termes);
    }

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
        assertEquals("x^2 + 3x + 3", new Polynome(termes).toString());
    }

    @Test
    void toStringAvecCoefficientsNegatifs() {
        List<Monome> termes = new ArrayList<>();
        termes.add(new Monome(-1.0, 2));
        termes.add(new Monome(-3.0, 1));
        assertEquals("-x^2 - 3x", new Polynome(termes).toString());
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
        assertEquals("3.5x^2 + 2", new Polynome(termes).toString());
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

    // ── multiplierParScalaire ─────────────────────────────────────────────────

    @Test
    void multiplierParScalaireDoubleLeCoefficient() {
        // (x^2 + 3x) * 2 = 2x^2 + 6x
        Polynome p = creer(1.0, 2, 3.0, 1);
        Polynome resultat = p.multiplierParScalaire(2.0);

        assertEquals(2, resultat.getDegre());
        assertEquals(2.0, resultat.getCoefficient(2), 1e-9);
        assertEquals(6.0, resultat.getCoefficient(1), 1e-9);
    }

    @Test
    void multiplierParScalaireNegatifInverseLesSigles() {
        // (x^2 - 1) * (-1) = -x^2 + 1
        Polynome p = creer(1.0, 2, -1.0, 0);
        Polynome resultat = p.multiplierParScalaire(-1.0);

        assertEquals(2, resultat.getDegre());
        assertEquals(-1.0, resultat.getCoefficient(2), 1e-9);
        assertEquals(1.0,  resultat.getCoefficient(0), 1e-9);
    }

    @Test
    void multiplierParScalaireUnDonnePolynomeIdentique() {
        // P * 1 = P
        Polynome p = creer(3.0, 3, -2.0, 1, 5.0, 0);
        Polynome resultat = p.multiplierParScalaire(1.0);

        assertEquals(p.getDegre(), resultat.getDegre());
        for (int exp = 0; exp <= p.getDegre(); exp++) {
            assertEquals(p.getCoefficient(exp), resultat.getCoefficient(exp), 1e-9);
        }
    }

    @Test
    void multiplierParScalaireDecimal() {
        // (2x) * 0.5 = x
        Polynome p = creer(2.0, 1);
        Polynome resultat = p.multiplierParScalaire(0.5);

        assertEquals(1, resultat.getDegre());
        assertEquals(1.0, resultat.getCoefficient(1), 1e-9);
    }

    @Test
    void multiplierParScalairePolynomeNulResteNul() {
        // 0 * scalaire = 0
        Polynome nul = new Polynome(new ArrayList<>());
        Polynome resultat = nul.multiplierParScalaire(5.0);

        assertTrue(resultat.estNul());
    }

    @Test
    void multiplierParScalaireNulLeveIAE() {
        Polynome p = creer(1.0, 2);
        assertThrows(IllegalArgumentException.class, () -> p.multiplierParScalaire(0.0));
        List<Monome> termes = new ArrayList<>();
        termes.add(new Monome(1.0, 2));
        assertFalse(new Polynome(termes).estNul());
    }

    // ── additionner ───────────────────────────────────────────────────────────

    @Test
    void additionnerDeuxPolynomesClassiques() {
        // (X^2 + 3X) + (2X + 1) = X^2 + 5X + 1
        Polynome p = creer(1.0, 2, 3.0, 1);
        Polynome q = creer(2.0, 1, 1.0, 0);
        Polynome somme = p.additionner(q);

        assertEquals(2, somme.getDegre());
        assertEquals(1.0, somme.getCoefficient(2), 1e-9);
        assertEquals(5.0, somme.getCoefficient(1), 1e-9);
        assertEquals(1.0, somme.getCoefficient(0), 1e-9);
    }

    @Test
    void additionnerAvecAnnulation() {
        // (X^2 + X) + (-X^2) = X
        Polynome p = creer(1.0, 2, 1.0, 1);
        Polynome q = creer(-1.0, 2);
        Polynome somme = p.additionner(q);

        assertEquals(1, somme.getDegre());
        assertEquals(0.0, somme.getCoefficient(2), 1e-9);
        assertEquals(1.0, somme.getCoefficient(1), 1e-9);
    }

    @Test
    void additionnerAvecPolynomeNulDonneThis() {
        // P + 0 = P
        Polynome p = creer(1.0, 2, 3.0, 0);
        Polynome nul = new Polynome(new ArrayList<>());
        Polynome somme = p.additionner(nul);

        assertEquals(p.getDegre(), somme.getDegre());
        assertEquals(1.0, somme.getCoefficient(2), 1e-9);
        assertEquals(3.0, somme.getCoefficient(0), 1e-9);
    }

    @Test
    void additionnerNullLeveIAE() {
        Polynome p = creer(1.0, 2);
        assertThrows(IllegalArgumentException.class, () -> p.additionner(null));
    }

    @Test
    void additionnerDonnePolynomeNulQuandOpposés() {
        // P + (-P) = 0
        Polynome p = creer(3.0, 2, -1.0, 0);
        Polynome moins_p = creer(-3.0, 2, 1.0, 0);
        Polynome somme = p.additionner(moins_p);

        assertTrue(somme.estNul());
    }

    @Test
    void additionnerEstCommutatif() {
        // P + Q doit donner le même résultat que Q + P
        Polynome p = creer(2.0, 3, 1.0, 1);
        Polynome q = creer(4.0, 2, -1.0, 0);

        Polynome pq = p.additionner(q);
        Polynome qp = q.additionner(p);

        assertEquals(pq.getDegre(), qp.getDegre());
        for (int exp = 0; exp <= pq.getDegre(); exp++) {
            assertEquals(pq.getCoefficient(exp), qp.getCoefficient(exp), 1e-9);
        }
    }

    // ── multiplier ────────────────────────────────────────────────────────────

    @Test
    void multiplierDeuxBinomes() {
        // (X + 1) * (X - 1) = X^2 - 1
        Polynome p = creer(1.0, 1, 1.0, 0);
        Polynome q = creer(1.0, 1, -1.0, 0);
        Polynome produit = p.multiplier(q);

        assertEquals(2, produit.getDegre());
        assertEquals(1.0,  produit.getCoefficient(2), 1e-9);
        assertEquals(0.0,  produit.getCoefficient(1), 1e-9);
        assertEquals(-1.0, produit.getCoefficient(0), 1e-9);
    }

    @Test
    void multiplierParMonome() {
        // (X^2 + 2X + 1) * (2X) = 2X^3 + 4X^2 + 2X
        Polynome p = creer(1.0, 2, 2.0, 1, 1.0, 0);
        Polynome q = creer(2.0, 1);
        Polynome produit = p.multiplier(q);

        assertEquals(3, produit.getDegre());
        assertEquals(2.0, produit.getCoefficient(3), 1e-9);
        assertEquals(4.0, produit.getCoefficient(2), 1e-9);
        assertEquals(2.0, produit.getCoefficient(1), 1e-9);
        assertEquals(0.0, produit.getCoefficient(0), 1e-9);
    }

    @Test
    void multiplierParPolynomeNulDonneNul() {
        // P * 0 = 0
        Polynome p = creer(3.0, 2, 1.0, 0);
        Polynome nul = new Polynome(new ArrayList<>());
        assertTrue(p.multiplier(nul).estNul());
    }

    @Test
    void multiplierNullLeveIAE() {
        Polynome p = creer(1.0, 2);
        assertThrows(IllegalArgumentException.class, () -> p.multiplier(null));
    }

    @Test
    void multiplierEstCommutatif() {
        // P * Q = Q * P
        Polynome p = creer(2.0, 2, 1.0, 0);
        Polynome q = creer(1.0, 1, 3.0, 0);

        Polynome pq = p.multiplier(q);
        Polynome qp = q.multiplier(p);

        assertEquals(pq.getDegre(), qp.getDegre());
        for (int exp = 0; exp <= pq.getDegre(); exp++) {
            assertEquals(pq.getCoefficient(exp), qp.getCoefficient(exp), 1e-9);
        }
    }

    // ── diviser ───────────────────────────────────────────────────────────────

    @Test
    void diviserDivisionExacte() {
        // (X^2 - 1) / (X + 1)  =>  quotient = X - 1, reste = 0
        Polynome dividende = creer(1.0, 2, -1.0, 0);
        Polynome diviseur  = creer(1.0, 1, 1.0, 0);
        Polynome[] resultat = dividende.diviser(diviseur);

        Polynome quotient = resultat[0];
        Polynome reste    = resultat[1];

        // Quotient : X - 1
        assertEquals(1, quotient.getDegre());
        assertEquals(1.0,  quotient.getCoefficient(1), 1e-9);
        assertEquals(-1.0, quotient.getCoefficient(0), 1e-9);
        // Reste nul
        assertTrue(reste.estNul());
    }

    @Test
    void diviserAvecReste() {
        // (X^2 + 1) / X  =>  quotient = X, reste = 1
        Polynome dividende = creer(1.0, 2, 1.0, 0);
        Polynome diviseur  = creer(1.0, 1);
        Polynome[] resultat = dividende.diviser(diviseur);

        Polynome quotient = resultat[0];
        Polynome reste    = resultat[1];

        // Quotient : X
        assertEquals(1, quotient.getDegre());
        assertEquals(1.0, quotient.getCoefficient(1), 1e-9);
        // Reste : 1
        assertEquals(0, reste.getDegre());
        assertEquals(1.0, reste.getCoefficient(0), 1e-9);
    }

    @Test
    void diviserDividendeDeDegreMoindre() {
        // X / X^2  => quotient = 0, reste = X
        Polynome dividende = creer(1.0, 1);
        Polynome diviseur  = creer(1.0, 2);
        Polynome[] resultat = dividende.diviser(diviseur);

        assertTrue(resultat[0].estNul());
        assertEquals(1.0, resultat[1].getCoefficient(1), 1e-9);
    }

    @Test
    void diviserVerificationEgaliteDividende() {
        // Vérifie que dividende = diviseur * quotient + reste
        Polynome dividende = creer(2.0, 3, -3.0, 2, 1.0, 0);
        Polynome diviseur  = creer(1.0, 1, -1.0, 0);
        Polynome[] resultat = dividende.diviser(diviseur);

        Polynome reconstruit = diviseur.multiplier(resultat[0]).additionner(resultat[1]);

        // Le degré doit être identique
        assertEquals(dividende.getDegre(), reconstruit.getDegre());
        for (int exp = 0; exp <= dividende.getDegre(); exp++) {
            assertEquals(dividende.getCoefficient(exp), reconstruit.getCoefficient(exp), 1e-9);
        }
    }

    @Test
    void diviserParNullLeveIAE() {
        Polynome p = creer(1.0, 2);
        assertThrows(IllegalArgumentException.class, () -> p.diviser(null));
    }

    @Test
    void diviserParPolynomeNulLeveIAE() {
        Polynome p = creer(1.0, 2);
        Polynome nul = new Polynome(new ArrayList<>());
        assertThrows(IllegalArgumentException.class, () -> p.diviser(nul));
    }
}
