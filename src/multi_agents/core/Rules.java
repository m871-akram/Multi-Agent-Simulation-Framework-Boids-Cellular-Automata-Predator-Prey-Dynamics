package multi_agents.core;

import java.util.List;

/**
 * Classe utilitaire regroupant les règles de flocking de Reynolds.
 * C'est un peu comme une boîte à outils avec toutes les règles classiques :
 * cohésion, alignement, séparation, poursuite et fuite.
 * Toutes les méthodes sont statiques et renvoient des vecteurs normalisés.
 */
public class Rules {

    /**
     * Règle de cohésion : chaque boid essaie de se rapprocher du centre de masse de ses voisins.
     * En gros, il veut rester avec le groupe sans être tout seul.
     * Maintenant on vérifie aussi que le voisin est dans le champ de vision (angle).
     * @param b le boid qu'on considère
     * @param boids la liste de tous les boids du système
     * @param visionRadius le rayon dans lequel le boid peut "voir" ses voisins
     * @param fieldOfView l'angle de vision en radians (par exemple Math.PI * 0.75 pour 135°)
     * @return un vecteur normalisé pointant vers le centre de masse des voisins
     */
    public static Vecteur2D cohesion(Boid b, List<Boid> boids, double visionRadius, double fieldOfView) {
        Vecteur2D steering = new Vecteur2D(0, 0);
        int count = 0;

        for (Boid other : boids) {
            if (other != b) {
                double d = b.position.distance(other.position);
                if (d > 0 && d < visionRadius) {
                    // Vérification du champ de vision (angle)
                    Vecteur2D toOther = other.position.sub(b.position).normalize();
                    Vecteur2D velocity = b.vitesse.normalize();
                    double cosAngle = velocity.dot(toOther);
                    
                    // Si le voisin est dans le cône de vision
                    if (cosAngle > Math.cos(fieldOfView / 2)) {
                        steering = steering.add(other.position);
                        count++;
                    }
                }
            }
        }

        if (count > 0) {
            steering = steering.div(count); // centre de masse
            steering = steering.sub(b.position); // direction vers le centre
            if (steering.norm() > 0) {
                steering = steering.normalize();
            }
        }

        return steering;
    }

    /**
     * Règle d'alignement : chaque boid essaie d'adopter la vitesse moyenne de ses voisins.
     * Ça permet au groupe de se déplacer dans la même direction globale.
     * Maintenant on vérifie aussi que le voisin est dans le champ de vision (angle).
     * @param b le boid qu'on considère
     * @param boids la liste de tous les boids du système
     * @param visionRadius le rayon dans lequel le boid peut "voir" ses voisins
     * @param fieldOfView l'angle de vision en radians
     * @return un vecteur normalisé dans la direction de la vitesse moyenne des voisins
     */
    public static Vecteur2D alignment(Boid b, List<Boid> boids, double visionRadius, double fieldOfView) {
        Vecteur2D steering = new Vecteur2D(0, 0);
        int count = 0;

        for (Boid other : boids) {
            if (other != b) {
                double d = b.position.distance(other.position);
                if (d > 0 && d < visionRadius) {
                    // Vérification du champ de vision (angle)
                    Vecteur2D toOther = other.position.sub(b.position).normalize();
                    Vecteur2D velocity = b.vitesse.normalize();
                    double cosAngle = velocity.dot(toOther);
                    
                    // Si le voisin est dans le cône de vision
                    if (cosAngle > Math.cos(fieldOfView / 2)) {
                        steering = steering.add(other.vitesse);
                        count++;
                    }
                }
            }
        }

        if (count > 0) {
            steering = steering.div(count); // On calcule la vitesse moyenne
            if (steering.norm() > 0) {
                steering = steering.normalize();
            }
        }

        return steering;
    }

