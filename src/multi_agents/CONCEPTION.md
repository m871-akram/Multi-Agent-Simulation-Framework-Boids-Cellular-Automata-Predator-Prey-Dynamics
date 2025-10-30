# Document de Conception - Simulation Multi-Agents Boids

**Projet :** Système de simulation de boids avec interactions prédateur-proie  
**Contexte :** 2A POO - Programmation Orientée Objet Avancée  
**Date :** Octobre 2025

---

## 1. Choix de Conception & Architecture

### 1.1 Architecture Globale

Le projet adopte une **architecture en couches** respectant les principes SOLID :

```
┌─────────────────────────────────────────┐
│         sim/ (Présentation)             │  ← Interface utilisateur & rendu
├─────────────────────────────────────────┤
│      events/ (Contrôleur)               │  ← Gestion événementielle
├─────────────────────────────────────────┤
│         core/ (Modèle)                  │  ← Logique métier & agents
└─────────────────────────────────────────┘
```

**Justification :** Séparation claire des responsabilités (SRP), facilite la maintenance et l'évolutivité.

### 1.2 Hiérarchie des Systèmes de Boids

#### Choix : Classe Abstraite vs Interface

**Décision :** Utilisation d'une classe abstraite `BoidSystem`

**Justification :**
- **Factorisation importante** : attributs communs (width, height, boids, rayonVision, etc.)
- **Comportement partagé** : méthode `màjBoids()` identique pour tous
- **Template Method Pattern** : `step()` abstraite, mais structure commune

```java
public abstract class BoidSystem {
    // Attributs factorisés (évite duplication)
    protected List<Boid> boids;
    protected double rayonVision, distanceSep, Vmax;
    
    // Méthodes abstraites (polymorphisme)
    public abstract void step();
    public abstract boolean ispredateur();
    public abstract boolean isproie();
    
    // Méthode utilitaire réutilisable (délégation)
    protected void màjBoids(List<Vecteur2D> accelerations) { ... }
}
```

