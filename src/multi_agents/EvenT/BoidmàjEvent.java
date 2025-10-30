package multi_agents.EvenT;

import multi_agents.logic.BoidSystem;
import multi_agents.simulation.BoidSimulator;

/**
 * Événement qui représente la mise à jour d'un système de boids.
 * Quand il s'exécute, il fait avancer la simulation d'un pas, redessine l'écran,
 * et se replanifie lui-même pour continuer la boucle de simulation.
 */
public class BoidmàjEvent extends Event {
    private BoidSystem system;
    private BoidSimulator simulator;
    private long delay;

    /**
     * Constructeur qui crée un événement de mise à jour.
     * @param date la date à laquelle cet événement doit se produire
     * @param system le système de boids concerné (proies ou prédateurs)
     * @param simulator le simulateur qui gère l'affichage
     * @param delay le délai avant la prochaine mise à jour (fréquence de rafraîchissement)
     */
    public BoidmàjEvent(long date, BoidSystem system, BoidSimulator simulator, long delay) {
        super(date);
        this.system = system;
        this.simulator = simulator;
        this.delay = delay;
    }

    /**
     * Exécute cet événement : on fait une étape de simulation, on redessine,
     * puis on se replanifie automatiquement pour continuer la boucle.
     * C'est ce qui fait que la simulation tourne en continu !
     */
    @Override
    public void execute() {
        // On met à jour les dimensions du système pour qu'il s'adapte à la taille de la fenêtre
        system.màjDimensions(
            simulator.getGui().getPanelWidth(),
            simulator.getGui().getPanelHeight()
        );
        system.step(); // On fait avancer le système d'un pas
        simulator.draw(); // On redessine tout à l'écran
        // On se replanifie pour la prochaine fois (date actuelle + delay)
        simulator.getManager().addEvent(new BoidmàjEvent(getDate() + delay, system, simulator, delay));
    }
}
