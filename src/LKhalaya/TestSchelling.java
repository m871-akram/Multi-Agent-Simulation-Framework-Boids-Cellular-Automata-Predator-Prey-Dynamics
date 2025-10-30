package LKhalaya;

import gui.GUISimulator;
import java.awt.Color;

public class TestSchelling {
    public static void main(String[] args) {
        GUISimulator gui = new GUISimulator(800, 600, Color.BLACK);

        int rows = 50;
        int cols = 50;
        int nColors = 4;
        int K = 2;
        double vacantRatio = 0.2;

        SchellingGrid grid = new SchellingGrid(rows, cols, nColors, K, vacantRatio);
        Color[] palette = {
                Color.decode("#1f77b4"), // bleu
                Color.decode("#ff7f0e"), // orange
                Color.decode("#2ca02c"), // vert
                Color.decode("#d62728")  // rouge
        };

        new SchellingSimulator(gui, grid, 5, palette);
    }
}