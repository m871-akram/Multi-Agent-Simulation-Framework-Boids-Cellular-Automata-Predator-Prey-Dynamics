package multi_agents.TestTest;

import gui.GUISimulator;
import multi_agents.logic.ProieBoidSystem;
import multi_agents.simulation.BoidSimulateur;
import java.awt.Color;

/**
 * Test simple avec un seul groupe de boids 
 * On peut voir les trois règles de Reynolds en action : cohésion, alignement et séparation.
 */
public class TestBoids {

    public static void main(String[] args) {
        // On crée la fenêtre graphique (800x600 pixels, fond noir)
        GUISimulator gui = new GUISimulator(800, 600, Color.BLACK);

        // On crée le simulateur qui va gérer nos boids
        BoidSimulateur simulateur = new BoidSimulateur(gui);

        // On définit les paramètres de la simulation
        int nbBoids = 50; 
        int width = 800;
        int height = 600;
        double rayonVision = 100.0; 
        double distanceSep = 30.0; 
        double Vmax = 3.0; 
        double Fmax = 0.15; 
        double angleVision = Math.PI * 0.8; 
        double poiDECohesion = 1.0; 
        double poiDEAlignement = 1.0; 
        double poiDESeparation = 1.5; 

        // On crée un système de proies avec ces paramètres
        ProieBoidSystem proieSystem = new ProieBoidSystem(
            nbBoids, width, height,
            rayonVision, distanceSep, Vmax, Fmax, angleVision,
            poiDECohesion, poiDEAlignement, poiDESeparation
        );

        // On ajoute ce système au simulateur en bleu , avec mise à jour à chaque pas (delay=1)
        simulateur.addSystem(proieSystem, Color.BLUE, 1);

        // On dessine l'état initial
        simulateur.draw();
    }
}
