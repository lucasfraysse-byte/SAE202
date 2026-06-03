package iut.info.polynome.test;

import iut.info.polynome.DivisionEuclidienneResultat;
import iut.info.polynome.Monome;
import iut.info.polynome.Polynome;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestPolynome {

    private static Polynome creer(double... args) {
        List<Monome> termes = new ArrayList<>();
        for (int i = 0; i < args.length; i += 2) {
            termes.add(new Monome(args[i], (int) args[i + 1]));
        }
        return new Polynome(termes);
    }

    // ── Construction et Base ──────────────────────────────────────────────────

    @Test
    void constructionElimineLesZerosSousEpsilon() {
        List<Monome> termes = new ArrayList<>();
        termes.add(new Monome(1e-10, 1)); // En dessous de EPSILON (1e-9)
        termes.add(new Monome(1.0, 0));
        Polynome p = new Polynome(termes);
        assertEquals(0, p.getDegre());
    }

    @Test
    void constructionParRacines() {
        double[] racines = {1.0, -1.0};
        int[] mults = {1, 1};
        // 2(x-1)(x+1) = 2x^2 - 2
        Polynome p = new Polynome(racines, mults, 2.0);
        assertEquals(2, p.getDegre());
        assertEquals(2.0, p.getCoefficient(2), 1e-9);
        assertEquals(-2.0, p.getCoefficient(0), 1e-9);
    }

    @Test
    void getCoefficientsRetourneTableauDense() {
        Polynome p = creer(3.0, 2, 5.0, 0);
        double[] coeffs = p.getCoefficients();
        assertEquals(3, coeffs.length);
        assertEquals(5.0, coeffs[0], 1e-9);
        assertEquals(0.0, coeffs[1], 1e-9);
        assertEquals(3.0, coeffs[2], 1e-9);
    }

    // ── Analyse Mathématique (Horner, Limites, etc.) ──────────────────────────

    @Test
    void evaluerHornerCasClassiqueEtExtremes() {
        Polynome p = creer(2.0, 3, -1.0, 1, 5.0, 0); // 2x³ - x + 5
        assertEquals(19.0, p.evaluer(2.0), 1e-9);
        
        // Valeur extrême positive
        assertTrue(p.evaluer(1e50) > 1e140);
        // Valeur extrême négative
        assertTrue(p.evaluer(-1e50) < -1e140);
    }

    @Test
    void deriverEtIntegrer() {
        Polynome p = creer(3.0, 2, 2.0, 1, 5.0, 0);
        
        Polynome derivee = p.deriver();
        assertEquals("6x + 2", derivee.toString());
        
        Polynome primitive = derivee.integrer();
        // La primitive de 6x+2 est 3x^2 + 2x (sans la constante d'origine)
        assertEquals("3x^2 + 2x", primitive.toString());
    }

    @Test
    void valeurMoyenne() {
        Polynome p = creer(3.0, 2); // 3x²
        // Moyenne sur [0, 2] = (1/2) * [x^3] de 0 à 2 = (8 - 0) / 2 = 4.0
        assertEquals(4.0, p.valeurMoyenne(0.0, 2.0), 1e-9);
    }

    @Test
    void limitesEnInfini() {
        Polynome pairPos = creer(2.0, 2);
        Polynome pairNeg = creer(-2.0, 2);
        Polynome impairPos = creer(2.0, 3);
        Polynome impairNeg = creer(-2.0, 3);

        assertEquals(Double.POSITIVE_INFINITY, pairPos.getLimiteEnPlusInfini());
        assertEquals(Double.POSITIVE_INFINITY, pairPos.getLimiteEnMoinsInfini());
        
        assertEquals(Double.NEGATIVE_INFINITY, pairNeg.getLimiteEnPlusInfini());
        assertEquals(Double.NEGATIVE_INFINITY, pairNeg.getLimiteEnMoinsInfini());

        assertEquals(Double.POSITIVE_INFINITY, impairPos.getLimiteEnPlusInfini());
        assertEquals(Double.NEGATIVE_INFINITY, impairPos.getLimiteEnMoinsInfini());

        assertEquals(Double.NEGATIVE_INFINITY, impairNeg.getLimiteEnPlusInfini());
        assertEquals(Double.POSITIVE_INFINITY, impairNeg.getLimiteEnMoinsInfini());
    }

    @Test
    void multiplierParScalairePolynome() {
        Polynome p = creer(2.0, 2, 4.0, 1); // 2x^2 + 4x
        Polynome triple = p.multiplierParScalaire(3.0);
        assertEquals("6x^2 + 12x", triple.toString());
    }

    @Test
    void multiplierParScalaireZeroRetourneNul() {
        Polynome p = creer(2.0, 2, 4.0, 1);
        assertTrue(p.multiplierParScalaire(0.0).estNul());
    }

    @Test
    void parserExposantNegatifLeveIAE() {
        assertThrows(IllegalArgumentException.class, () -> Polynome.parser("2x^-2 + 3"));
    }

    @Test
    void parserCasVaries() {
        // Coefficient flottant
        Polynome p1 = Polynome.parser("3.5x^2 - 2x + 1");
        assertEquals(3.5, p1.getCoefficient(2), 1e-9);
        assertEquals(-2.0, p1.getCoefficient(1), 1e-9);
        assertEquals(1.0, p1.getCoefficient(0), 1e-9);

        // Coefficient implicite (1 et -1)
        Polynome p2 = Polynome.parser("x^3 - x");
        assertEquals(1.0,  p2.getCoefficient(3), 1e-9);
        assertEquals(-1.0, p2.getCoefficient(1), 1e-9);

        // Terme x seul, constante seule
        assertEquals(1.0, Polynome.parser("x").getCoefficient(1), 1e-9);
        assertEquals(5.0, Polynome.parser("5").getCoefficient(0), 1e-9);
    }

    // ── Suite de Sturm et Interpolation ───────────────────────────────────────

    @Test
    void suiteDeSturmEtNombreDeRacines() {
        // x^2 - 4 a deux racines : -2 et 2
        Polynome p = creer(1.0, 2, -4.0, 0);
        List<Polynome> sturm = p.suiteDeSturm();
        
        assertEquals(3, sturm.size()); // P, P', Reste
        assertEquals(2, p.getNombreRacinesReelles(-5.0, 5.0)); // Intervalle englobant
        assertEquals(1, p.getNombreRacinesReelles(0.0, 5.0));  // Uniquement racine positive
        assertEquals(0, p.getNombreRacinesReelles(5.0, 10.0)); // Hors intervalle
    }

    @Test
    void interpolerLagrangeClassiqueEtExtremes() {
        double[] x = {1.0, 2.0, 3.0};
        double[] y = {2.0, 4.0, 9.0};
        Polynome p = Polynome.interpoler(x, y);
        
        // Vérification des points
        assertEquals(2.0, p.evaluer(1.0), 1e-9);
        assertEquals(4.0, p.evaluer(2.0), 1e-9);
        assertEquals(9.0, p.evaluer(3.0), 1e-9);

        // Cas d'erreur : points x identiques
        double[] xFaux = {1.0, 1.0};
        double[] yFaux = {2.0, 3.0};
        assertThrows(IllegalArgumentException.class, () -> Polynome.interpoler(xFaux, yFaux));
    }

    // ── Opérations Algébriques ────────────────────────────────────────────────

    @Test
    void diviserDivisionExacte() {
        Polynome dividende = creer(1.0, 2, -1.0, 0); // X^2 - 1
        Polynome diviseur  = creer(1.0, 1, 1.0, 0);  // X + 1
        DivisionEuclidienneResultat resultat = dividende.diviser(diviseur);

        assertEquals("x - 1", resultat.getQuotient().toString());
        assertTrue(resultat.getReste().estNul());
    }

    @Test
    void diviserAvecReste() {
        Polynome dividende = creer(1.0, 2, 1.0, 0); // X^2 + 1
        Polynome diviseur  = creer(1.0, 1);         // X
        DivisionEuclidienneResultat resultat = dividende.diviser(diviseur);

        assertEquals("x", resultat.getQuotient().toString());
        assertEquals("1", resultat.getReste().toString());
    }

    // ── Racines réelles ───────────────────────────────────────────────────────

    @Test
    void getRacinesReellesCasSimple() {
        // x^2 - 4 = (x-2)(x+2)
        Polynome p = creer(1.0, 2, -4.0, 0);
        List<Double> racines = p.getRacinesReelles();
        assertEquals(2, racines.size());
        racines.sort(Double::compareTo);
        assertEquals(-2.0, racines.get(0), 1e-7);
        assertEquals(2.0,  racines.get(1), 1e-7);
    }

    @Test
    void getRacinesReellesAucuneRacine() {
        // x^2 + 1 : aucune racine réelle
        Polynome p = creer(1.0, 2, 1.0, 0);
        assertTrue(p.getRacinesReelles().isEmpty());
    }

    @Test
    void getRacinesReellesTroisRacines() {
        // x^3 - x = x(x-1)(x+1)
        Polynome p = creer(1.0, 3, -1.0, 1);
        List<Double> racines = p.getRacinesReelles();
        assertEquals(3, racines.size());
        racines.sort(Double::compareTo);
        assertEquals(-1.0, racines.get(0), 1e-7);
        assertEquals( 0.0, racines.get(1), 1e-7);
        assertEquals( 1.0, racines.get(2), 1e-7);
    }

    @Test
    void getMultiplicitesRacinesSimple() {
        // x^2 - 4 : deux racines simples
        Polynome p = creer(1.0, 2, -4.0, 0);
        List<double[]> mults = p.getMultiplicitesRacines();
        assertEquals(2, mults.size());
        for (double[] rm : mults) assertEquals(1, (int) rm[1]);
    }

    @Test
    void getMultiplicitesRacinesRacineDouble() {
        // x^2 - 2x + 1 = (x-1)^2 : racine double en 1
        Polynome p = creer(1.0, 2, -2.0, 1, 1.0, 0);
        List<double[]> mults = p.getMultiplicitesRacines();
        assertEquals(1, mults.size());
        assertEquals(1.0, mults.get(0)[0], 1e-5);
        assertEquals(2,  (int) mults.get(0)[1]);
    }
}