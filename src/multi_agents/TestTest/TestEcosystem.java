package multi_agents.TestTest;

import gui.GUISimulator;
import multi_agents.logic.ProieBoidSystem;
import multi_agents.logic.PredateurBoidSystem;
import multi_agents.simulation.BoidSimulateur;
import java.awt.Color;

/**
 * Test pour observer l'évolution dynamique d'un écosystème proie-prédateur : 
 * - Les proies (bleues) se reproduisent si elles survivent assez longtemps et ont assez d'énergie
 * - Les prédateurs (rouges) perdent de l'énergie et doivent manger des proies pour survivre
 * - Les deux populations oscillent selon un modèle Lotka-Volterra simplifié
 *   beaucoup de proies → beaucoup de prédateurs → peu de proies → peu de prédateurs
 * - Des couleurs plus sombres pour les boids affamés (énergie < 30)
 */
public class TestEcosystem {
    public static void main(String[] args) {
        // Créer la fenêtre de simulation
        GUISimulator gui = new GUISimulator(800, 600, Color.BLACK);
        BoidSimulateur simulator = new BoidSimulateur(gui);

        int width = 800;
        int height = 600;

        // Population initiale plus grande pour un écosystème stable
        int nbProies = 30;
        double proieVision = 80.0;
        double proieSeparationDist = 20.0;
        double proieVmax = 3.0;
        double proieFmax = 0.15;
        double proieVisionAngle = Math.PI * 1.5; // 270 degrés
        double proieCohesionWeight = 1.0;
        double proieAlignmentWeight = 1.2;
        double proieSeparationWeight = 1.5;

        // Population initiale plus petite (ratio classique prédateur/proie = 1:3)
        int nbPredateurs = 10;
        double predateurVision = 120.0; // Vision plus large
        double predateurSeparationDist = 25.0;
        double predateurVmax = 4.0; // Plus rapides que les proies
        double predateurFmax = 0.2;
        double predateurVisionAngle = Math.PI * 1.8; // 324 degrés
        double predateurCohesionWeight = 0.8;
        double predateurAlignmentWeight = 0.9;
        double predateurSeparationWeight = 1.2;

        ProieBoidSystem proies = new ProieBoidSystem(
            nbProies, width, height,
            proieVision, proieSeparationDist, proieVmax, proieFmax, proieVisionAngle,
            proieCohesionWeight, proieAlignmentWeight, proieSeparationWeight
        );
        
        PredateurBoidSystem predateurs = new PredateurBoidSystem(
            nbPredateurs, width, height,
            predateurVision, predateurSeparationDist, predateurVmax, predateurFmax, predateurVisionAngle,
            predateurCohesionWeight, predateurAlignmentWeight, predateurSeparationWeight
        );

        // Les proies sont bleues, les prédateurs rouges
        simulator.addSystem(proies, new Color(50, 150, 255), 1);      // Bleu clair
        simulator.addSystem(predateurs, new Color(255, 80, 80), 1);   // Rouge clair
        
        // Lier les systèmes pour permettre les interactions
        simulator.linkSystems();
        
        // Afficher l'état initial
        simulator.draw();

    }
}
