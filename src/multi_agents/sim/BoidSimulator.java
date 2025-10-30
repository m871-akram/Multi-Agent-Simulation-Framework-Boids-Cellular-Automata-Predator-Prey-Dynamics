package multi_agents.sim;

import gui.GUISimulator;
import multi_agents.core.AbstractBoidSystem;
import multi_agents.core.Boid;
import multi_agents.core.Vecteur2D;
import multi_agents.events.BoidUpdateEvent;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simulateur spécialisé pour les boids, capable de gérer plusieurs systèmes en même temps.
 * Par exemple, on peut avoir un système de proies et un système de prédateurs qui évoluent
 * chacun à leur propre rythme avec leur propre couleur d'affichage.
 */
public class BoidSimulator extends AbstractSimulator {
    private Map<AbstractBoidSystem, Color> systems;

    /**
     * Constructeur qui crée un simulateur de boids.
     * @param gui l'interface graphique où on va dessiner les boids
     */
    public BoidSimulator(GUISimulator gui) {
        super(gui);
        // LinkedHashMap pour garder l'ordre d'ajout des systèmes
        this.systems = new LinkedHashMap<>();
    }

    /**
     * Ajoute un nouveau système de boids à la simulation.
     * On peut appeler cette méthode plusieurs fois pour avoir plusieurs groupes.
     * @param system le système de boids (proies, prédateurs, etc.)
     * @param color la couleur pour afficher ce groupe
     * @param delay la fréquence de mise à jour (1 = rapide, 3 = plus lent, etc.)
     */
    public void addSystem(AbstractBoidSystem system, Color color, long delay) {
        systems.put(system, color);
        // On crée le premier événement de mise à jour pour ce système
        manager.addEvent(new BoidUpdateEvent(0, system, this, delay));
    }

    /**
     * Dessine tous les boids de tous les systèmes à l'écran.
     * Chaque boid est représenté par une petite image (loupe) orientée selon sa vitesse.
     */
    @Override
    public void draw() {
        gui.reset(); // On efface tout l'écran
        // On parcourt tous les systèmes qu'on a ajoutés
        for (Map.Entry<AbstractBoidSystem, Color> entry : systems.entrySet()) {
            Color color = entry.getValue();
            AbstractBoidSystem system = entry.getKey();

            // Pour chaque boid du système, on le dessine
            for (Boid b : system.getBoids()) {
                // On calcule l'angle pour orienter l'image selon la direction du boid
                Vecteur2D vitesse = b.getVitesse();
                double angle = Math.atan2(vitesse.y, vitesse.x);

                // On ajoute un élément graphique pour ce boid
                Vecteur2D position = b.getPosition();
                gui.addGraphicalElement(new RotatedImageElement(
                    (int) position.x,
                    (int) position.y,
                    "doc/resources/glass.png", // L'image de la loupe
                    20, // Largeur
                    20, // Hauteur
                    angle, // Angle de rotation
                    color // Couleur de teinte
                ));
            }
        }
    }

    /**
     * Lie tous les systèmes entre eux pour permettre les interactions inter-groupes.
     * Chaque système sera conscient de tous les autres systèmes, ce qui permet
     * aux prédateurs de chasser les proies et aux proies de fuir les prédateurs.
     * IMPORTANT : Appeler cette méthode après avoir ajouté tous les systèmes !
     */
    public void linkSystems() {
        java.util.List<AbstractBoidSystem> allSystems = new java.util.ArrayList<>(systems.keySet());
        
        // Pour chaque système, on lui donne la liste de tous les autres systèmes
        for (AbstractBoidSystem system : allSystems) {
            java.util.List<AbstractBoidSystem> others = new java.util.ArrayList<>(allSystems);
            others.remove(system); // On retire le système lui-même de sa liste
            system.setOtherSystems(others);
        }
    }

    /**
     * Réinitialise complètement la simulation : on replace tous les boids au hasard
     * et on recrée les événements de mise à jour.
     */
    @Override
    public void restart() {
        manager.restart(); // On vide la file d'événements

        // On réinitialise chaque système
        for (Map.Entry<AbstractBoidSystem, Color> entry : systems.entrySet()) {
            AbstractBoidSystem system = entry.getKey();
            int nbBoids = system.size();
            system.reInit(nbBoids); // Replacement aléatoire des boids

            // On recrée l'événement de mise à jour initial
            // Note: on utilise delay=1 par défaut (c'est une simplification)
            manager.addEvent(new BoidUpdateEvent(0, system, this, 1));
        }

        draw(); // On redessine l'état initial
    }
}
