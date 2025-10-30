package multi_agents.tests;

import gui.GUISimulator;
import multi_agents.core.PreyBoidSystem;
import multi_agents.core.PredatorBoidSystem;
import multi_agents.sim.BoidSimulator;

import java.awt.Color;

/**
 * Test sympa avec deux groupes de boids : des proies et des prédateurs.
 * Les proies font du flocking normal, tandis que les prédateurs les chassent.
 * En plus, les deux groupes se mettent à jour à des rythmes différents pour voir
 * l'effet des fréquences asynchrones.
 */
public class TestMultiGroupBoids {

    public static void main(String[] args) {
        // On crée la fenêtre graphique
        GUISimulator gui = new GUISimulator(800, 600, Color.BLACK);

        // On crée le simulateur
        BoidSimulator simulator = new BoidSimulator(gui);

        // Les dimensions sont adaptatives : elles s'ajustent automatiquement à la fenêtre.
        // On utilise les dimensions réelles du GUI pour placer les boids correctement dès le départ.
        int width = gui.getPanelWidth();
        int height = gui.getPanelHeight();

        // === Configuration du système de proies ===
        int nbPreys = 60; // Assez de proies pour faire un beau groupe
        double preyVisionRadius = 80.0;
        double preySeparationDist = 25.0;
        double preyVmax = 2.5; // Les proies sont assez rapides
        double preyMaxForce = 0.2; // Force de steering pour des mouvements plus réactifs
        double preyFieldOfView = Math.PI * 0.9; // 162 degrés (vision très large pour détecter les prédateurs)
        double preyCohesion = 1.0;
        double preyAlignment = 1.0;
        double preySeparation = 1.5;

        PreyBoidSystem preySystem = new PreyBoidSystem(
            nbPreys, width, height,
            preyVisionRadius, preySeparationDist, preyVmax, preyMaxForce, preyFieldOfView,
            preyCohesion, preyAlignment, preySeparation
        );

        // === Configuration du système de prédateurs ===
        int nbPredators = 8; // Quelques prédateurs seulement
        double predatorVisionRadius = 120.0; // Ils voient plus loin
        double predatorSeparationDist = 40.0;
        double predatorVmax = 3.5; // Ils sont un peu plus rapides que les proies
        double predatorMaxForce = 0.15; // Force de steering plus faible (virages plus larges)
        double predatorFieldOfView = Math.PI * 0.6; // 108 degrés (vision plus focalisée)
        double predatorCohesion = 0.8; // Moins de cohésion (ils chassent plus individuellement)
        double predatorAlignment = 0.8;
        double predatorSeparation = 1.2;
        double chaseWeight = 2.0; // Importance de la chasse

        PredatorBoidSystem predatorSystem = new PredatorBoidSystem(
            nbPredators, width, height,
            predatorVisionRadius, predatorSeparationDist, predatorVmax,
            predatorMaxForce, predatorFieldOfView,
            predatorCohesion, predatorAlignment, predatorSeparation,
            chaseWeight
        );

        // === On ajoute les deux systèmes au simulateur ===
        // Les proies bougent vite : mise à jour à chaque pas (delay=1)
        simulator.addSystem(preySystem, Color.CYAN, 1);

        // Les prédateurs bougent moins souvent : mise à jour tous les 3 pas (delay=3)
        simulator.addSystem(predatorSystem, Color.RED, 3);

        // IMPORTANT : On lie les systèmes pour qu'ils puissent interagir
        // Les prédateurs pourront chasser les proies, les proies pourront fuir
        simulator.linkSystems();

        // On dessine l'état initial
        simulator.draw();

        // Petit message pour l'utilisateur
        System.out.println("=== Simulation Multi-Groupes avec Capture ===");
        System.out.println("Proies (cyan): " + nbPreys + " boids, mise à jour: chaque pas");
        System.out.println("Prédateurs (rouge): " + nbPredators + " boids, mise à jour: tous les 3 pas");
        System.out.println("\nComportements dynamiques:");
        System.out.println("- Les proies fuient les prédateurs proches");
        System.out.println("- Les prédateurs chassent la proie la plus proche");
        System.out.println("- Les proies sont capturées si un prédateur s'approche à moins de 10 pixels");
        System.out.println("- Le nombre de proies diminuera au fil du temps !");
    }
}
