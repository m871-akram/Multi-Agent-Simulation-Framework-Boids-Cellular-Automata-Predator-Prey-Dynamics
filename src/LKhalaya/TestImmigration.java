package LKhalaya;

import gui.GUISimulator;

import java.awt.Color;

public class TestImmigration {
    public static void main(String[] args) {
        GUISimulator gui = new GUISimulator(800, 600, Color.BLACK);

        int rows = 60;
        int cols = 80;
        int nStates = 4; // 4 états comme dans le sujet

        ImmigrationGrid grid = new ImmigrationGrid(rows, cols, nStates);
        grid.randomInit(42L);

        // Palette de couleurs (modifiable)
        Color[] palette = {
                Color.decode("#1f77b4"), // bleu
                Color.decode("#ff7f0e"), // orange
                Color.decode("#2ca02c"), // vert
                Color.decode("#d62728")  // rouge
        };

        new ImmigrationSimulator(gui, grid, palette, 5);
    }
}