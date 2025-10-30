package multi_agents.logic;

import java.util.List;

/**
 * Classe regroupant les règles de flocking de Reynolds :
 * cohésion, alignement, séparation, poursuite et fuite
 */
public class LaLoi {

    /**
     * Règle de cohésion : chaque boid essaie de rester avec le groupe sans être tout seul
     * @param b le boid qu'on considère
     * @param boids la liste de tous les boids du système
     * @param visionRadius le rayon dans lequel le boid peut "voir" ses voisins
     * @param angleVision l'angle de vision
     * @return un vecteur normalisé pointant vers le centre de masse des voisins
     */
    public static Vecteur2D cohesion(Boid b, List<Boid> boids, double visionRadius, double angleVision) {
        Vecteur2D steering = new Vecteur2D(0, 0);
        int count = 0;
        for (Boid other : boids) {
            if (other != b) {
                double d = b.position.distance(other.position);
                if ( d < visionRadius) {
                    double cosAngle = b.vitesse.normalize().dot(other.position.sub(b.position).normalize());
                    // Si le voisin est dans le cône de vision
                    if (cosAngle > Math.cos(angleVision / 2)) {
                        steering = steering.add(other.position);
                        count++;
                    }
                }
            }
        }
        steering = steering.div(count); // centre de masse
        steering = steering.sub(b.position).normalize(); // direction vers le centre
        return steering;
    }

    /**
     * Règle d'alignement : Ça permet au groupe de se déplacer dans la même direction globale
     * @param b le boid qu'on considère
     * @param boids la liste de tous les boids du système
     * @param visionRadius le rayon dans lequel le boid peut "voir" ses voisins
     * @param angleVision l'angle de vision
     * @return un vecteur normalisé dans la direction de la vitesse moyenne des voisins
     */
    public static Vecteur2D alignment(Boid b, List<Boid> boids, double visionRadius, double angleVision) {
        Vecteur2D steering = new Vecteur2D(0, 0);
        int count = 0;
        for (Boid other : boids) {
            if (other != b) {
                double d = b.position.distance(other.position);
                if ( d < visionRadius) {
                    double cosAngle = b.vitesse.normalize().dot(other.position.sub(b.position).normalize());
                    // Si le voisin est dans le cône de vision
                    if (cosAngle > Math.cos(angleVision / 2)) {
                        steering = steering.add(other.vitesse);
                        count++;
                    }
                }
            }
        }
        steering = steering.div(count).normalize(); // On calcule la vitesse moyenne
        return steering;
    }

    /**
     * Règle de séparation : chaque boid s'éloigne des boids trop proches pour éviter les collisions
     * @param b le boid qu'on considère
     * @param boids la liste de tous les boids du système
     * @param separationDistance la distance minimale à respecter
     * @param angleVision l'angle de vision 
     * @return un vecteur normalisé qui s'éloigne des voisins trop proches
     */
    public static Vecteur2D separation(Boid b, List<Boid> boids, double separationDistance, double angleVision) {
        Vecteur2D steering = new Vecteur2D(0, 0);
        int count = 0;
        for (Boid other : boids) {
            if (other != b) {
                double d = b.position.distance(other.position);
                if ( d < separationDistance) {
                    double cosAngle = b.vitesse.normalize().dot(other.position.sub(b.position).normalize());
                    // Si le voisin est dans le cône de vision
                    if (cosAngle > Math.cos(angleVision / 2)) {
                        steering = steering.add(b.position.sub(other.position).div(d));
                        count++;
                    }
                }
            }
        }
        steering = steering.div(count).normalize();
        return steering;
    }

    /**
     * Règle de poursuite : le boid se dirige vers une cible donnée
     * @param b le boid qu'on considère
     * @param proie la position de la cible à atteindre
     * @param Vmax la vitesse maximale du boid
     * @return un vecteur de steering (non normalisé, à limiter par Fmax)
     */
    public static Vecteur2D poursuite(Boid b, Vecteur2D proie, double Vmax) {
        Vecteur2D leViseur = proie.sub(b.position);
        if (leViseur.norm() > 0) {
            return leViseur.normalize().mult(Vmax).sub(b.vitesse); 
        }
        return new Vecteur2D(0, 0);
    }

    /**
     * Règle de fuite : le boid s'enfuit loin d'une menace
     * @param b le boid qu'on considère
     * @param menace la position de la menace à éviter
     * @param Vmax la vitesse maximale du boid
     * @return un vecteur de steering (non normalisé, à limiter par Fmax)
     */
    public static Vecteur2D fuite(Boid b, Vecteur2D menace, double Vmax) {
        Vecteur2D desired = b.position.sub(menace);
        if (desired.norm() > 0) {
            return desired.normalize().mult(Vmax).sub(b.vitesse); // steering = desired - velocity
        }
        return new Vecteur2D(0, 0);
    }

    /**
     * Comportement de vagabondage : le boid explore aléatoirement
     * @param b le boid qu'on considère
     * @param VagabondStrength l'intensité du vagabondage 
     * @return un vecteur aléatoire pour l'exploration
     */
    public static Vecteur2D Vagabond(Boid b, double VagabondStrength) {
        double theta = Math.random() * 2 * Math.PI;
        Vecteur2D randomDir = new Vecteur2D(Math.cos(theta), Math.sin(theta));
        return randomDir.mult(VagabondStrength);
    }
    
}
