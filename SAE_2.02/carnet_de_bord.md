# Carnet de bord — Bibliothèque de polynômes réels

---

## Étape 0 — Initialisation du projet (2026-04-30)

### Fonctionnalité concernée
Mise en place de la structure du projet et de l'environnement de test.

### Choix effectués

**Structure du projet**
- Sources : `src/iut/info/polynome/`
- Tests : `src/iut/info/polynome/test/`
- Nommage des classes de test : `Test<NomDeClasse>` (ex. `TestMonome`)

**Outil de build**
- Aucun outil de build externe (pas de Maven ni Gradle)
- JUnit 5 intégré via la bibliothèque Eclipse (`org.eclipse.jdt.junit.JUNIT_CONTAINER/5`)

**Module system**
- Suppression de `module-info.java` et de l'attribut `module=true` dans `.classpath`
- Raison : le module system Java complique l'accès de JUnit aux classes de test sans configuration avancée inutile pour ce projet

**Renommage des stubs**
- `monome.java` → `Monome.java`
- `polynome.java` → `Polynome.java`
- `divisionEuclidienneResultat.java` → `DivisionEuclidienneResultat.java`
- Raison : nommage PascalCase obligatoire en Java

### Impacts
- Le fichier `.classpath` est modifié : JUnit 5 est désormais disponible dans tout le projet
- Les anciens stubs vides sont renommés, leur contenu sera écrit au fil du TDD

---

## Étape 1 — Classe `Monome` (2026-04-30)

### Fonctionnalité concernée
Construction d'un monôme avec coefficient et exposant, avec validation des entrées.

### Tests associés (`TestMonome`)
| Test | Comportement attendu |
|---|---|
| `constructionAvecCoefficientEtExposant` | getters retournent les valeurs passées |
| `constructionMonomeConstante` | exposant = 0 est valide |
| `constructionAvecCoefficientNegatif` | coefficient négatif est valide |
| `constructionAvecCoefficientNulLeveUneException` | `IllegalArgumentException` si coefficient = 0 |
| `constructionAvecExposantNegatifLeveUneException` | `IllegalArgumentException` si exposant < 0 |

### Choix effectués

**Immuabilité**
- Les champs `coefficient` et `exposant` sont `final`
- Raison : un monôme ne change pas après création ; l'immuabilité évite les effets de bord

**Validation du coefficient nul dans `Monome`**
- Le rejet du coefficient nul est fait dans `Monome` et non uniquement dans `Polynome`
- Raison : un monôme à coefficient nul est mathématiquement sans sens ; mieux vaut échouer tôt

**Validation de l'exposant négatif**
- Un exposant négatif donnerait une fraction rationnelle, pas un polynôme
- Raison : hors du domaine mathématique visé

**Comparaison avec == 0.0**
- La comparaison `coefficient == 0.0` est utilisée (et non une tolérance epsilon)
- Raison : on valide une entrée explicitement passée à 0 par l'appelant ; les zéros issus de calculs internes seront traités à un niveau supérieur (normalisation dans `Polynome`)

### Alternatives envisagées
- Valider uniquement dans `Polynome` : rejeté car cela permettrait de créer des `Monome` incohérents en dehors du contexte de `Polynome`

### Implémentation minimale retenue
- Deux champs `final`, un constructeur avec deux gardes, deux getters
- Aucune autre méthode pour l'instant (TDD : on n'écrit que ce qui est nécessaire)

### Ce qu'il reste à faire
- `evaluer(x)`, `deriver()`, `multiplier(autre)`, `multiplierParScalaire(facteur)` dans `Monome`
- Toute la classe `Polynome`
- `DivisionEuclidienneResultat`
- `FormatPolynome`
- `PolynomeIO`

---

## Étape 2 — Construction de `Polynome` par tableau de coefficients (2026-04-30)

### Fonctionnalité concernée
Constructeur public `Polynome(double[] coefficients)` et les méthodes `getDegre()` et `getCoefficient(int)`.

### Tests associés (`TestPolynome`)
| Test | Comportement attendu |
|---|---|
| `constructionAvecTableauVideDonnePolynomeNul` | degré = -1 |
| `constructionAvecTousCoefficientsNulsDonnePolynomeNul` | degré = -1 |
| `constructionPolynomeConstant` | degré = 0, coefficient[0] correct |
| `constructionPolynomeDegre1` | degré = 1, coefficients corrects |
| `constructionPolynomeDegre2AvecCoefficientNulIntermediaire` | degré = 2, zéro intermédiaire accessible via getCoefficient |
| `constructionAvecZerosDeFinIgnores` | degré ramené au vrai degré mathématique |
| `constructionAvecTableauNullLeveUneException` | `IllegalArgumentException` |
| `getCoefficientPourExposantAbsentRetourneZero` | retourne 0.0 sans exception |

### Choix effectués

**Convention du tableau : index = degré (ordre croissant)**
- `coefficients[i]` = coefficient de x^i
- Raison : plus naturel pour l'indexation programmatique ; le degré d'un terme correspond directement à son indice