    /**
     * Règle de séparation : chaque boid veut garder un espace personnel avec ses voisins.
     * Il s'éloigne donc des boids trop proches pour éviter les collisions.
     * Pour la séparation, on vérifie aussi le champ de vision.
     * @param b le boid qu'on considère
     * @param boids la liste de tous les boids du système
     * @param separationDistance la distance minimale à respecter
     * @param fieldOfView l'angle de vision en radians
     * @return un vecteur normalisé qui s'éloigne des voisins trop proches
     */
    public static Vecteur2D separation(Boid b, List<Boid> boids, double separationDistance, double fieldOfView) {
        Vecteur2D steering = new Vecteur2D(0, 0);
        int count = 0;

        for (Boid other : boids) {
            if (other != b) {
                double d = b.position.distance(other.position);
                if (d > 0 && d < separationDistance) {
                    // Vérification du champ de vision
                    Vecteur2D toOther = other.position.sub(b.position).normalize();
                    Vecteur2D velocity = b.vitesse.normalize();
                    double cosAngle = velocity.dot(toOther);
                    
                    // Pour la séparation, on peut avoir un champ de vision plus large
                    if (cosAngle > Math.cos(fieldOfView / 2)) {
                        Vecteur2D diff = b.position.sub(other.position);
                        diff = diff.div(d); // pondération par la distance
                        steering = steering.add(diff);
                        count++;
                    }
                }
            }
        }

        if (count > 0) {
            steering = steering.div(count);
            if (steering.norm() > 0) {
                steering = steering.normalize();
            }
        }

        return steering;
    }

    /**
     * Règle de poursuite (seek) : le boid se dirige vers une cible donnée.
     * Version améliorée qui utilise maxSpeed pour calculer un steering réaliste.
     * Utile pour les prédateurs qui chassent des proies par exemple.
     * @param b le boid qu'on considère
     * @param target la position de la cible à atteindre
     * @param maxSpeed la vitesse maximale du boid
     * @return un vecteur de steering (non normalisé, à limiter par maxForce)
     */
    public static Vecteur2D seek(Boid b, Vecteur2D target, double maxSpeed) {
        Vecteur2D desired = target.sub(b.position);
        if (desired.norm() > 0) {
            desired = desired.normalize().mult(maxSpeed);
            return desired.sub(b.vitesse); // steering = desired - velocity
        }
        return new Vecteur2D(0, 0);
    }

    /**
     * Règle de fuite (flee) : le boid s'enfuit loin d'une menace.
     * Version améliorée qui utilise maxSpeed pour un steering réaliste.
     * C'est l'opposé de seek, utilisé par les proies pour échapper aux prédateurs.
     * @param b le boid qu'on considère
     * @param threat la position de la menace à éviter
     * @param maxSpeed la vitesse maximale du boid
     * @return un vecteur de steering (non normalisé, à limiter par maxForce)
     */
    public static Vecteur2D flee(Boid b, Vecteur2D threat, double maxSpeed) {
        Vecteur2D desired = b.position.sub(threat);
        if (desired.norm() > 0) {
            desired = desired.normalize().mult(maxSpeed);
            return desired.sub(b.vitesse); // steering = desired - velocity
        }
        return new Vecteur2D(0, 0);
    }

    /**
     * Comportement de vagabondage (wander) : le boid explore aléatoirement.
     * Utile quand il n'y a pas de voisins ou de cibles à suivre.
     * Le boid choisit une direction aléatoire pour bouger un peu.
     * @param b le boid qu'on considère
     * @param wanderStrength l'intensité du vagabondage (ex: 0.5)
     * @return un vecteur aléatoire pour l'exploration
     */
    public static Vecteur2D wander(Boid b, double wanderStrength) {
        double theta = Math.random() * 2 * Math.PI;
        Vecteur2D randomDir = new Vecteur2D(Math.cos(theta), Math.sin(theta));
        return randomDir.mult(wanderStrength);
    }

    /**
     * Trouve le boid le plus proche dans une liste.
     * Pratique pour les prédateurs qui cherchent leur proie la plus proche.
     * @param from le boid de référence
     * @param targets la liste des cibles potentielles
     * @return le boid le plus proche, ou null si la liste est vide
     */
    public static Boid findClosest(Boid from, List<Boid> targets) {
        Boid closest = null;
        double minDist = Double.MAX_VALUE;
        
        for (Boid target : targets) {
            double d = from.position.distance(target.position);
            if (d < minDist) {
                minDist = d;
                closest = target;
            }
        }
        
        return closest;
    }
}
