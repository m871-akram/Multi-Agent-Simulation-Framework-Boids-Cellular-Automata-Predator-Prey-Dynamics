package multi_agents.TestTest;

import gui.GUISimulator;
import multi_agents.logic.PreyBoidSystem;
import multi_agents.simulation.BoidSimulator;

import java.awt.Color;

/**
 * Test simple avec un seul groupe de boids (des proies qui font du flocking classique).
 * On peut voir les trois règles de Reynolds en action : cohésion, alignement et séparation.
 */
public class TestBoids {

    public static void main(String[] args) {
        // On crée la fenêtre graphique (800x600 pixels, fond noir)
        GUISimulator gui = new GUISimulator(800, 600, Color.BLACK);

        // On crée le simulateur qui va gérer nos boids
        BoidSimulator simulator = new BoidSimulator(gui);

        // On définit les paramètres de la simulation
        int nbBoids = 50; // Nombre de boids dans le groupe
        // Les dimensions sont adaptatives : elles s'ajustent automatiquement à la fenêtre.
        // On utilise les dimensions réelles du GUI pour placer les boids correctement dès le départ.
        int width = gui.getPanelWidth();
        int height = gui.getPanelHeight();
        double rayonVision = 100.0; // Distance à laquelle un boid voit ses voisins
        double distanceSep = 30.0; // Distance minimale entre boids
        double Vmax = 3.0; // Vitesse maximale
        double Fmax = 0.15; // Force de steering maximale pour des mouvements fluides
        double angleVision = Math.PI * 0.8; // 144 degrés (vision assez large)
        double poiDECohesion = 1.0; // Poids de la cohésion
        double poiDEAlignement = 1.0; // Poids de l'alignement
        double poiDESeparation = 1.5; // Poids de la séparation (un peu plus fort)

        // On crée un système de proies avec ces paramètres
        PreyBoidSystem preySystem = new PreyBoidSystem(
            nbBoids, width, height,
            rayonVision, distanceSep, Vmax, Fmax, angleVision,
            poiDECohesion, poiDEAlignement, poiDESeparation
        );

        // On ajoute ce système au simulateur en bleu cyan, avec mise à jour à chaque pas (delay=1)
        simulator.addSystem(preySystem, Color.CYAN, 1);

        // On dessine l'état initial
        simulator.draw();
    }
}
