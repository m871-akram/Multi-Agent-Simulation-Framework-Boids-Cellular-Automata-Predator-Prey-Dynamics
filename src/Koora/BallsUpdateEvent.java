package Koora;

import multi_agents.EvenT.Event;

/** Event fired each simulation step to move the balls and reschedule itself. */
public class BallsUpdateEvent extends Event {
    private final Balls balls;
    private final BallsSimulator simulator;

    /** Creates an update event at the given date, linked to the balls model and its simulator. */
    public BallsUpdateEvent(long date, Balls balls, BallsSimulator simulator) {
        super(date);
        this.balls = balls;
        this.simulator = simulator;
    }

    /** Moves the balls, redraws the frame, then schedules the next update event. */
    @Override
    public void execute() {
        balls.Rebond(simulator.gui.getWidth(), simulator.gui.getHeight());
        simulator.draw();
        simulator.manager.addEvent(new BallsUpdateEvent(getDate() + 1, balls, simulator));
    }
}