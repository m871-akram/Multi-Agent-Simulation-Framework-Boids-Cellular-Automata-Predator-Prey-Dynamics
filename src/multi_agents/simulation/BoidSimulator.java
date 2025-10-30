package multi_agents.simulation;

import gui.GUISimulator;
import gui.Simulable;
import multi_agents.logic.BoidSystem;
import multi_agents.logic.Boid;
import multi_agents.logic.Vecteur2D;
import multi_agents.EvenT.BoidmàjEvent;
import multi_agents.EvenT.EventManager;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simulateur pour les boids, capable de gérer plusieurs systèmes en même temps.
 * Par exemple, on peut avoir un système de proies et un système de prédateurs qui évoluent
 * chacun à leur propre rythme avec leur propre couleur d'affichage.
 * 
 * Cette classe unifie la logique de simulation : gestion des événements, rendu graphique,
 * et boutons Next/Restart de l'interface.
 */
public class BoidSimulator implements Simulable {
    protected final GUISimulator gui;
    protected final EventManager manager;
    private Map<BoidSystem, Color> systems;

    /**
     * Constructeur qui crée un simulateur de boids.
     * @param gui l'interface graphique où on va dessiner les boids
     */
    public BoidSimulator(GUISimulator gui) {
        this.gui = gui;
        this.manager = new EventManager();
        gui.setSimulable(this); // On dit à la GUI que c'est nous le simulateur
        // LinkedHashMap pour garder l'ordre d'ajout des systèmes
        this.systems = new LinkedHashMap<>();
    }

    /**
     * Renvoie le gestionnaire d'événements de ce simulateur.
     * @return le gestionnaire d'événements
     */
    public EventManager getManager() {
        return manager;
    }

    /**
     * Renvoie l'interface graphique (GUI) de ce simulateur.
     * @return l'interface graphique GUISimulator
     */
    public GUISimulator getGui() {
        return gui;
    }

    /**
     * Ajoute un nouveau système de boids à la simulation.
     * On peut appeler cette méthode plusieurs fois pour avoir plusieurs groupes.
     * @param system le système de boids (proies, prédateurs, etc.)
     * @param color la couleur pour afficher ce groupe
     * @param delay la fréquence de mise à jour (1 = rapide, 3 = plus lent, etc.)
     */
    public void addSystem(BoidSystem system, Color color, long delay) {
        systems.put(system, color);
        // On crée le premier événement de mise à jour pour ce système
        manager.addEvent(new BoidmàjEvent(0, system, this, delay));
    }

    /**
     * Dessine tous les boids de tous les systèmes à l'écran.
     * Chaque boid est représenté par une petite image (loupe) orientée selon sa vitesse.
     * La couleur est assombrie quand le boid a peu d'énergie (< 30).
     * Les boids morts ne sont pas affichés.
     */
    public void draw() {
        gui.reset(); // On efface tout l'écran
        // On parcourt tous les systèmes qu'on a ajoutés
        for (Map.Entry<BoidSystem, Color> entry : systems.entrySet()) {
            Color baseColor = entry.getValue();
            BoidSystem system = entry.getKey();

            // Pour chaque boid du système, on le dessine
            for (Boid b : system.getBoids()) {
                // Ignorer les boids morts
                if (!b.estvivant()) {
                    continue;
                }
                
                // On calcule l'angle pour orienter l'image selon la direction du boid
                Vecteur2D vitesse = b.getVitesse();
                double angle = Math.atan2(vitesse.y, vitesse.x);

                // Ajuster la couleur selon l'énergie (assombrir si énergie < 30)
                Color color = baseColor;
                if (b.getenergie() < 30) {
                    float factor = (float) (b.getenergie() / 30.0); // 0.0 à 1.0
                    color = new Color(
                        (int) (baseColor.getRed() * factor),
                        (int) (baseColor.getGreen() * factor),
                        (int) (baseColor.getBlue() * factor)
                    );
                }

                // On ajoute un élément graphique pour ce boid
                Vecteur2D position = b.getPosition();
                gui.addGraphicalElement(new RotatedImageElement(
                    (int) position.x,
                    (int) position.y,
                    "doc/resources/glass.png", // L'image de la loupe
                    20, // Largeur
                    20, // Hauteur
                    angle, // Angle de rotation
                    color // Couleur de teinte (ajustée selon l'énergie)
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
        java.util.List<BoidSystem> allSystems = new java.util.ArrayList<>(systems.keySet());
        
        // Pour chaque système, on lui donne la liste de tous les autres systèmes
        for (BoidSystem system : allSystems) {
            java.util.List<BoidSystem> others = new java.util.ArrayList<>(allSystems);
            others.remove(system); // On retire le système lui-même de sa liste
            system.setinterGroups(others);
        }
    }

    /**
     * Appelée quand on clique sur le bouton "Next" de l'interface.
     * On exécute simplement le prochain événement dans la file.
     */
    @Override
    public void next() {
        manager.next();
    }

    /**
     * Réinitialise complètement la simulation : on replace tous les boids au hasard
     * et on recrée les événements de mise à jour.
     * Appelée quand on clique sur le bouton "Restart" de l'interface.
     */
    @Override
    public void restart() {
        manager.restart(); // On vide la file d'événements

        // On réinitialise chaque système
        for (Map.Entry<BoidSystem, Color> entry : systems.entrySet()) {
            BoidSystem system = entry.getKey();
            int nbBoids = system.size();
            system.reInit(nbBoids); // Replacement aléatoire des boids

            // On recrée l'événement de mise à jour initial
            // Note: on utilise delay=1 par défaut (c'est une simplification)
            manager.addEvent(new BoidmàjEvent(0, system, this, 1));
        }

        draw(); // On redessine l'état initial
    }
}