**Degré du polynôme nul = -1**
- Raison : convention mathématique courante pour distinguer le polynôme nul de la constante non nulle (degré 0)

**Constructeur privé `Polynome(List<Monome>)`**
- Utilisé en interne pour construire les résultats des opérations (addition, multiplication, etc.)
- Raison : évite de re-valider une liste déjà normalisée, et empêche l'utilisateur de passer une liste arbitraire

**Stockage interne : `List<Monome>` triée par exposant décroissant, sans zéros**
- Correspond directement aux invariants du diagramme UML
- Raison : le premier élément est toujours le terme dominant → `getDegre()` est O(1)

**`getCoefficient(int exposant)` retourne 0.0 pour un exposant absent**
- Raison : cohérent avec la représentation mathématique (coefficient absent = 0) et évite les vérifications null chez l'appelant

### Ce qu'il reste à faire
- `getCoefficients(): double[]` dans `Polynome`
- `getLimiteEnMoinsInfini()`, `getLimiteEnPlusInfini()`
- `getRacinesReelles()`, `getMultiplicitesRacines()`
- `additionner()`, `multiplierParScalaire()`, `multiplier()`, `diviserPar()`
- `deriver()`, `integrer()`, `valeurMoyenne()`
- Méthodes de `Monome` : `deriver`, `multiplier`, `multiplierParScalaire`
- `DivisionEuclidienneResultat`, `FormatPolynome`, `PolynomeIO`

---

## Étape 3 — Évaluation : `Monome.evaluer()` et `Polynome.evaluer()` (2026-04-30)

### Fonctionnalité concernée
Évaluation d'un polynôme en un point réel.

### Tests associés

**`TestMonome` — nouveaux tests :**
| Test | Valeur attendue |
|---|---|
| `evaluerMonomeLineaire` | 2·3¹ = 6.0 |
| `evaluerMonomeQuadratique` | 3·2² = 12.0 |
| `evaluerMonomeConstante` | 5·100⁰ = 5.0 |
| `evaluerEnZero` | 3·0² = 0.0 |
| `evaluerAvecValeurNegative` | 3·(-2)² = 12.0 |

**`TestPolynome` — nouveaux tests :**
| Test | Valeur attendue |
|---|---|
| `evaluerPolynomeNulRetourneZero` | 0.0 |
| `evaluerPolynomeConstant` | 7.0 |
| `evaluerPolynomeDegre1` | 2·4+3 = 11.0 |
| `evaluerPolynomeDegre2Dense` | 3²-1 = 8.0 |
| `evaluerPolynomeCreuxAvecTermesManquants` | 2·2³+1 = 17.0 |
| `evaluerEnZeroRetourneCoefficientConstant` | 1.0 |

### Choix effectués

**`Monome.evaluer()` : `coefficient * Math.pow(valeurX, exposant)`**
- Raison : formule directe, `Math.pow(x, 0) = 1.0` en Java (convention correcte pour les polynômes)

**`Polynome.evaluer()` : Horner généralisé pour polynômes creux**
- Trois approches envisagées : somme naive, Horner dense, Horner creux
- Retenu : Horner creux — seule option combinant l'algorithme de Horner (exigé) et la cohérence avec le stockage creux (pas de tableau intermédiaire)
- Complexité : O(n) en temps, O(1) en espace supplémentaire

**Principe du Horner généralisé :**
```
résultat = coefficient_dominant
pour chaque terme suivant (exposant décroissant) :
    écart = exposant_précédent - exposant_courant
    résultat = résultat * x^écart + coefficient_courant
si dernier exposant > 0 :
    résultat = résultat * x^dernier_exposant
```

**Tolérance `1e-9` dans les assertions doubles**
- Raison : bonne pratique pour les comparaisons de doubles, même si les cas de test sont exacts

### Ce qu'il reste à faire
- `getCoefficients(): double[]`
- `getLimiteEnMoinsInfini()`, `getLimiteEnPlusInfini()`
- `deriver()` (sur `Monome` puis `Polynome`)
- `integrer()`, `valeurMoyenne()`
- `multiplierParScalaire()`, `multiplier()`, `diviserPar()`
- Constructeur par racines
- `DivisionEuclidienneResultat`, `FormatPolynome`, `PolynomeIO`

---

## Étape 4 — `Polynome.additionner()` (2026-04-30)

### Fonctionnalité concernée
Addition de deux polynômes, retournant un nouveau polynôme normalisé.

### Tests associés (`TestPolynome`)
| Test | Cas vérifié |
|---|---|
| `additionnerDeuxPolynomesSimples` | (x+1)+(x+2) = 2x+3 |
| `additionnerTermesDeMemeExposant` | 3x²+2x² = 5x² |
| `additionnerTermesDExposantsDifferents` | x²+x = x²+x |
| `additionnerCoefficientsOpposésDonnePolynomeNul` | x+(-x) = polynôme nul |
| `additionnerAvecPolynomeNulRetournePolynomeInitial` | p+0 = p |
| `additionnerDeuxPolynomesNulsDonnePolynomeNul` | 0+0 = polynôme nul |
| `additionnerAvecArgumentNullLeveUneException` | `IllegalArgumentException` si null |

