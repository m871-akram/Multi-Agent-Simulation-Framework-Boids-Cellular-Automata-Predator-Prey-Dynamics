package multi_agents.TestTest;

import multi_agents.EvenT.Event;
import multi_agents.EvenT.EventManager;

/**
 * Test basé sur la figure 12 du sujet :
 * On crée un gestionnaire d'événements et on y ajoute :
 * - des événements [PING] tous les 2 pas de temps,
 * - des événements [PONG] tous les 3 pas de temps.
 */
public class TestEventManager {

    static class MessageEvent extends Event {
        private String message;

        public MessageEvent(long date, String message) {
            super(date);
            this.message = message;
        }

        @Override
        public void execute() {
            System.out.println(getDate() + " " + message);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        // On cr é e un s im ul at eu r
        EventManager manager = new EventManager();

        // On poste un é v é nement [ PING ] tous les deux pas de temps
        for (int i = 2; i <= 10; i += 2) {
            manager.addEvent(new MessageEvent(i, "[PING]"));
        }

        // On poste un é v é nement [ PONG ] tous les trois pas de temps
        for (int i = 3; i <= 9; i += 3) {
            manager.addEvent(new MessageEvent(i, "[PONG]"));
        }

        while (!manager.isFinished()) {
            System.out.println("Next... Current date: " + (manager.getCurrentDate() + 1));
            manager.next();
            Thread.sleep(1000);
        }

    }
}