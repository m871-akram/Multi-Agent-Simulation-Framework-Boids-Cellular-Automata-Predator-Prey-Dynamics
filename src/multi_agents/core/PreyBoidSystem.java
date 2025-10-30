package multi_agents.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Système de boids représentant des proies (par exemple des poissons ou des oiseaux).
 * Les proies suivent les trois règles classiques de Reynolds, mais maintenant elles peuvent
 * aussi fuir les prédateurs (détectés via otherSystems) et vagabonder quand elles sont seules.
 */
public class PreyBoidSystem extends AbstractBoidSystem {
    private double fleeWeight;
    private double wanderWeight;

    /**
     * Constructeur qui crée un système de proies avec tous les paramètres nécessaires.
     * @param nbBoids le nombre de proies dans le système
     * @param width la largeur de la zone de simulation
     * @param height la hauteur de la zone de simulation
     * @param rayonVision le rayon dans lequel une proie voit ses voisines
     * @param distanceSep la distance minimale entre deux proies
     * @param Vmax la vitesse maximale d'une proie
     * @param maxForce la force de steering maximale
     * @param fieldOfView l'angle de vision en radians
     * @param cohesionWeight l'importance de la cohésion
     * @param alignmentWeight l'importance de l'alignement
     * @param separationWeight l'importance de la séparation
     */
    public PreyBoidSystem(int nbBoids, int width, int height,
                          double rayonVision, double distanceSep, double Vmax,
                          double maxForce, double fieldOfView,
                          double cohesionWeight, double alignmentWeight, double separationWeight) {
        super(nbBoids, width, height, rayonVision, distanceSep, Vmax, maxForce, fieldOfView,
              cohesionWeight, alignmentWeight, separationWeight);
        this.fleeWeight = 3.0;    // La fuite est très importante pour survivre
        this.wanderWeight = 0.3;  // Le vagabondage est plus faible
    }

    /**
     * Effectue une étape de simulation pour toutes les proies.
     * On applique les règles de flocking avec champ de vision, la fuite des prédateurs
     * (maintenant détectés via otherSystems), et le vagabondage si la proie est seule.
     */
    @Override
    public void step() {
        List<Vecteur2D> accelerations = new ArrayList<>();
        
        for (Boid boid : boids) {
            // On calcule les trois forces de flocking avec champ de vision
            Vecteur2D cohesion = Rules.cohesion(boid, boids, rayonVision, fieldOfView);
            Vecteur2D alignment = Rules.alignment(boid, boids, rayonVision, fieldOfView);
            Vecteur2D separation = Rules.separation(boid, boids, distanceSep, fieldOfView);

            // On multiplie chaque force par son poids
            cohesion = cohesion.mult(cohesionWeight);
            alignment = alignment.mult(alignmentWeight);
            separation = separation.mult(separationWeight);

            // Comportement de fuite des prédateurs (version otherSystems)
            Vecteur2D flee = new Vecteur2D(0, 0);
            
            // On parcourt tous les autres systèmes pour détecter les prédateurs
            // On utilise isPredator() pour éviter instanceof (meilleure conception OO)
            for (AbstractBoidSystem sys : otherSystems) {
                if (sys.isPredator()) {
                    for (Boid predator : sys.getBoids()) {
                        double dist = boid.position.distance(predator.position);
                        // Si un prédateur est dans le rayon de détection
                        if (dist < rayonVision * 1.2) {
                            Vecteur2D fleeForce = Rules.flee(boid, predator.position, boid.maxSpeed);
                            flee = flee.add(fleeForce);
                            boid.increaseFear(0.1); // La peur augmente
                        }
                    }
                }
            }
            
            // La peur amplifie la force de fuite
            if (flee.norm() > 0) {
                flee = flee.mult(fleeWeight * (1.0 + boid.getFear()));
            }

            // Comportement de vagabondage si pas de voisins (exploration)
            Vecteur2D wander = new Vecteur2D(0, 0);
            if (cohesion.norm() < 0.01) { // Si pas de cohésion, le boid est seul
                wander = Rules.wander(boid, wanderWeight);
            }

            // On combine toutes les forces
            Vecteur2D acc = cohesion.add(alignment).add(separation).add(flee).add(wander);
            accelerations.add(acc);
        }
        
        // On met à jour tous les boids avec la méthode utilitaire
        updateBoids(accelerations);
    }
    
    /**
     * Indique que ce système représente des proies.
     * @return true car c'est un système de proies
     */
    @Override
    public boolean isPrey() {
        return true;
    }
    
    /**
     * Indique que ce système ne représente pas des prédateurs.
     * @return false car c'est un système de proies
     */
    @Override
    public boolean isPredator() {
        return false;
    }
}