### Choix effectués

**Algorithme de fusion par double pointeur (style merge sort)**
- Trois options envisagées : double pointeur O(n+m), Map O((n+m)log(n+m)), concaténation+tri O((n+m)log(n+m))
- Retenu : double pointeur — exploite le tri existant, O(n+m) sans allocation intermédiaire

**Filtre des coefficients nuls résultants**
- Si la somme de deux coefficients de même exposant vaut 0.0, le terme n'est pas ajouté
- Raison : maintenir l'invariant « pas de coefficient nul stocké »

**Accès à `autre.termes` depuis `additionner()`**
- Java autorise l'accès aux membres `private` d'une autre instance de la même classe
- Raison : évite d'exposer `termes` via un getter public inutile

**Réutilisation du constructeur privé `Polynome(List<Monome>)`**
- La liste produite par le double pointeur est déjà triée et sans zéros
- Raison : pas de revalidation inutile

### Ce qu'il reste à faire
- `multiplierParScalaire()`, `multiplier()`, `diviserPar()`
- `deriver()`, `integrer()`, `valeurMoyenne()`
- `getCoefficients()`, limites, racines
- Constructeur par racines
- `DivisionEuclidienneResultat`, `FormatPolynome`, `PolynomeIO`

---

## Étape 5 — `multiplierParScalaire()` sur `Monome` et `Polynome` (2026-04-30)

### Fonctionnalité concernée
Multiplication d'un polynôme (et d'un monôme) par un scalaire réel.

### Tests associés

**`TestMonome` — nouveaux tests :**
| Test | Cas |
|---|---|
| `multiplierParScalairePositif` | 3·x²  × 2 → coefficient = 6 |
| `multiplierParScalaireNegatif` | 3·x² × (-1) → coefficient = -3 |
| `multiplierParScalaireUnRetourneMemeValeurs` | × 1 → identité |
| `multiplierParScalaireNulLeveUneException` | facteur = 0 → `IllegalArgumentException` |

**`TestPolynome` — nouveaux tests :**
| Test | Cas |
|---|---|
| `multiplierParScalairePositif` | (2x+1)×3 = 6x+3 |
| `multiplierParScalaireNegatif` | (x+1)×(-1) = -x-1 |
| `multiplierParScalaireUnRetourneMemeValeurs` | ×1 = identité |
| `multiplierParScalaireZeroDonnePolynomeNul` | p×0 = polynôme nul |
| `multiplierPolynomeNulParScalaireDonnePolynomeNul` | 0×5 = polynôme nul |

### Choix effectués

**Asymétrie du traitement de facteur = 0**
- `Monome.multiplierParScalaire(0)` → `IllegalArgumentException` : un monôme ne peut pas avoir un coefficient nul
- `Polynome.multiplierParScalaire(0)` → polynôme nul : comportement mathématique correct (p × 0 = 0)
- Raison : les invariants opèrent à des niveaux différents

**`Polynome.multiplierParScalaire` délègue à `Monome.multiplierParScalaire`**
- Raison : évite la duplication de la multiplication de coefficient, respecte le principe de responsabilité unique

**Cas `termes.isEmpty()` traité avant la boucle**
- Raison : le polynôme nul multiplié par n'importe quel scalaire reste nul, et évite une boucle inutile

**NaN / Infini : non traités pour l'instant**
- Un facteur NaN produirait des coefficients NaN ; un facteur Infini produirait des coefficients infinis
- Ces cas seront adressés si le besoin se manifeste ; une garde `Double.isFinite()` pourrait être ajoutée

### Ce qu'il reste à faire
- `multiplier(Polynome autre)`, `diviserPar(Polynome diviseur)`
- `deriver()`, `integrer()`, `valeurMoyenne()`
- `getCoefficients()`, limites en ±∞, racines réelles
- Constructeur par racines
- `DivisionEuclidienneResultat`, `FormatPolynome`, `PolynomeIO`

---

## Étape 6 — `Monome.multiplier()` et `Polynome.multiplier()` (2026-04-30)

### Fonctionnalité concernée
Multiplication de deux polynômes, retournant un nouveau polynôme normalisé.

### Tests associés

**`TestMonome` — nouveaux tests :**
| Test | Cas |
|---|---|
| `multiplierDeuxMonomes` | (3x²)·(2x) = 6x³ |
| `multiplierParMonomeConstante` | (3x²)·(2) = 6x² |
| `multiplierAvecMonomeNullLeveUneException` | null → `IllegalArgumentException` |

**`TestPolynome` — nouveaux tests :**
| Test | Cas |
|---|---|
| `multiplierDeuxPolynomesSimples` | (x+1)·(x+2) = x²+3x+2 |
| `multiplierParPolynomeConstante` | (x+1)·2 = 2x+2 |
| `multiplierParPolynomeNulDonnePolynomeNul` | p·0 = polynôme nul |
| `multiplierEstCommutatif` | (x+1)·(x-1) = (x-1)·(x+1) |
| `multiplierAvecArgumentNullLeveUneException` | null → `IllegalArgumentException` |

