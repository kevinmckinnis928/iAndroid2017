package org.wintrisstech.erik.iaroc;

import android.os.SystemClock;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import java.util.Random;
import org.wintrisstech.irobot.ioio.IRobotCreateAdapter;
import org.wintrisstech.irobot.ioio.IRobotCreateInterface;

/**
 * A Lada is an implementation of the IRobotCreateInterface, inspired by Vic's
 * awesome API. It is entirely event driven.
 *
 * @author Erik
 */
public class Lada extends IRobotCreateAdapter
{

    private static final String TAG = "Lada";
    private final Dashboard dashboard;
    /*
     * State variables:
     */
    private int speed = 100; // The normal speed of the Lada when going straight

    /**
     * States
     */
    private static enum State
    {

        STRAIGHT_FORWARD,
        LEFT_BACKWARD,
        RIGHT_BACKWARD,
        STRAIGHT_BACKWARD
        //TODO: Add states as needed, e.g., RIGHT_FORWARD, TURNING_LEFT, etc.
    }

    /**
     * Events
     */
    private static enum Event
    {

        BACKUP_DONE,
        RIGHT_BUMP,
        LEFT_BUMP,
        FRONT_BUMP,
        //TODO: Add events as needed, e.g., RED_BUOY_FOUND, RED_BUOY_LOST, etc. 
    }
    private State presentState;
    private int heading = 0;
    private static final int howFarToGoBackWhenBumped = 200;
    private int howFarBacked = 0;
    private boolean backingUp = false;
    //TODO Add variableas as needed, e.g., boolean redBouyInSight, boolean greenBouyInSight, etc
    private Random rand = new Random();

    /**
     * Constructs a Lada, an amazing machine!
     *
     * @param ioio the IOIO instance that the Lada can use to communicate with
     * other peripherals such as sensors
     * @param create an implementation of an iRobot
     * @param dashboard the Dashboard instance that is connected to the Lada
     * @throws ConnectionLostException
     */
    public Lada(IOIO ioio, IRobotCreateInterface create, Dashboard dashboard) throws ConnectionLostException
    {
        super(create);
        this.dashboard = dashboard;
        song(0, new int[]
                {
                    58, 10
                });
    }

    public void initialize() throws ConnectionLostException
    {
        dashboard.log("===========Start===========");
        heading = 0;
        backingUp = false;
        presentState = State.STRAIGHT_FORWARD;
        readSensors(SENSORS_GROUP_ID6);//Resets all counters in the Create to 0.
        driveDirect(speed, speed);
    }

    /**
     * This method is called repeatedly
     *
     * @throws ConnectionLostException
     */
    public void loop() throws ConnectionLostException
    {
        SystemClock.sleep(100); // Comment out or adjust sleep time as needed
        readSensors(SENSORS_GROUP_ID6);
        heading += getAngle();
        if (isBumpLeft() && isBumpRight())
        {
            dashboard.log("Bump front");
            fireEvent(Event.FRONT_BUMP);
        } else if (isBumpLeft())
        {
            dashboard.log("Bump left");
            fireEvent(Event.LEFT_BUMP);
        } else if (isBumpRight())
        {
            dashboard.log("Bump right");
            fireEvent(Event.RIGHT_BUMP);
        } else if (backingUp)
        {
            howFarBacked -= getDistance(); //
            if (howFarBacked > howFarToGoBackWhenBumped)
            {
                dashboard.log("Done backup");
                fireEvent(Event.BACKUP_DONE);
            }
        }
        // TODO: extend this with beacon reading events ...
    }

    /**
     * Implementation of a Moore finite state machine. The next state is
     * determined by the present state and the input event. The output is
     * determined by the state only (and, unlike Mealy FSM, not by the state and
     * the input event).
     *
     * @param event the input event
     * @throws ConnectionLostException
     */
    private void fireEvent(Event event) throws ConnectionLostException
    {
        presentState = nextState(presentState, event); // transit to the new state 
        switch (presentState)
        {
            case STRAIGHT_FORWARD:
                dashboard.log("Straight forward");
                dashboard.speak("going straight forward");
                dashboard.log("Heading = " + heading);
                backingUp = false;
                driveDirect(speed, speed);
                break;
            case LEFT_BACKWARD:
                dashboard.log("Left backward");
                dashboard.speak("going left backward");
                backingUp = true;
                howFarBacked = 0;
                driveDirect(-speed, -speed / 4);
                break;
            case RIGHT_BACKWARD:
                dashboard.log("Right backward");
                dashboard.speak("going right backward");
                backingUp = true;
                howFarBacked = 0;
                driveDirect(-speed / 4, -speed);
                break;
            case STRAIGHT_BACKWARD:
                dashboard.log("Straight backward");
                backingUp = true;
                howFarBacked = 0;
                if (rand.nextBoolean())
                {
                    dashboard.speak("going slightly right backward");
                    driveDirect(-speed / 2, -speed);
                } else
                {
                    dashboard.speak("going slightly left backward");
                    driveDirect(-speed, -speed / 2);
                }
                break;
            default:
                // should never get here
                dashboard.log("What the ?!! am I doing here?");
        }
    }

    /**
     * Implementation of a state transition table.
     *
     * @param state the present state
     * @param event the input event
     * @return the next state
     */
    private State nextState(State state, Event event)
    {
        //TODO: Add state transitions for the new states and events
        switch (state)
        {
            case STRAIGHT_FORWARD:
                switch (event)
                {
                    case BACKUP_DONE:
                        //Should never get here
                        dashboard.log("What the ?!! am I doing here?");
                        return state; // no transition
                    case FRONT_BUMP:
                        return State.STRAIGHT_BACKWARD;
                    case LEFT_BUMP:
                        return State.RIGHT_BACKWARD;
                    case RIGHT_BUMP:
                        return State.LEFT_BACKWARD;
                }
            case LEFT_BACKWARD:
                switch (event)
                {
                    case BACKUP_DONE:
                        return State.STRAIGHT_FORWARD;
                    case FRONT_BUMP:
                        return State.STRAIGHT_BACKWARD;
                    case LEFT_BUMP:
                        return State.RIGHT_BACKWARD;
                    case RIGHT_BUMP:
                        return State.LEFT_BACKWARD;
                }
            case RIGHT_BACKWARD:
                switch (event)
                {
                    case BACKUP_DONE:
                        return State.STRAIGHT_FORWARD;
                    case FRONT_BUMP:
                        return State.STRAIGHT_BACKWARD;
                    case LEFT_BUMP:
                        return State.RIGHT_BACKWARD;
                    case RIGHT_BUMP:
                        return State.LEFT_BACKWARD;
                }
            case STRAIGHT_BACKWARD:
                switch (event)
                {
                    case BACKUP_DONE:
                        return State.STRAIGHT_FORWARD;
                    case FRONT_BUMP:
                        return State.STRAIGHT_BACKWARD;
                    case LEFT_BUMP:
                        return State.RIGHT_BACKWARD;
                    case RIGHT_BUMP:
                        return State.LEFT_BACKWARD;
                }
            default:
                //Should never get here
                dashboard.log("What the ?!! am I doing here?");
                return state; // no transition
        }
    }

    public void stop() throws ConnectionLostException
    {
        driveDirect(0, 0); // stop the Create
    }
}