**Avantages :**
- Évite 200+ lignes de code dupliqué
- Un seul endroit pour modifier `màjBoids()`
- Respect du principe DRY (Don't Repeat Yourself)

### 1.3 Élimination d'instanceof

#### Problème Initial
```java
// ❌ Mauvaise pratique
if (sys instanceof PredateurBoidSystem) {
    // traitement prédateur
}
```

#### Solution Polymorphique
```java
// ✅ Bonne conception OO
public abstract boolean ispredateur();
public abstract boolean isproie();

// Utilisation
if (sys.ispredateur()) {
    // traitement prédateur
}
```

**Justification :**
- **Open/Closed Principle** : ouvert à l'extension (nouveaux types), fermé à la modification
- **Polymorphisme** : chaque classe définit son identité
- **Maintenabilité** : ajout d'un nouveau type de boid ne casse pas le code existant

### 1.4 Encapsulation & Intégrité des Données

#### Choix de Visibilité

| Attribut | Visibilité | Justification |
|----------|-----------|---------------|
| `energie`, `peur` | `private` | États sensibles, accès contrôlé |
| `position`, `vitesse` | package-protected | Performance (accès dans `LaLoi`), masqué de l'extérieur |
| `Vmax`, `Fmax` | package-protected | Même raison |

**Validation dans les Setters :**
```java
public void setVmax(double Vmax) {
    if (Vmax < 0) {
        throw new IllegalArgumentException("Vitesse maximale négative");
    }
    this.Vmax = Vmax;
}
```

**Avantages :**
- **Intégrité garantie** : impossible de mettre des valeurs invalides
- **Compromis performance/encapsulation** : accès direct dans le package, protégé ailleurs
- **Robustesse** : exceptions explicites

### 1.5 Architecture Événementielle

#### Choix : Date-Driven Simulation

**Décision :** File de priorité d'événements triés par date

```java
public class EventManager {
    private PriorityQueue<Event> events;  // Triée par date
    private long currentDate;
}
```

**Justification :**
- **Fréquences indépendantes** : proies (delay=1), prédateurs (delay=3)
- **Scalabilité** : O(log n) pour insertion/extraction
- **Précision temporelle** : pas de calculs inutiles

**Alternative rejetée :** Boucle fixe avec compteurs  
**Raison :** Moins flexible, calculs à chaque frame même si pas nécessaire

### 1.6 Interactions Multi-Groupes

#### Choix : Liste d'Autres Systèmes

**Décision :**
```java
protected List<BoidSystem> interGroups;
```

**Justification :**
- **Extensibilité** : n systèmes peuvent interagir (pas limité à 2)
- **Couplage faible** : pas de référence directe predateurSystem ↔ proieSystem
- **Modularité** : on peut lier/délier dynamiquement

**Utilisation :**
```java
simulator.linkSystems();  // Liaison automatique après ajout
```

---

## 2. Classes et Méthodes Clés

### 2.1 Classe `Boid`

**Responsabilité :** Représenter un agent autonome individuel

**Méthodes principales :**

| Méthode | Rôle | Justification du Design |
|---------|------|------------------------|
| `limiterForce(Vecteur2D)` | Accumule forces | Steering behaviors (Nature of Code) |
| `màj(width, height)` | Intègre physique | Euler semi-implicite (vitesse puis position) |
| `increaseenergie(double)` | Gère énergie | États émergents, borne [0,1] |

**Choix technique :** Attribut `acceleration` réinitialisé à chaque frame  
**Raison :** Modèle de forces cumulatives (cohésion + alignement + séparation + ...)

### 2.2 Classe `LaLoi`

**Responsabilité :** Algorithmes de flocking

**Choix :** Classe utilitaire avec méthodes statiques

**Justification :**
- Pas d'état interne (stateless)
- Réutilisable partout
- Lisibilité : `LaLoi.cohesion(...)` vs `new LaLoi().cohesion(...)`

**Méthodes avancées :**

1. **Champ de vision (FOV)**
```java
Vecteur2D toOther = other.position.sub(b.position).normalize();
double cosAngle = velocity.dot(toOther);
if (cosAngle > Math.cos(angleVision / 2)) { ... }
```
**Justification :** Réalisme biologique, les boids ne voient pas derrière eux

2. **poursuite/fuite avec anticipation**
```java
Vecteur2D desired = target.sub(b.position).normalize().mult(Vmax);
return desired.sub(b.vitesse);  // steering force
```
**Justification :** Approche Reynolds, mouvement fluide sans téléportation

### 2.3 Système Événementiel

**`BoidmàjEvent` auto-replanifiant :**
```java
public void execute() {
    system.màjDimensions(gui.getPanelWidth(), gui.getPanelHeight());
    system.step();
    simulator.draw();
    manager.addEvent(new BoidmàjEvent(date + delay, ...)); // ← Auto-replanification
}
```

**Avantages :**
- Boucle infinie sans `while(true)`
- Facile d'arrêter/redémarrer (vider la file)
- Chaque système contrôle sa propre fréquence

---

## 3. Tests Effectués & Résultats

### 3.1 Test Unitaire : EventManager

**Fichier :** `TestEventManager.java`

**Objectif :** Vérifier le tri chronologique et l'exécution séquentielle

**Scénario :**
```java
manager.addEvent(new MessageEvent(3, "Third"));
manager.addEvent(new MessageEvent(1, "First"));
manager.addEvent(new MessageEvent(2, "Second"));
manager.next(); // Doit afficher "First"
manager.next(); // Doit afficher "Second"
```

**Résultat :** ✅ **SUCCÈS** - Ordre correct maintenu, validation de la PriorityQueue

### 3.2 Test d'Intégration : Flocking Simple

**Fichier :** `TestBoids.java`

**Configuration :**
- 50 proies
- Rayon de vision : 100
- Champ de vision : 144° (Math.PI * 0.8)

**Comportements observés :**
1. **Formation de groupe** : Les boids isolés convergent vers le centre de masse (cohésion)
2. **Alignement** : Vitesses se synchronisent, le groupe se déplace ensemble
3. **Évitement** : Pas de collisions grâce à la séparation
4. **Rebondissement** : Rebond sur les bords avec amortissement (0.9)

**Résultat :** ✅ **SUCCÈS** - Flocking classique reproduit fidèlement

### 3.3 Test Système : Prédateurs vs Proies

**Fichier :** `TestMultiGroupBoids.java`

**Configuration :**
- 60 proies (cyan, delay=1)
- 8 prédateurs (rouge, delay=3)
- Capture à distance < 10 pixels

**Scénarios testés :**

| Scénario | Comportement Attendu | Résultat Observé |
|----------|---------------------|------------------|
| Prédateur loin | Proies font flocking normal | ✅ Groupes cohésifs |
| Prédateur proche (<120) | Proies fuient, peur augmente | ✅ Dispersion + fuite |
| Capture | Proie disparaît, prédateur gagne énergie | ✅ Liste modifiée, énergie +0.2 |
| Prédateur affamé | Chasse plus agressive (faim × 2) | ✅ Accélération visible |
| 5 minutes | Population proies diminue | ✅ ~35 proies restantes |

**Résultat :** ✅ **SUCCÈS** - Dynamique prédateur-proie émergente et stable

### 3.4 Test de Robustesse : Valeurs Limites

**Test 1 : Vitesse négative**
```java
boid.setVmax(-1.0);  // Lance IllegalArgumentException
```
**Résultat :** ✅ Exception levée avec message clair

**Test 2 : Événement dans le passé**
```java
manager.addEvent(new Event(currentDate - 10));
```
**Résultat :** ✅ Exception avec détails de dates

**Test 3 : Division par zéro**
```java
if (steering.norm() > 0) steering = steering.normalize();
```
**Résultat :** ✅ Protections en place, pas de NaN

### 3.5 Test de Performance

**Configuration :** 200 boids (150 proies + 50 prédateurs)

**Mesures :**
- FPS moyen : ~45 FPS
- Temps par frame : ~22ms
- Complexité observée : O(n²) pour calculs de voisinage

**Optimisations possibles (hors scope) :**
- Quadtree pour voisinage : O(n log n)
- Multithreading par système

---

## 4. Conclusion

Le projet implémente une architecture robuste et extensible respectant tous les principes POO :

**Points forts :**
- ✅ Zéro `instanceof` (polymorphisme pur)
- ✅ Encapsulation stricte avec validation
- ✅ Héritage bien utilisé (factorisation)
- ✅ Exceptions pour robustesse
- ✅ Documentation Javadoc complète

**Résultats :**
- Simulations stables sur longue durée
- Comportements émergents réalistes
- Code maintenable et testable