### Choix effectués

**Algorithme double boucle + TreeMap**
- Trois approches envisagées : TreeMap O(n·m·log(n·m)), additionner-accumulateur O(n·m·(n+m)), Karatsuba O(n^1.585)
- Retenu : TreeMap — simple, efficace pour les tailles réalistes, maintient le tri et fusionne les exposants naturellement

**Délégation à `Monome.multiplier(Monome)`**
- Chaque produit de paire est calculé par `monomeThis.multiplier(monomeAutre)`
- Raison : responsabilité unique, évite de dupliquer la logique de multiplication de coefficient+exposant dans `Polynome`

**`getOrDefault` plutôt que `merge`**
- Raison : plus explicite sur l'intention (lire la valeur existante, ajouter, remettre) ; évite la dépendance à un lambda `Double::sum` qui peut surprendre à la lecture

**`descendingMap().entrySet()` pour l'ordre décroissant**
- La `TreeMap` est en ordre croissant par défaut ; `descendingMap()` retourne une vue inverse sans copie
- Raison : les termes doivent être stockés par exposant décroissant (invariant)

**Imports ajoutés : `java.util.Map`, `java.util.TreeMap`**
- Classes standard Java, aucune dépendance externe

### Ce qu'il reste à faire
- `diviserPar(Polynome diviseur)` + `DivisionEuclidienneResultat`
- `deriver()`, `integrer()`, `valeurMoyenne()`
- `getCoefficients()`, limites en ±∞, racines réelles
- Constructeur par racines
- `FormatPolynome`, `PolynomeIO`

---

## Étape 7 — `Monome.deriver()` et `Polynome.deriver()` (2026-04-30)

### Fonctionnalité concernée
Dérivation d'un polynôme : application de la règle (c·xⁿ)' = c·n·x^(n-1).

### Tests associés

**`TestMonome` — nouveaux tests :**
| Test | Cas |
|---|---|
| `deriverMonomeDegre1` | (3x)' = 3 |
| `deriverMonomeDegre2` | (3x²)' = 6x |
| `deriverMonomeDegreN` | (2x³)' = 6x² |
| `deriverMonomeConstanteLeveUneException` | (5)' → `IllegalStateException` |

**`TestPolynome` — nouveaux tests :**
| Test | Cas |
|---|---|
| `deriverPolynomeDegre1` | (2x+3)' = 2 |
| `deriverPolynomeDegre2` | (x²+2x+1)' = 2x+2 |
| `deriverPolynomeNulDonnePolynomeNul` | 0' = 0 |
| `deriverConstanteDonnePolynomeNul` | (5)' = polynôme nul |
| `deriverPolynomeCreux` | (x³+1)' = 3x² |

### Choix effectués

**`Monome.deriver()` lève `IllegalStateException` pour exposant = 0**
- Quatre options envisagées : IllegalStateException, null, Optional<Monome>, aucune impl sur Monome
- Retenu : IllegalStateException — explicite, cohérent avec les gardes existantes, préserve la responsabilité de `Monome`
- `IllegalStateException` (et non `IllegalArgumentException`) car le problème vient de l'état de l'objet, pas d'un argument invalide

**`Polynome.deriver()` filtre les termes constants avant d'appeler `monome.deriver()`**
- Les termes d'exposant 0 disparaissent mathématiquement à la dérivation
- Le filtre `exposant > 0` est la traduction directe de cette règle
- La liste résultante est déjà triée (les exposants décroissants restent décroissants après -1)

**Aucune allocation inutile**
- `Polynome.deriver()` n'a pas besoin de normaliser : l'ordre est préservé et aucun coefficient ne peut devenir nul (c·n ≠ 0 si c ≠ 0 et n ≥ 1)

### Ce qu'il reste à faire
- `integrer()`, `valeurMoyenne()`
- `diviserPar()` + `DivisionEuclidienneResultat`
- `getCoefficients()`, limites en ±∞, racines réelles
- Constructeur par racines
- `FormatPolynome`, `PolynomeIO`

---

## Étape 8 — `Polynome.integrer()` et `Polynome.valeurMoyenne()` (2026-04-30)

### Fonctionnalité concernée
Primitive d'un polynôme (constante = 0) et valeur moyenne sur un intervalle.

### Tests associés (`TestPolynome`)
| Test | Cas |
|---|---|
| `integrerPolynomeConstant` | ∫3dx = 3x |
| `integrerPolynomeDegre1` | ∫(2x+6)dx = x²+6x |
| `integrerPolynomeDegre2` | ∫(3x²+2x+1)dx = x³+x²+x |
| `integrerPolynomeNulDonnePolynomeNul` | ∫0dx = polynôme nul |
| `integrerEstInverseDeDeriver` | (p.integrer()).deriver() == p |
| `valeurMoyennePolynomeConstant` | moy(5) sur [0,1] = 5 |
| `valeurMoyennePolynomeLineaire` | moy(x) sur [0,2] = 1 |
| `valeurMoyennePolynomeDegre2` | moy(x²) sur [0,3] = 3 |
| `valeurMoyenneAvecBornesEgalesLeveUneException` | a=b → `IllegalArgumentException` |

