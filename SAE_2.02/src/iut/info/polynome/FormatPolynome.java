package iut.info.polynome;

/**
 * Formats de sérialisation disponibles pour la persistance d'un polynôme.
 *
 * <ul>
 *   <li>{@link #COEFFICIENTS} — représentation dense par la liste ordonnée des coefficients.</li>
 *   <li>{@link #RACINES} — représentation par le coefficient dominant et les racines avec multiplicités.</li>
 * </ul>
 *
 * @see PolynomeIO
 */
public enum FormatPolynome {

    /**
     * Format texte : {@code COEFFICIENTS:c0,c1,...,cn}
     * <p>Les coefficients sont listés par exposant croissant (c0 = coefficient de x^0).</p>
     * <p>Exemple : {@code COEFFICIENTS:1.0,0.0,3.0} représente {@code 3x^2 + 1}.</p>
     */
    COEFFICIENTS,

    /**
     * Format texte : {@code RACINES:coeffDominant:r1/m1,r2/m2,...}
     * <p>Le coefficient dominant est suivi de chaque racine et sa multiplicité.</p>
     * <p>Exemple : {@code RACINES:1.0:2.0/1,-1.0/1} représente {@code (x-2)(x+1)}.</p>
     * <p>Note : la sauvegarde dans ce format n'est pas supportée ({@link UnsupportedOperationException}) ;
     * seule la lecture l'est.</p>
     */
    RACINES
}
