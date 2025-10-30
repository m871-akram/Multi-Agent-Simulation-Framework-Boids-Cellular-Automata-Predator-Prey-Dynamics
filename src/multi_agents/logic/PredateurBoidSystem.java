package multi_agents.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Système de boids représentant des prédateurs (par exemple des requins ou des faucons).
 * En plus des règles de flocking, les prédateurs ont un comportement de chasse :
 * ils poursuivent activement les proies les plus proches (détectées via interGroups)
 * et peuvent les capturer quand ils sont suffisamment proches.
 */
public class PredateurBoidSystem extends BoidSystem {

    /**
     * Constructeur qui crée un système de prédateurs.
     * On a un paramètre en plus par rapport aux proies : le poids de la chasse.
     * @param nbBoids le nombre de prédateurs dans le système
     * @param width la largeur de la zone de simulation
     * @param height la hauteur de la zone de simulation
     * @param rayonVision le rayon dans lequel un prédateur voit ses voisins (et les proies)
     * @param distanceSep la distance minimale entre deux prédateurs
     * @param Vmax la vitesse maximale d'un prédateur
     * @param Fmax la force de steering maximale
     * @param angleVision l'angle de vision en radians
     * @param poiDECohesion l'importance de la cohésion entre prédateurs
     * @param poiDEAlignement l'importance de l'alignement
     * @param poiDESeparation l'importance de la séparation
     */
    public PredateurBoidSystem(int nbBoids, int width, int height,
                               double rayonVision, double distanceSep, double Vmax,
                               double Fmax, double angleVision,
                               double poiDECohesion, double poiDEAlignement, double poiDESeparation) {
        super(nbBoids, width, height, rayonVision, distanceSep, Vmax, Fmax, angleVision,
              poiDECohesion, poiDEAlignement, poiDESeparation);

    }

    /**
     * Effectue une étape de simulation pour tous les prédateurs.
     * Ils appliquent les règles de flocking, chassent les proies détectées via interGroups,
     * et peuvent capturer (retirer) les proies quand ils sont suffisamment proches.
     * 
     * Les prédateurs perdent de l'énergie plus rapidement que les proies (métabolisme actif).
     * Manger une proie restaure leur énergie. Les prédateurs affamés meurent.
     */
    @Override
    public void step() {
        List<Vecteur2D> accelerations = new ArrayList<>();
        List<Boid> proiesToCatch = new ArrayList<>(); // Liste des proies à capturer
        
        for (Boid predateur : boids) {
            // Métabolisme : les prédateurs perdent de l'énergie (plus vite que les proies)
            predateur.fatigue(0.5);
            
            // Les trois règles de flocking entre prédateurs avec champ de vision
            Vecteur2D cohesion = LaLoi.cohesion(predateur, boids, rayonVision, angleVision);
            Vecteur2D alignment = LaLoi.alignment(predateur, boids, rayonVision, angleVision);
            Vecteur2D separation = LaLoi.separation(predateur, boids, distanceSep, angleVision);

            // On applique les poids pour régler l'importance de chaque règle
            cohesion = cohesion.mult(poiDECohesion);
            alignment = alignment.mult(poiDEAlignement);
            separation = separation.mult(poiDESeparation);

            // Comportement de chasse via interGroups
            Vecteur2D pursue = new Vecteur2D(0, 0);
            Boid closestproie = null;
            double minDist = Double.MAX_VALUE;
            
            // On parcourt tous les autres systèmes pour trouver des proies
            // On utilise isproie() pour éviter instanceof (meilleure conception OO)
            for (BoidSystem sys : interGroups) {
                if (sys.isproie()) {
                    for (Boid proie : sys.getBoids()) {
                        double dist = predateur.position.distance(proie.position);
                        if (dist < minDist) {
                            minDist = dist;
                            closestproie = proie;
                        }
                    }
                }
            }
            
            // Si une proie est détectée dans le rayon de chasse
            if (closestproie != null && minDist < rayonVision * 2) {
                // On poursuit la proie
                pursue = LaLoi.poursuite(predateur, closestproie.position, predateur.Vmax);
                
                // La faim augmente la motivation à chasser (énergie sur échelle 0-100)
                double hungerFactor = 1.0 + (100.0 - predateur.getenergie()) / 100.0;
                pursue = pursue.mult(hungerFactor);
                
                // Capture : si le prédateur est très proche, il attrape la proie
                if (minDist < 10.0) {
                    proiesToCatch.add(closestproie);
                    predateur.gainenergie(40.0); // Récupération d'énergie importante
                }
            }

            // On combine toutes les forces
            Vecteur2D acc = cohesion.add(alignment).add(separation).add(pursue);
            accelerations.add(acc);
        }
        
        // On retire les proies capturées de leurs systèmes
        for (Boid proie : proiesToCatch) {
            for (BoidSystem sys : interGroups) {
                if (sys.isproie()) {
                    sys.getBoids().remove(proie);
                }
            }
        }
        
        // On met à jour tous les boids avec la méthode utilitaire
        màjBoids(accelerations);
        
        // Retirer les prédateurs morts (énergie épuisée)
        boids.removeIf(predateur -> !predateur.estvivant());
    }
    
    /**
     * Indique que ce système représente des prédateurs.
     * @return true car c'est un système de prédateurs
     */
    @Override
    public boolean ispredateur() {
        return true;
    }
    
    /**
     * Indique que ce système ne représente pas des proies.
     * @return false car c'est un système de prédateurs
     */
    @Override
    public boolean isproie() {
        return false;
    }
}
