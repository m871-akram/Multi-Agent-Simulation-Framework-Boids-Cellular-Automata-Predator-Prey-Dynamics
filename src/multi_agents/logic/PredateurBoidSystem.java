package multi_agents.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Système de boids représentant des prédateurs
 * En plus des règles de flocking, les prédateurs ont un comportement de chasse :
 * ils poursuivent activement les proies les plus proches (détectées via interGroups)
 * et peuvent les capturer quand ils sont suffisamment proches.
 */
public class PredateurBoidSystem extends BoidSystem {

    /**
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
     * Effectue une étape de simulation pour tous les prédateurs
     * Les prédateurs perdent de l'énergie plus rapidement que les proies (métabolisme actif)
     * Manger une proie restaure leur énergie. Les prédateurs affamés meurent.
     */
    @Override
    public void step() {
        List<Vecteur2D> accelerations = new ArrayList<>();
        List<Boid> proieAattraper = new ArrayList<>();
        
        for (Boid predateur : boids) {
            // Métabolisme : les prédateurs perdent de l'énergie (plus vite que les proies)
            predateur.fatigue(0.5);
            
            // Les trois règles de flocking
            Vecteur2D cohesion = LaLoi.cohesion(predateur, boids, rayonVision, angleVision);
            Vecteur2D alignment = LaLoi.alignment(predateur, boids, rayonVision, angleVision);
            Vecteur2D separation = LaLoi.separation(predateur, boids, distanceSep, angleVision);

            cohesion = cohesion.mult(poiDECohesion);
            alignment = alignment.mult(poiDEAlignement);
            separation = separation.mult(poiDESeparation);

            // Comportement de chasse via interGroups
            Vecteur2D pursuit = new Vecteur2D(0, 0);
            Boid proieProche = null;
            double minDist = Double.MAX_VALUE;
            
            // On parcourt tous les autres systèmes pour trouver des proies
            for (BoidSystem sys : interGroups) {
                if (sys.estproie()) {
                    for (Boid proie : sys.getBoids()) {
                        double dist = predateur.position.distance(proie.position);
                        if (dist < minDist) {
                            minDist = dist;
                            proieProche = proie;
                        }
                    }
                }
            }
            // Si une proie est détectée dans le rayon de chasse qui est deux fois plus grand que le rayon de vision d une proie
            // ben c est normal , c est un PREDATEUR
            if (proieProche != null && minDist < rayonVision * 2) {
                // On poursuit la proie
                pursuit = LaLoi.poursuite(predateur, proieProche.position, predateur.Vmax);
                // Capture : si le prédateur est très proche, il attrape la proie
                if (minDist < 10.0) {
                    proieAattraper.add(proieProche);
                    predateur.gainenergie(40.0); // Récupération d'énergie
                }
            }
            // On combine toutes les forces
            Vecteur2D acc = cohesion.add(alignment).add(separation).add(pursuit);
            accelerations.add(acc);
        }
        // On retire les proies capturées de leurs systèmes
        for (Boid proie : proieAattraper) {
            for (BoidSystem sys : interGroups) {
                if (sys.estproie()) {
                    sys.getBoids().remove(proie);
                }
            }
        }
        
        // On met à jour tous les boids
        màjBoids(accelerations);
        // Retirer les prédateurs morts (énergie épuisée)
        boids.removeIf(predateur -> !predateur.estvivant());
    }
    
    /**
     * @return true car c'est un système de prédateurs
     */
    @Override
    public boolean estpredateur() {
        return true;
    }
    
    /**
     * @return false car c'est un système de prédateurs
     */
    @Override
    public boolean estproie() {
        return false;
    }
}
