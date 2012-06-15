package org.wintrisstech.erik.iaroc;

import android.os.SystemClock;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.wintrisstech.irobot.ioio.IRobotCreateAdapter;
import org.wintrisstech.irobot.ioio.IRobotCreateInterface;
import org.wintrisstech.sensors.UltraSonicSensors;

public class Commander extends IRobotCreateAdapter {

    private static final String TAG = "Lada";
    private final Dashboard dashboard;
    public UltraSonicSensors sonar;
    public IOIO ioio;
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int FORWARD = 2;

    public Commander(IOIO ioio, IRobotCreateInterface create, Dashboard dashboard) throws ConnectionLostException {

        super(create);
        this.ioio = ioio;
        this.dashboard = dashboard;
        this.sonar = new UltraSonicSensors(ioio);
        song(0, new int[]{
                    58, 10
                });
    }

    public void initialize() throws ConnectionLostException {

        dashboard.log("===========Initialize===========");
        readSensors(SENSORS_GROUP_ID6);//Resets all counters in the Create to 0.
        dashboard.log("Battery Voltage" + getVoltage());//reads the voltage of the Robot
    }

    /**
     * This method is called repeatedly
     *
     * @throws ConnectionLostException
     */
    public void loop() throws ConnectionLostException {
        try {
            readSensors(SENSORS_GROUP_ID2);
            sonar.readUltrasonicSensors();
            dashboard.log("Left Sonar" + sonar.getLeftDistance());// reads the distance from left wall
            dashboard.log("Front Sonar" + sonar.getFrontDistance());// reads the distance from the front wall
            dashboard.log("Right Sonar" + sonar.getRightDistance());// reads the distance from the right wall
            dashboard.log("--------------------------------------");
            scenarios();

            SystemClock.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Commander.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stop() throws ConnectionLostException {

        dashboard.log("===========Stop===========");
        driveDirect(0, 0); // stop the Create
    }

    private void scenarios() throws ConnectionLostException {
        //scenario 1
        if (sonar.getLeftDistance() <= 950 && sonar.getRightDistance() <= 900 && sonar.getFrontDistance() >= 850) {

            if (getDistance() <= 990) {

                driveDirect(100, 100);
            }
        }
        //scenario 2
        if (sonar.getLeftDistance() <= 950 && sonar.getFrontDistance() <= 850 && sonar.getRightDistance() >= 900) {
            turn(90, RIGHT);
        }
        //scenario 3
        if (sonar.getLeftDistance() >= 950 && sonar.getFrontDistance() <= 850 && sonar.getRightDistance() <= 900) {
            turn(90, LEFT);
        }
        //scenario 4
        if (sonar.getLeftDistance() <= 950 && sonar.getFrontDistance() <= 850 && sonar.getRightDistance() <= 900) {
            turn(180, RIGHT);
        }
        //scenario 5
        if (sonar.getLeftDistance() >= 950 && sonar.getFrontDistance() <= 850 && sonar.getRightDistance() >= 900) {
            scenario5();
        }

        //scenario 6
        if (sonar.getLeftDistance() <= 950 && sonar.getFrontDistance() >= 850 && sonar.getRightDistance() >= 900) {
            scenario6();
        }
        //scenario 7
        if (sonar.getLeftDistance() >= 950 && sonar.getFrontDistance() >= 850 && sonar.getRightDistance() <= 900) {
            // randomly choose whether to go straight or turn 90 degrees left
        }
        //scenario 8
        if (sonar.getLeftDistance() >= 950 && sonar.getFrontDistance() >= 850 && sonar.getRightDistance() >= 900) {
            // randomly choose whether to go straight or turn 90 degrees right or left
        }
    }

    public void turn(int angleToTurn, int directionToTurn) throws ConnectionLostException {

        System.out.println("Engine.turn(" + angleToTurn + ", " + directionToTurn + ");");
        int angleSoFar = 0;
        drive(directionToTurn);
        System.out.println("angleToTurn: " + angleToTurn);
        while (angleSoFar < angleToTurn) {
            angleSoFar = Math.abs(getAngle());
            System.out.println("angleSoFar: " + angleSoFar);
        }
        stop();
    }

    private void drive(int direction) throws ConnectionLostException {
        drive(direction, 0, 0);
    }

    private void drive(int direction, int leftSpeedUp, int rightSpeedUp) throws ConnectionLostException {
        if (direction == FORWARD) {
            driveDirect(100, 100);
        } 
        else if (direction == LEFT) {
            driveDirect(100, -100);
        } else if (direction == RIGHT) {
            driveDirect(-100, 100);
        } else {
            throw new RuntimeException("I don't know how to go that way!");
        }
    }
    private void scenario5() throws ConnectionLostException
    {
        double x = Math.random();
        if(x <= .5)
        {
            turn(90, RIGHT);
        }
        else turn(90, LEFT);
    }
    private void scenario6() throws ConnectionLostException
    {
        double x = Math.random();
        if(x <= .5)
        {
            turn(90, RIGHT);
        }
        else drive(FORWARD);
    }
}
