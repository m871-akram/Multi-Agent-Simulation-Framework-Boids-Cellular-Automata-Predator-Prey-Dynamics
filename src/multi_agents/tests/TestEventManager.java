package multi_agents.tests;

import multi_agents.events.Event;
import multi_agents.events.EventManager;

/**
 * Petit test pour vérifier que le système d'événements fonctionne bien.
 * On fait un simple PING/PONG : à chaque fois qu'un message s'affiche,
 * il planifie automatiquement le message suivant.
 */
public class TestEventManager {

    /**
     * Classe interne qui représente un événement de message.
     * Quand il s'exécute, il affiche un texte et peut planifier le message suivant.
     */
    static class MessageEvent extends Event {
        private String message;
        private EventManager manager;
        private boolean addNext;

        public MessageEvent(long date, String message, EventManager manager, boolean addNext) {
            super(date);
            this.message = message;
            this.manager = manager;
            this.addNext = addNext;
        }

        @Override
        public void execute() {
            System.out.println("Date " + getDate() + ": " + message);
            // Si on est avant la date 20, on continue le ping-pong
            if (addNext && getDate() < 20) {
                if (message.equals("PING")) {
                    // Après un PING on envoie un PONG 2 unités de temps plus tard
                    manager.addEvent(new MessageEvent(getDate() + 2, "PONG", manager, true));
                } else {
                    // Après un PONG on envoie un PING
                    manager.addEvent(new MessageEvent(getDate() + 2, "PING", manager, true));
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Test EventManager: PING/PONG ===");

        // On crée le gestionnaire d'événements
        EventManager manager = new EventManager();

        // On lance le premier événement (PING à la date 0)
        manager.addEvent(new MessageEvent(0, "PING", manager, true));

        // On fait tourner la simulation jusqu'à ce qu'il n'y ait plus d'événements
        while (!manager.isEmpty()) {
            manager.next();
        }

        System.out.println("\n=== Test terminé ===");
    }
}
