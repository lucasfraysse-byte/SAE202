package iut.info.polynome;

/**
 * Formats de sérialisation disponibles pour la persistance d'un polynôme.
 *
 * <ul>
 *   <li>Représentation dense par la liste ordonnée des coefficients.</li>
 *   <li>Représentation par le coefficient dominant et les racines avec multiplicités.</li>
 * </ul>
 *
 * @see PolynomeIO
 */
public enum FormatPolynome {

    /**
     * <p>Les coefficients sont listés par exposant croissant (c0 = coefficient de x^0).</p>
     */
    COEFFICIENTS,

    /**
     * <p>Le coefficient dominant est suivi de chaque racine et sa multiplicité.</p>
     * <p>Note : la sauvegarde dans ce format n'est pas supportée ;
     * seule la lecture l'est.</p>
     */
    RACINES
}
