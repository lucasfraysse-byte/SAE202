package iut.info.polynome.test;

import iut.info.polynome.FormatPolynome;
import iut.info.polynome.Polynome;
import iut.info.polynome.PolynomeIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestPolynomeIO {

    @TempDir
    Path dossierTemporaire;

    @Test
    void roundTripCoefficients() throws IOException {
        Polynome original = Polynome.parser("3.5x^2 - 2x + 1");
        Path fichier = dossierTemporaire.resolve("poly_coeff.txt");
        
        // Sauvegarde
        PolynomeIO.sauvegarder(original, fichier.toString(), FormatPolynome.COEFFICIENTS);
        assertTrue(Files.exists(fichier));
        
        // Chargement
        Polynome charge = PolynomeIO.charger(fichier.toString());
        assertEquals(original.toString(), charge.toString());
    }

    @Test
    void chargerFormatRacines() throws IOException {
        Path fichier = dossierTemporaire.resolve("poly_racines.txt");
        // Format attendu : RACINES: coeffDominant : racine1/mult1, racine2/mult2
        // Représente 2.0 * (x - 1.0)^1 * (x + 1.0)^1 = 2x^2 - 2
        Files.writeString(fichier, "RACINES:2.0:1.0/1,-1.0/1");
        
        Polynome charge = PolynomeIO.charger(fichier.toString());
        assertEquals(2, charge.getDegre());
        assertEquals(2.0, charge.getCoefficient(2), 1e-9);
        assertEquals(-2.0, charge.getCoefficient(0), 1e-9);
    }

    @Test
    void exceptionSurSauvegardeRacines() {
        Polynome p = Polynome.parser("x^2");
        Path fichier = dossierTemporaire.resolve("impossible.txt");
        
        // La sauvegarde au format racines nécessite de conserver l'historique de construction (non implémenté comme requis)
        assertThrows(UnsupportedOperationException.class, 
            () -> PolynomeIO.sauvegarder(p, fichier.toString(), FormatPolynome.RACINES));
    }

    @Test
    void exceptionFichierCorrompuOuInvalide() throws IOException {
        Path fichier = dossierTemporaire.resolve("corrompu.txt");
        Files.writeString(fichier, "FORMAT_INCONNU:1,2,3");

        assertThrows(IllegalArgumentException.class, () -> PolynomeIO.charger(fichier.toString()));
    }

    @Test
    void sauvegarderTousEtChargerTous() throws IOException {
        Polynome p1 = Polynome.parser("x^2 + 1");
        Polynome p2 = Polynome.parser("3x - 2");
        Polynome p3 = Polynome.parser("x^3");
        Path fichier = dossierTemporaire.resolve("tous.txt");

        PolynomeIO.sauvegarderTous(List.of(p1, p2, p3), fichier.toString(), FormatPolynome.COEFFICIENTS);
        assertTrue(Files.exists(fichier));

        List<Polynome> charges = PolynomeIO.chargerTous(fichier.toString());
        assertEquals(3, charges.size());
        assertEquals(p1.toString(), charges.get(0).toString());
        assertEquals(p2.toString(), charges.get(1).toString());
        assertEquals(p3.toString(), charges.get(2).toString());
    }

    @Test
    void chargerTousFichierVide() throws IOException {
        Path fichier = dossierTemporaire.resolve("vide.txt");
        Files.writeString(fichier, "");

        List<Polynome> charges = PolynomeIO.chargerTous(fichier.toString());
        assertTrue(charges.isEmpty());
    }
}