### Choix effectués

**`integrer()` : formule exacte terme à terme**
- Option numérique rejetée : inutile, imprécise, complexe
- `n+1` est toujours ≥ 1 (n ≥ 0 par invariant `Monome`) → aucun risque de division par zéro
- L'ordre des termes est préservé (n+1 > m+1 si n > m), liste déjà triée, pas de normalisation nécessaire
- Le coefficient c/(n+1) est non nul si c ≠ 0 → invariant `Monome` maintenu

**`valeurMoyenne()` : formule analytique (P(b) − P(a)) / (b − a)**
- Réutilise `integrer()` et `evaluer()` — aucune duplication de logique
- `borneInferieure == borneSuperieure` → `IllegalArgumentException` (division par zéro)
- `borneInferieure > borneSuperieure` → autorisé : la formule reste mathématiquement valide

**Choix du cas test `integrerEstInverseDeDeriver` : coefficients entiers pairs**
- Raison : éviter les erreurs d'arrondi flottant (ex. 1.0/3.0 × 3 ≠ 1.0 exactement en IEEE 754)
- Le test utilise p = 2x + 6 dont les coefficients intègrent et dérivent sans perte de précision

### Ce qu'il reste à faire
- `diviserPar()` + `DivisionEuclidienneResultat`
- `getCoefficients()`, limites en ±∞, racines réelles
- Constructeur par racines
- `FormatPolynome`, `PolynomeIO`

---

## Étape 9 — `getCoefficients()`, `getLimiteEnPlusInfini()`, `getLimiteEnMoinsInfini()` (2026-04-30)

### Fonctionnalité concernée
Accesseurs : reconstruction du tableau de coefficients dense et calcul des limites aux infinis.

### Tests associés (`TestPolynome`)
| Test | Cas |
|---|---|
| `getCoefficientsPolynomeNulRetourneTableauVide` | tableau de longueur 0 |
| `getCoefficientsPolynomeConstant` | [5.0] |
| `getCoefficientsPolynomeDense` | x²+2x+3 → [3,2,1] |
| `getCoefficientsPolynomeCreuxContientZerosIntermediaires` | x²+1 → [1,0,1] |
| `getCoefficientsEstInverseDeConstructeur` | round-trip constructor ↔ getCoefficients |
| `limiteEnPlusInfiniPolynomeNul` | 0.0 |
| `limiteEnPlusInfiniPolynomeConstant` | c |
| `limiteEnPlusInfiniCoefficientDominantPositif` | +∞ |
| `limiteEnPlusInfiniCoefficientDominantNegatif` | -∞ |
| `limiteEnMoinsInfiniPolynomeNul` | 0.0 |
| `limiteEnMoinsInfiniPolynomeConstant` | c |
| `limiteEnMoinsInfiniDegrePairCoefficientPositif` | +∞ |
| `limiteEnMoinsInfiniDegreImpairCoefficientPositif` | -∞ |
| `limiteEnMoinsInfiniDegreImpairCoefficientNegatif` | +∞ |

### Choix effectués

**`getCoefficients()` : tableau initialisé à 0.0 par Java**
- Java garantit que `new double[n]` est rempli de 0.0 → pas besoin d'initialiser explicitement les termes absents
- Taille = `getDegre() + 1` ; pour le polynôme nul (degré -1), cela donne 0, soit `new double[0]`

