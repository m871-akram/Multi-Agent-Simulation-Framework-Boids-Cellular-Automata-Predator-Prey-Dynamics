package multi_agents.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Système de boids représentant des prédateurs (par exemple des requins ou des faucons).
 * En plus des règles de flocking, les prédateurs ont un comportement de chasse :
 * ils poursuivent activement les proies les plus proches (détectées via otherSystems)
 * et peuvent les capturer quand ils sont suffisamment proches.
 */
public class PredatorBoidSystem extends AbstractBoidSystem {
    private double chaseWeight;

    /**
     * Constructeur qui crée un système de prédateurs.
     * On a un paramètre en plus par rapport aux proies : le poids de la chasse.
     * @param nbBoids le nombre de prédateurs dans le système
     * @param width la largeur de la zone de simulation
     * @param height la hauteur de la zone de simulation
     * @param rayonVision le rayon dans lequel un prédateur voit ses voisins (et les proies)
     * @param distanceSep la distance minimale entre deux prédateurs
     * @param Vmax la vitesse maximale d'un prédateur
     * @param maxForce la force de steering maximale
     * @param fieldOfView l'angle de vision en radians
     * @param cohesionWeight l'importance de la cohésion entre prédateurs
     * @param alignmentWeight l'importance de l'alignement
     * @param separationWeight l'importance de la séparation
     * @param chaseWeight l'importance de la chasse des proies
     */
    public PredatorBoidSystem(int nbBoids, int width, int height,
                               double rayonVision, double distanceSep, double Vmax,
                               double maxForce, double fieldOfView,
                               double cohesionWeight, double alignmentWeight, double separationWeight,
                               double chaseWeight) {
        super(nbBoids, width, height, rayonVision, distanceSep, Vmax, maxForce, fieldOfView,
              cohesionWeight, alignmentWeight, separationWeight);
        this.chaseWeight = chaseWeight;
    }

    /**
     * Effectue une étape de simulation pour tous les prédateurs.
     * Ils appliquent les règles de flocking, chassent les proies détectées via otherSystems,
     * et peuvent capturer (retirer) les proies quand ils sont suffisamment proches.
     * L'énergie influence leur comportement : moins d'énergie = plus de motivation à chasser.
     */
    @Override
    public void step() {
        List<Vecteur2D> accelerations = new ArrayList<>();
        List<Boid> preysToCatch = new ArrayList<>(); // Liste des proies à capturer
        
        for (Boid predator : boids) {
            // Les trois règles de flocking entre prédateurs avec champ de vision
            Vecteur2D cohesion = Rules.cohesion(predator, boids, rayonVision, fieldOfView);
            Vecteur2D alignment = Rules.alignment(predator, boids, rayonVision, fieldOfView);
            Vecteur2D separation = Rules.separation(predator, boids, distanceSep, fieldOfView);

            // On applique les poids pour régler l'importance de chaque règle
            cohesion = cohesion.mult(cohesionWeight);
            alignment = alignment.mult(alignmentWeight);
            separation = separation.mult(separationWeight);

            // Comportement de chasse via otherSystems
            Vecteur2D pursue = new Vecteur2D(0, 0);
            Boid closestPrey = null;
            double minDist = Double.MAX_VALUE;
            
            // On parcourt tous les autres systèmes pour trouver des proies
            // On utilise isPrey() pour éviter instanceof (meilleure conception OO)
            for (AbstractBoidSystem sys : otherSystems) {
                if (sys.isPrey()) {
                    for (Boid prey : sys.getBoids()) {
                        double dist = predator.position.distance(prey.position);
                        if (dist < minDist) {
                            minDist = dist;
                            closestPrey = prey;
                        }
                    }
                }
            }
            
            // Si une proie est détectée dans le rayon de chasse
            if (closestPrey != null && minDist < rayonVision * 2) {
                // On poursuit la proie
                pursue = Rules.seek(predator, closestPrey.position, predator.maxSpeed);
                
                // La faim augmente la motivation à chasser
                double hungerFactor = 1.0 + (1.0 - predator.getEnergy());
                pursue = pursue.mult(chaseWeight * hungerFactor);
                
                // Capture : si le prédateur est très proche, il attrape la proie
                if (minDist < 10.0) {
                    preysToCatch.add(closestPrey);
                    predator.increaseEnergy(0.2); // Récupération d'énergie importante
                }
            }

            // On combine toutes les forces
            Vecteur2D acc = cohesion.add(alignment).add(separation).add(pursue);
            accelerations.add(acc);
        }
        
        // On retire les proies capturées de leurs systèmes
        for (Boid prey : preysToCatch) {
            for (AbstractBoidSystem sys : otherSystems) {
                if (sys.isPrey()) {
                    sys.getBoids().remove(prey);
                }
            }
        }
        
        // On met à jour tous les boids avec la méthode utilitaire
        updateBoids(accelerations);
    }
    
    /**
     * Indique que ce système représente des prédateurs.
     * @return true car c'est un système de prédateurs
     */
    @Override
    public boolean isPredator() {
        return true;
    }
    
    /**
     * Indique que ce système ne représente pas des proies.
     * @return false car c'est un système de prédateurs
     */
    @Override
    public boolean isPrey() {
        return false;
    }
}
