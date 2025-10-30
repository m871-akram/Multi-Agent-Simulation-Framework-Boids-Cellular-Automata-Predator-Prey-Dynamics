package Koora;

import java.awt.Color;
import gui.GUISimulator;
import java.awt.Point;
import java.util.Arrays;
import java.util.List;

public class TestBallsSimulator {
    public static void main(String[] args) {
        // Crée une fenêtre graphique de 800x600 avec fond noir
        GUISimulator gui = new GUISimulator(800, 600, Color.BLACK);

        // Crée quelques positions initiales
        List<Point> PositionsInit = Arrays.asList(
                        new Point(100, 100),
                        new Point(200, 150),
                        new Point(300, 250)
                );

        List<Point> Vitesse = Arrays.asList(
                new Point(5, 4),
                new Point(7, 6),
                new Point(8, 3)
        );


        // Instancie le modèle logique
        Balls balls = new Balls(PositionsInit, Vitesse, 10);

        // Crée et connecte le simulateur à la fenêtre GUI
        new BallsSimulator(gui, balls, Color.decode("#1f77b4"));

    }
}