**Polynôme nul → tableau de longueur 0**
- Cohérent avec `getDegre() = -1` (pas d'indice valide)
- Symétrique avec le constructeur : `new Polynome(new double[]{})` reconstruit le polynôme nul

**Limites : seul le terme dominant compte**
- Pour n ≥ 1, les termes de degré inférieur sont négligeables à ±∞
- O(1) : `termes.get(0)` donne directement le terme dominant (invariant de tri)

**Limites : règle de parité du degré pour -∞**
- xⁿ → +∞ si n pair, -∞ si n impair, quand x → -∞
- Traduit directement par `degre % 2 != 0` pour inverser le signe

**`Double.POSITIVE_INFINITY` / `Double.NEGATIVE_INFINITY`**
- Constantes standard Java, pas de dépendance externe

### Ce qu'il reste à faire
- `diviserPar()` + `DivisionEuclidienneResultat`
- `getRacinesReelles()`, `getMultiplicitesRacines()`
- Constructeur par racines `Polynome(double[], int[], double)`
- `FormatPolynome`, `PolynomeIO`

---

## Étape 10 — Constructeur par racines `Polynome(double[], int[], double)` (2026-04-30)

### Fonctionnalité concernée
Construction d'un polynôme à partir de ses racines réelles, leurs multiplicités et son coefficient dominant.

### Tests associés (`TestPolynome`)
| Test | Cas |
|---|---|
| `constructionParRacineSimple` | (x-2) → degré 1, coeff[1]=1, coeff[0]=-2 |
| `constructionParDeuxRacinesSimples` | (x-1)(x+1) = x²-1 |
| `constructionParRacineAvecMultiplicite2` | (x-3)² = x²-6x+9 |
| `constructionAvecCoefficientDominant` | 2x → (racine=0, coeff=2) |
| `constructionSansRacinesDonnePolynomeConstant` | racines=[], coeff=5 → constante 5 |
| `constructionParRacinesVerifieeParEvaluation` | (x-1)(x-2) : evaluer(1)=0, evaluer(2)=0 |
| `constructionParRacinesAvecRacinesNullLeveUneException` | null → `IllegalArgumentException` |
| `constructionParRacinesAvecMultiplicitesNullLeveUneException` | null → `IllegalArgumentException` |
| `constructionParRacinesAvecLongueursDifferentesLeveUneException` | longueurs ≠ → `IllegalArgumentException` |
| `constructionParRacinesAvecCoefficientDominantNulLeveUneException` | coeff=0 → `IllegalArgumentException` |
| `constructionParRacinesAvecMultiplicitePasPositiveLeveUneException` | multiplicité=0 → `IllegalArgumentException` |

### Choix effectués

**Algorithme : multiplication successive de facteurs linéaires**
- Trois approches envisagées : multiplication successive (retenue), binôme de Newton, interpolation de Lagrange
- Retenu : multiplication successive — réutilise `multiplier()` déjà testé, lisible, O(d²·log d)
- Pas de surcoût supplémentaire par rapport aux alternatives pour les degrés courants

**Raccordement au champ `final` `termes`**
- La `Polynome produit` locale est construite par des appels à `multiplier()`
- `this.termes = new ArrayList<>(produit.termes)` : copie défensive car `produit` est local (sécurité, pas strictement nécessaire)
- Accès à `produit.termes` (private) autorisé en Java dans la même classe

**Raison de `new ArrayList<>(produit.termes)` plutôt que `produit.termes` direct**
- Même si `produit` est local et sans autre référence, la copie explicite rend l'intention claire et évite tout aliasing inattendu

**Test par évaluation en les racines**
- `constructionParRacinesVerifieeParEvaluation` valide l'invariant mathématique directement : p(rᵢ) = 0
- Plus robuste qu'une vérification coefficient par coefficient pour les polynômes complexes

### Ce qu'il reste à faire
- `diviserPar()` + `DivisionEuclidienneResultat`
- `getRacinesReelles()`, `getMultiplicitesRacines()` (nécessitent un algorithme de recherche numérique)
- `FormatPolynome`, `PolynomeIO`

---

## Étape 11 — `DivisionEuclidienneResultat` et `Polynome.diviserPar()` (2026-05-01)

### Fonctionnalité concernée
Division euclidienne de polynômes : `this = diviseur · q + r` avec `deg(r) < deg(diviseur)`.

### Tests associés

**`TestDivisionEuclidienneResultat` (nouveau fichier) :**
| Test | Cas |
|---|---|
| `constructionEtAccesQuotient` | getQuotient() retourne le polynôme passé |
| `constructionEtAccesReste` | getReste() retourne le polynôme passé |
| `constructionAvecResteNul` | reste = polynôme nul |

**`TestPolynome` — nouveaux tests :**
| Test | Cas |
|---|---|
| `diviserPolynomeDegre2ParDegre1SansReste` | (x²-1)÷(x-1) = (x+1, reste=0) |
| `diviserAvecReste` | (x²+1)÷(x+1) = (x-1, reste=2) |
| `diviserPolynomeNulDonneQuotientEtResteNuls` | 0÷(x+1) = (0, 0) |
| `diviserParPolynomeDegreSuperieurDonneQuotientNul` | x÷x² = (0, x) |
| `diviserParConstante` | (2x+4)÷2 = (x+2, 0) |
| `verifierRelationEuclidienne` | diviseur·q+r == dividend |
| `diviserAvecDiviseurNullLeveUneException` | null → `IllegalArgumentException` |
| `diviserAvecDiviseurNulLeveUneException` | polynôme nul → `IllegalArgumentException` |

### Choix effectués

**Algorithme de division longue classique**
- Deux autres options envisagées : division synthétique (degré 1 seulement), pseudo-division (entiers)
- Retenu : division longue — générale, lisible, réutilise les opérations existantes

**Construction du terme quotient par constructeur privé `Polynome(List<Monome>)`**
- Évite de créer un tableau dense de taille `exposantTerme + 1` (coûteux pour les hauts degrés)
- Accès au constructeur privé autorisé depuis `diviserPar()` (même classe)

**Terminaison garantie**
- À chaque itération, `deg(reste)` diminue strictement : le terme dominant est annulé par construction
- Le nombre maximum d'itérations est `deg(this) - deg(diviseur) + 1`

**Limite documentée : arrondi flottant**
- Pour des coefficients non exactement représentables, le terme dominant peut ne pas s'annuler exactement
- Tous les tests utilisent des coefficients entiers (exactement représentables en IEEE 754)
- Une solution robuste nécessiterait un seuil epsilon pour détecter les coefficients "proches de 0"

**`verifierRelationEuclidienne` : test de propriété mathématique**
- Vérifie `diviseur·q + r == dividend` plutôt que des coefficients précis
- Plus résistant aux refactorisations internes

### Ce qu'il reste à faire
- `getRacinesReelles()`, `getMultiplicitesRacines()` (algorithme numérique, ex. Newton-Raphson)
- `FormatPolynome` (enum)
- `PolynomeIO` (persistance texte)

---

## Étape 12 — `FormatPolynome` (2026-04-30)

### Fonctionnalité concernée
Enum décrivant les deux formats de sérialisation d'un polynôme dans un fichier texte.

### Choix effectués

**Enum Java (et non une constante entière ou une String)**
- Raison : type-safe, auto-documenté, exhaustif dans les switch ; impossible de passer une valeur invalide

**Deux valeurs uniquement : `COEFFICIENTS` et `RACINES`**
- `COEFFICIENTS:c0,c1,...,cn` — format universel (tous polynômes)
- `RACINES:leadingCoeff:r1/m1,...` — format compact pour polynômes à racines réelles connues
- Aucune valeur par défaut car le choix est toujours explicite côté appelant

---

## Étape 13 — `Polynome.getRacinesReelles()` et `getMultiplicitesRacines()` (2026-04-30)

### Fonctionnalité concernée
Recherche numérique de toutes les racines réelles d'un polynôme et calcul de leurs multiplicités.

### Tests associés (`TestPolynome`)
| Test | Cas |
|---|---|
| `racinesPolynomeNulRetourneListeVide` | degré -1 → liste vide |
| `racinesConstanteRetourneListeVide` | constante non nulle → liste vide |
| `racinesPolynomeDegre1` | 3x-6 → [2.0] |
| `racinesPolynomeDegre2DeuxRacinesDistinctes` | x²-5x+6 → [2.0, 3.0] |
| `racinesPolynomeDegre2RacineDouble` | (x-2)² → [2.0] |
| `racinesPolynomeDegre2SansRacineReelle` | x²+1 → [] |
| `racinesPolynomeDegre3TroisRacines` | (x-1)(x-2)(x-3) → [1.0, 2.0, 3.0] |
| `multiplicitesDeuxRacinesSimples` | (x-1)(x-2) → {1.0:1, 2.0:1} |
| `multiplicitesRacineDouble` | (x-2)² → {2.0:2} |

### Choix effectués

**Approche récursive : dérivée + intervalles de monotonie**
- Racines de P trouvées en exploitant les racines de P' (points critiques) comme bornage
- Dans chaque intervalle monotone, au plus une racine → bisection si changement de signe
- Récursion terminée par les cas de base (degré 1 : formule directe, degré 2 : discriminant)

**Borne de Cauchy : `1 + max(|aᵢ/aₙ|)` pour i=0..n-1**
- Garantit que toutes les racines réelles sont dans (-borne, borne)
- Simple à calculer, O(n), suffisamment serrée pour les cas usuels

**Bisection (`rechercherRacineParBisection`) : 200 itérations, tolérance 1e-9**
- Robuste, simple, convergence garantie pour tous les polynômes (pas de divergence comme Newton)
- 200 itérations donnent une précision de borne/2²⁰⁰ ≈ machine epsilon pour des bornes ≤ 10⁵⁰

**Déduplication par liste triée avec tolérance 1e-7**
- Deux racines à moins de 1e-7 sont considérées identiques
- Tolérance entre EPSILON_RACINE (1e-9) et la précision des tests (1e-5) pour éviter les faux doublons

**Multiplicité : dérivées successives jusqu'à non-annulation**
- P(r)=0, P'(r)=0, ..., P^(k-1)(r)=0, P^k(r)≠0 → multiplicité k
- Tolérance plus souple (1e-6) pour les erreurs d'arrondi accumulées

**Nouveaux imports : `java.util.Collections`, `java.util.LinkedHashMap`**
- `Collections.sort` pour trier les listes de racines
- `LinkedHashMap` pour conserver l'ordre croissant des racines dans `getMultiplicitesRacines`

---

## Étape 14 — `PolynomeIO` et `TestPolynomeIO` (2026-04-30)

### Fonctionnalité concernée
Lecture et écriture de polynômes dans des fichiers texte, avec support des deux formats.

### Tests associés (`TestPolynomeIO`)
| Test | Cas |
|---|---|
| `serialiserParCoefficientsPolynomeDegre2` | `COEFFICIENTS:1.0,2.0,3.0` |
| `serialiserParCoefficientsPolynomeNul` | `COEFFICIENTS:` |
| `serialiserParCoefficientsPolynomeConstant` | `COEFFICIENTS:5.0` |
| `serialiserParRacinesDeuxRacinesSimples` | commence par `RACINES:` |
| `serialiserParRacinesPolynomeConstant` | `RACINES:7.0:` |
| `parserPolynomeDepuisCoefficients` | round-trip coefficients |
| `parserPolynomeNulDepuisCoefficients` | `COEFFICIENTS:` → degré -1 |
| `parserPolynomeDepuisRacines` | `RACINES:1.0:2.0/1,-1.0/1` → (x-2)(x+1) |
| `parserPolynomeConstantDepuisRacines` | `RACINES:7.0:` → constante 7 |
| `parserPolynomeFormatInvalide` | `INVALIDE:...` → `IllegalArgumentException` |
| `sauvegarderEtChargerCoefficients` | round-trip fichier COEFFICIENTS |
| `sauvegarderEtChargerRacines` | round-trip fichier RACINES |
| `sauvegarderTousEtChargerTousCoefficients` | liste de 2 polynômes |
| `chargerFichierInexistantLeveException` | `IOException` |

### Choix effectués

**Classe utilitaire statique (constructeur privé)**
- Toutes les méthodes sont statiques : pas d'état, pas d'instance nécessaire
- Raison : `PolynomeIO` est un service, pas une entité

**Format RACINES : `leadingCoeff:r1/m1,r2/m2,...`**
- Le coefficient dominant est explicite → permet la reconstruction exacte (à la précision numérique)
- Les racines non réelles ne sont pas représentables dans ce format (comportement documenté)
- Le polynôme nul et les constantes sont traités comme cas dégénérés (champ racines vide)

**`BufferedReader` / `BufferedWriter` avec try-with-resources**
- Fermeture garantie même en cas d'exception
- Performance amortie pour les fichiers multi-lignes (`chargerTous`/`sauvegarderTous`)

**Lignes vides ignorées dans `chargerTous`**
- `ligne.isBlank()` permet les fichiers avec lignes de séparation sans lever d'exception

---

## Étape 15 — Révision et consolidation des fichiers de test (2026-05-03)

### Fichiers modifiés

**`TestMonome.java`** (14 tests → 8 tests)
- Ajout d'un `@BeforeEach` : `new Monome(3.0, 2)` partagé (présent dans 8 tests sur 14)
- Fusion des 3 tests de construction valides en `constructionStockeCoefficientsEtExposant`
- Fusion des 5 tests `evaluer` en `evaluerCalculeValeurExacte`
- Fusion des 3 tests nominaux `multiplierParScalaire` en `multiplierParScalaireRetourneNouveauMonome`
- Fusion des 2 tests nominaux `multiplier` en `multiplierDeuxMonomesFusionneExposants`
- Fusion des 3 tests nominaux `deriver` en `deriverRetourneMonomeDerive`
- Ajout de Javadoc sur chaque méthode de test

**`TestDivisionEuclidienneResultat.java`** (3 tests → 3 tests, restructurés)
- Fusion des 2 premiers tests en `constructionStockeQuotientEtReste` : vérifie quotient ET reste en une seule construction
- Renommage de `constructionAvecResteNul` → `constructionAvecResteNulStockePolynomeNul`
- Ajout du cas symétrique `constructionAvecQuotientNulStockePolynomeNul`
- Simplification : suppression des vérifications de coefficients internes du quotient (hors périmètre de DER)
- **Aucun test d'exception** : le constructeur n'a aucune garde explicite (`null` accepté sans throw)

**`TestPolynomeIO.java`** (14 tests → 10 tests)
- Suppression de l'import mort `java.nio.file.Files`
- Fusion des 3 tests `serialiserParCoefficients` en `serialiserParCoefficientsCouvreLesCasTypes`
- Fusion des 2 tests `parserPolynome` depuis coefficients ; fusion des 2 tests depuis racines
- Renforcement du test `serialiserParRacines` : vérification du coefficient dominant et du nombre de racines
- Ajout de `sauvegarderTousEtChargerTousRacines` (chemin RACINES dans `sauvegarderTous` manquant)

**`TestPolynome.java`** (77 tests → 27 tests)
- Fusion systématique par méthode : 1–2 tests nominaux couvrant l'ensemble du contrat + 1 test d'exception séparé
- Construction coefficients : 2 tests (nominaux + IAE)
- Évaluation : 1 test (nul, constant, Horner dense, Horner creux)
- Addition : 2 tests (somme + IAE)
- Multiplication scalaire, polynôme : 2 tests chacun
- Dérivation, intégration : 1 test chacun
- Valeur moyenne : 2 tests (nominal + IAE)
- `getCoefficients` : 1 test (avec round-trip)
- Limites : 2 tests (±∞ séparés)
- Construction par racines : 3 tests (nominal + 2 groupes d'exceptions)
- Division euclidienne : 3 tests (contrat, relation euclidienne, IAE)
- Racines réelles : 2 tests (degrés 0-2 + degré 3)
- Multiplicités : 1 test

### Tests d'exception non ajoutés et raison

| Classe | Précondition absente | Raison |
|--------|---------------------|--------|
| `DivisionEuclidienneResultat` | `null` passé au constructeur | Aucune garde dans le code ; le constructeur stocke sans valider — créer un test d'exception serait tester un comportement inexistant |
| `PolynomeIO.parserPolynome(null)` | `null` non documenté | Aucun `if (ligne == null)` ; lève `NullPointerException` non spécifiée — pas de précondition explicite à tester |
