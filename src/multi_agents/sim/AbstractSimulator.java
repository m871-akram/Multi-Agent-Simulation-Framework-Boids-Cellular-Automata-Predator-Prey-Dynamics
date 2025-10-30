package multi_agents.sim;

import gui.GUISimulator;
import gui.Simulable;
import multi_agents.events.EventManager;

/**
 * Classe abstraite qui sert de base à tous les simulateurs.
 * Elle unifie la logique commune : gestion des événements, boutons Next/Restart, etc.
 * Chaque simulateur concret n'aura plus qu'à implémenter draw() pour définir comment il dessine.
 */
public abstract class AbstractSimulator implements Simulable {
    protected final GUISimulator gui;
    protected final EventManager manager;

    /**
     * Constructeur qui initialise le simulateur avec son interface graphique.
     * On crée aussi un gestionnaire d'événements et on se connecte à la GUI.
     * @param gui l'interface graphique (fenêtre de simulation)
     */
    public AbstractSimulator(GUISimulator gui) {
        this.gui = gui;
        this.manager = new EventManager();
        gui.setSimulable(this); // On dit à la GUI que c'est nous le simulateur
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
     * Méthode abstraite pour dessiner l'état actuel de la simulation.
     * Chaque simulateur concret devra définir comment il affiche ses éléments.
     */
    public abstract void draw();

    /**
     * Appelée quand on clique sur le bouton "Next" de l'interface.
     * On exécute simplement le prochain événement dans la file.
     */
    @Override
    public void next() {
        manager.next();
    }

    /**
     * Appelée quand on clique sur le bouton "Restart" de l'interface.
     * On vide la file d'événements et on redessine l'état initial.
     */
    @Override
    public void restart() {
        manager.restart();
        draw();
    }
}
