# CONCEPTION — Simulation Multi‑Agents (Boids)

Ce document présente la conception fonctionnelle et technique du sous‑projet multi‑agents: règles, dynamiques d’écosystème, interactions multi‑groupes, et lignes directrices d’extension.

## 1) Objectifs et périmètre

- Flocking réaliste (cohésion, alignement, séparation)
- Interactions proies ↔ prédateurs (fuite, poursuite, capture)
- Écosystème dynamique (énergie, âge, reproduction, mort)
- Architecture événementielle (pas de boucle bloquante, pas de threads requis)

Correspondance noms (docs historiques ↔ code actuel):
- AbstractBoidSystem → BoidSystem
- PreyBoidSystem → ProieBoidSystem
- PredatorBoidSystem → PredateurBoidSystem
- BoidSimulator → BoidSimulateur
- BoidUpdateEvent → BoidmàjEvent

Références de fichiers (principaux):

- `src/multi_agents/logic/BoidSystem.java`, `ProieBoidSystem.java`, `PredateurBoidSystem.java`, `Boid.java`, `LaLoi.java`
- `src/multi_agents/EvenT/EventManager.java`, `BoidmàjEvent.java`
- `src/multi_agents/simulation/BoidSimulateur.java`, `RotatedImageElement.java`
- Tests: `src/multi_agents/TestTest/TestBoids.java`, `TestMultiGroupBoids.java`, `TestEcosystem.java`


## 2) Modèle agent: Boid

État principal: position, vitesse, accélération, limites physiques (`Vmax`, `Fmax`).

États internes:

- Énergie (0–100)
- Âge (frames)
- Vivant (bool)

Méthodes clés:

- Limitation des forces: steering avec `Fmax` (mouvements fluides)
- Mise à jour avec rebonds (respect des bornes width/height)
- Métabolisme: `fatigue(mana)`, alimentation `gainenergie(mana)`

Contrat minimal (invariants):

- Vitesse bornée: `||v|| ≤ Vmax`
- Force appliquée bornée: `||a|| ≤ Fmax`
- Énergie dans [0, 100]; mort si `énergie ≤ 0` ⇒ le boid n’est plus dessiné

## 3) Règles locales (LaLoi)

- Cohésion: attraction vers le centre des voisins
- Alignement: rapprochement des vitesses
- Séparation: évitement des collisions
- Fuite (proie) / poursuite (prédateur) selon cible/menace
- Vagabondage: exploration légère si isolé
- Champ de vision (angle) pour perception réaliste

Combinaison (pondérée) typique dans `step()` d’un système:

1. Calculer pour chaque boid les contributions: `coh`, `ali`, `sep`, `fuite`/`poursuite`, `vagabond`
2. Accumuler: `a = w_coh*coh + w_ali*ali + w_sep*sep + ...`
3. Limiter `a` par `Fmax`, mettre à jour le boid (intègre `a` et limite `v` par `Vmax`)

## 4) Systèmes de boids

`BoidSystem` factorise: liste des boids, paramètres (rayonVision, distanceSep, `Vmax`, `Fmax`, angleVision, poids des règles), bornes `width/height`.

- `ProieBoidSystem` (proies)
  - Flocking + Fuite des prédateurs
  - Vagabondage si isolé
  - Métabolisme: `-0.1` énergie/frame
  - Reproduction: si énergie > 80, âge > 50, 2%/frame, coût −30 (nouveau boid proche)
  - Suppression des boids morts (énergie ≤ 0)

- `PredateurBoidSystem` (prédateurs)
  - Flocking + Chasse de la proie la plus proche
  - Métabolisme: `-0.5` énergie/frame
  - Capture si distance < 10 → `+40` énergie
  - Suppression des boids morts (famine)

Pseudocode d’un pas `step()` (schéma):

```text
pour chaque boid i:
  N := voisins visibles(i)
  coh, ali, sep := LaLoi.cohesion/alignement/separation(i, N)
  if proie:
    menaces := prédateurs dans FOV
    flee := LaLoi.fuite(i, menaces)
  if prédateur:
    cible := proie la plus proche dans FOV
    seek := LaLoi.poursuite(i, cible)
  a := w_coh*coh + w_ali*ali + w_sep*sep + (flee|seek) + vagabondage
  limiterForce(a, Fmax)
  i.màj(width, height)  // applique a, limite vitesse à Vmax, gère rebonds
  i.energie -= métabolisme
  if capture(proie, prédateur, dist<10): prédateur.energie += 40; retirer(proie)
retirer les boids morts
```

