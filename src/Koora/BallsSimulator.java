package Koora;

import gui.GUISimulator;
import gui.Oval;
import gui.Simulable;
import java.awt.Color;
import java.awt.Point;

import multi_agents.EvenT.EventManager;

/**
 * Relie la classe Balls (logique) à l'interface graphique GUISimulator.
 * Elle implémente Simulable pour répondre aux boutons "Suivant" et "Début".
 */
public class BallsSimulator implements Simulable {
    public GUISimulator gui;   // la fenêtre graphique
    private Balls balls;        // le modèle logique
    private Color ballColor;    // couleur des balles
    private int rayon;
    public EventManager manager;// rayon des balles

    /**
     * Constructeur : associe la simulation à la GUI.
     */
    public BallsSimulator(GUISimulator gui, Balls balls, Color color) {
        this.gui = gui;
        this.balls = balls;
        this.ballColor = color;
        this.manager = new EventManager();
        this.manager.addEvent(new BallsUpdateEvent(0, balls, this));
        this.rayon = balls.getRayon();

        gui.setSimulable(this); // lie le simulateur à la fenêtre
        draw();
    }

    /**
     * Méthode appelée à chaque clic sur "Suivant" ou à chaque pas de lecture automatique.
     * Fait bouger les balles et gère les rebonds.
     */
    @Override
    public void next() {
        manager.next();
        System.out.println(balls);
    }

    /**
     * Méthode appelée lors du clic sur "Début".
     * Réinitialise les balles à leur position initiale.
     */

    @Override
    public void restart() {
        balls.reInit();
        manager.restart();
        manager.addEvent(new BallsUpdateEvent(0, balls, this));
        draw();
    }

    /**
     * Redessine les balles à l’écran selon leur position actuelle.
     */
    protected void draw() {
        gui.reset(); // efface la fenêtre avant de redessiner
        for (Point p : balls.getBalls()) {
            gui.addGraphicalElement(new Oval(p.x, p.y, ballColor, ballColor, rayon));
        }
    }
}