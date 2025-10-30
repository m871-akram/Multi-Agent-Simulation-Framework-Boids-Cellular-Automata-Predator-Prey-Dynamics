package multi_agents.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Système de boids représentant des proies (par exemple des poissons ou des oiseaux).
 * Les proies suivent les trois règles classiques de Reynolds, mais maintenant elles peuvent
 * aussi fuir les prédateurs (détectés via interGroups) et vagabonder quand elles sont seules.
 */
public class ProieBoidSystem extends BoidSystem {
    private double fuiteWeight;
    private double VagabondWeight;

    /**
     * Constructeur qui crée un système de proies avec tous les paramètres nécessaires.
     * @param nbBoids le nombre de proies dans le système
     * @param width la largeur de la zone de simulation
     * @param height la hauteur de la zone de simulation
     * @param rayonVision le rayon dans lequel une proie voit ses voisines
     * @param distanceSep la distance minimale entre deux proies
     * @param Vmax la vitesse maximale d'une proie
     * @param Fmax la force de steering maximale
     * @param angleVision l'angle de vision en radians
     * @param poiDECohesion l'importance de la cohésion
     * @param poiDEAlignement l'importance de l'alignement
     * @param poiDESeparation l'importance de la séparation
     */
    public ProieBoidSystem(int nbBoids, int width, int height,
                          double rayonVision, double distanceSep, double Vmax,
                          double Fmax, double angleVision,
                          double poiDECohesion, double poiDEAlignement, double poiDESeparation) {
        super(nbBoids, width, height, rayonVision, distanceSep, Vmax, Fmax, angleVision,
              poiDECohesion, poiDEAlignement, poiDESeparation);
        this.fuiteWeight = 3.0;    // La fuite est très importante pour survivre
        this.VagabondWeight = 0.3;  // Le vagabondage est plus faible
    }

    /**
     * Effectue une étape de simulation pour toutes les proies.
     * On applique les règles de flocking avec champ de vision, la fuite des prédateurs
     * (maintenant détectés via interGroups), et le vagabondage si la proie est seule.
     * 
     * Les proies perdent de l'énergie au fil du temps (métabolisme).
     * Si une proie survit assez longtemps et a assez d'énergie, elle se reproduit.
     * Les proies mortes (énergie épuisée) sont retirées du système.
     */
    @Override
    public void step() {
        List<Vecteur2D> accelerations = new ArrayList<>();
        List<Boid> newBoids = new ArrayList<>();
        
        for (Boid boid : boids) {
            // Métabolisme : toutes les proies perdent de l'énergie
            boid.fatigue(0.1);
            
            // Reproduction : si la proie a assez d'énergie et d'âge
            // et que la chance le permet (2% par frame)
            if (boid.getenergie() > 80 && boid.getAge() > 50 && Math.random() < 0.02) {
                // Créer un nouveau boid à proximité
                double offsetX = (Math.random() - 0.5) * 20;
                double offsetY = (Math.random() - 0.5) * 20;
                Boid baby = new Boid(
                    boid.position.x + offsetX, 
                    boid.position.y + offsetY
                );
                // Hériter des paramètres du parent
                baby.Vmax = boid.Vmax;
                baby.Fmax = boid.Fmax;
                newBoids.add(baby);
                
                // Le parent perd de l'énergie pour la reproduction
                boid.fatigue(30.0);
            }
            
            // On calcule les trois forces de flocking avec champ de vision
            Vecteur2D cohesion = LaLoi.cohesion(boid, boids, rayonVision, angleVision);
            Vecteur2D alignment = LaLoi.alignment(boid, boids, rayonVision, angleVision);
            Vecteur2D separation = LaLoi.separation(boid, boids, distanceSep, angleVision);

            // On multiplie chaque force par son poids
            cohesion = cohesion.mult(poiDECohesion);
            alignment = alignment.mult(poiDEAlignement);
            separation = separation.mult(poiDESeparation);

            // Comportement de fuite des prédateurs (version interGroups)
            Vecteur2D fuite = new Vecteur2D(0, 0);
            
            // On parcourt tous les autres systèmes pour détecter les prédateurs
            // On utilise ispredateur() pour éviter instanceof (meilleure conception OO)
            for (BoidSystem sys : interGroups) {
                if (sys.ispredateur()) {
                    for (Boid predateur : sys.getBoids()) {
                        double dist = boid.position.distance(predateur.position);
                        // Si un prédateur est dans le rayon de détection
                        if (dist < rayonVision * 1.2) {
                            Vecteur2D fuiteForce = LaLoi.fuite(boid, predateur.position, boid.Vmax);
                            fuite = fuite.add(fuiteForce);
                            boid.auGpeur(0.1); // La peur augmente
                        }
                    }
                }
            }
            
            // La peur amplifie la force de fuite
            if (fuite.norm() > 0) {
                fuite = fuite.mult(fuiteWeight * (1.0 + boid.getpeur()));
            }

            // Comportement de vagabondage si pas de voisins (exploration)
            Vecteur2D Vagabond = new Vecteur2D(0, 0);
            if (cohesion.norm() < 0.01) { // Si pas de cohésion, le boid est seul
                Vagabond = LaLoi.Vagabond(boid, VagabondWeight);
            }

            // On combine toutes les forces
            Vecteur2D acc = cohesion.add(alignment).add(separation).add(fuite).add(Vagabond);
            accelerations.add(acc);
        }
        
        // On met à jour tous les boids avec la méthode utilitaire
        màjBoids(accelerations);
        
        // Ajouter les nouveaux boids nés
        boids.addAll(newBoids);
        
        // Retirer les boids morts (énergie épuisée)
        boids.removeIf(boid -> !boid.estvivant());
    }
    
    /**
     * Indique que ce système représente des proies.
     * @return true car c'est un système de proies
     */
    @Override
    public boolean isproie() {
        return true;
    }
    
    /**
     * Indique que ce système ne représente pas des prédateurs.
     * @return false car c'est un système de proies
     */
    @Override
    public boolean ispredateur() {
        return false;
    }
}