## 5) Interactions multi‑groupes

- Chaque système reçoit la liste des autres via `BoidSimulateur.linkSystems()` (évite les dépendances dures)
- Proies: détectent prédateurs dans un rayon élargi (p. ex. `rayonVision * 1.2`)
- Prédateurs: chassent si proie < `rayonVision * 2`
- Retrait des proies capturées après le calcul du pas

## 6) Événements et synchronisation

- `EventManager` planifie des `BoidmàjEvent(system, simulator, delay)`
- À chaque exécution d’événement:
  1) synchronise les bornes avec la taille du panel GUI (`setBounds(panelW, panelH)`),
  2) `system.step()`,
  3) `simulator.draw()`,
  4) re‑planifie l’événement (boucle date‑driven).

Avantage: fréquences d’update distinctes par système, simulation réactive aux tailles de fenêtre.

Remarque pratique:

- Appeler `linkSystems()` après avoir ajouté tous les systèmes (`addSystem(...)`), sinon les listes inter‑groupes seront incomplètes.

## 7) Paramétrage recommandé (exemples)

Proies (exploration réactive):

- `Vmax ≈ 3.0`, `Fmax ≈ 0.15–0.20`, angle de vision large
- Poids: cohésion 1.0, alignement 1.2, séparation 1.5

Prédateurs (chasse focalisée):

- `Vmax ≈ 4.0`, `Fmax ≈ 0.2`, angle de vision plus étroit
- Poids: cohésion 0.8, alignement 0.9, séparation 1.2, chasse 2.5

Écosystème:

- Métabolisme (proie/pred): 0.1 / 0.5
- Reproduction proie: seuils 80/50, chance 2%, coût 30
- Capture: distance 10, gain +40

Astuce de tuning:

- Si les proies disparaissent trop vite: réduire `Vmax` des prédateurs ou augmenter `Fmax`/FOV des proies.
- Si la simulation diverge: baisser `Fmax` (mouvements plus lisses) et augmenter la séparation.

## 8) Gestion des bordures et espace

- Rebonds amortis côté `Boid.màj()` + limitation par `Vmax`
- Bornes `width/height` mises à jour dynamiquement à chaque événement (taille panel GUI)

## 9) Extension: comment ajouter un type

1. Créer une sous‑classe de `BoidSystem`
2. Implémenter `step()` en combinant règles de `LaLoi`
3. Ajouter au simulateur via `addSystem(system, color, delay)`
4. Appeler `linkSystems()` après avoir ajouté tous les systèmes

Idées:

- Objets neutres (obstacles, attracteurs)
- Super‑prédateurs (chaîne alimentaire)
- Ressources végétales (nourriture des proies)
- Apprentissage (ajustement dynamique des poids)

Points d’intégration:

- Nouveaux champs dans `Boid` (ex: santé) ⇒ répercuter dans le rendu (couleur/alpha) si nécessaire.
- Règles additionnelles ⇒ implémenter dans `LaLoi` et pondérer dans `step()`.

## 10) Dépannage

- Rien ne bouge: vérifier les événements (Next/Restart), appels `addSystem()` puis `linkSystems()`
- Les boids sortent de l’écran: vérifier `setBounds()` appelé avant `step()`
- Pas d’interactions: `linkSystems()` doit être appelé après tous les `addSystem()`
- Couleurs ternes: c’est l’indicateur d’énergie < 30 (comportement voulu)
- Pas d’image ou artefacts visuels: `RotatedImageElement` utilise `doc/resources/glass.png` par défaut; en cas d’erreur, un cercle est dessiné à la place.

## 11) Références

- Reynolds, C. (1987) — Flocks, herds and schools
- Shiffman, D. — The Nature of Code, chap. 6
- Lotka–Volterra — dynamiques proies/prédateurs (modèle discret)

Voir aussi: `ARCHITECTURE.md` (diagrammes Mermaid + flux), `README.md` (commandes de lancement, Makefile).
