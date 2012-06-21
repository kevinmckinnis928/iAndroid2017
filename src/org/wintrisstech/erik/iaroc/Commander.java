package org.wintrisstech.erik.iaroc;

import android.os.SystemClock;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.wintrisstech.irobot.ioio.IRobotCreateAdapter;
import org.wintrisstech.irobot.ioio.IRobotCreateInterface;
import org.wintrisstech.irobot.ioio.IRobotCreateScript;
import org.wintrisstech.sensors.UltraSonicSensors;

public class Commander extends IRobotCreateAdapter {

    public static final int INCREASED_SPEED = 101;
    public static final int STANDARD_SPEED = 100;
    private static final String TAG = "Lada";
    private final Dashboard dashboard;
    public UltraSonicSensors sonar;
    public IOIO ioio;
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int FORWARD = 2;
    public static final int BACKWARD = 3;
    public static final int speed = 200;
    public static final int oneBlockDistance = 675;
    int distance = 0;
    private boolean done = false;
    private final int RIGHT_WALL_PRESENT_SONAR_VALUE = 2000;
    private final int FRONT_WALL_PRESENT_SONAR_VALUE = 2000;
    private final int LEFT_WALL_PRESENT_SONAR_VALUE = 2000;
    private final int CORRECTION_EQUILIBRIUM = 836;

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
        dashboard.log("-----------------------------------");
    }

    /**
     * This method is called repeatedly
     *
     * @throws ConnectionLostException
     */
    public void loop() throws ConnectionLostException {
        if (isProgramDone()) {
            SystemClock.sleep(1000);
            return;
        }

        SystemClock.sleep(1000);

        readPrintUltraSonic();

        SystemClock.sleep(1000);

        readPrintUltraSonic();

        scenarios();

        if (isBumping()) {
            driveDirect(0, 0);
        }
        SystemClock.sleep(1000);
    }

    private void readPrintUltraSonic() {
        try {
            sonar.readUltrasonicSensors();
        } catch (ConnectionLostException ex) {
            Logger.getLogger(Commander.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Commander.class.getName()).log(Level.SEVERE, null, ex);
        }
        dashboard.log("Left Sonar" + sonar.getLeftDistance());// reads the distance from left wall
        dashboard.log("Front Sonar" + sonar.getFrontDistance());// reads the distance from the front wall
        dashboard.log("Right Sonar" + sonar.getRightDistance());// reads the distance from the right wall
        dashboard.log("--------------------------------------");
    }

    public void stop() throws ConnectionLostException {

        dashboard.log("===========Stop===========");
        driveDirect(0, 0); // stop the Create
    }

    private void scenarios() throws ConnectionLostException {

        //scenario 1
        if (sonar.getLeftDistance() <= LEFT_WALL_PRESENT_SONAR_VALUE && sonar.getRightDistance() <= RIGHT_WALL_PRESENT_SONAR_VALUE && sonar.getFrontDistance() >= FRONT_WALL_PRESENT_SONAR_VALUE) {
            dashboard.log("scenario 1 ---------------");
            if (getDistance() <= 990) {
                driveDistance(speed, oneBlockDistance);
            }
        } //scenario 2
        else if (sonar.getLeftDistance() <= LEFT_WALL_PRESENT_SONAR_VALUE && sonar.getFrontDistance() <= FRONT_WALL_PRESENT_SONAR_VALUE && sonar.getRightDistance() >= RIGHT_WALL_PRESENT_SONAR_VALUE) {
            dashboard.log("scenario 2 ---------------");
            turnUsingScript(RIGHT);
            driveDistance(speed, oneBlockDistance);
        } //scenario 3
        else if (sonar.getLeftDistance() >= LEFT_WALL_PRESENT_SONAR_VALUE && sonar.getFrontDistance() <= FRONT_WALL_PRESENT_SONAR_VALUE && sonar.getRightDistance() <= RIGHT_WALL_PRESENT_SONAR_VALUE) {
            dashboard.log("scenario 3 ---------------");
            turnUsingScript(LEFT);
            driveDistance(speed, oneBlockDistance);
        } //scenario 4
        else if (sonar.getLeftDistance() <= LEFT_WALL_PRESENT_SONAR_VALUE && sonar.getFrontDistance() <= FRONT_WALL_PRESENT_SONAR_VALUE && sonar.getRightDistance() <= RIGHT_WALL_PRESENT_SONAR_VALUE) {
            dashboard.log("scenario 4 ---------------");
            turnUsingScript(BACKWARD);
            driveDistance(speed, oneBlockDistance);
        } //scenario 5
        else if (sonar.getLeftDistance() >= LEFT_WALL_PRESENT_SONAR_VALUE && sonar.getFrontDistance() <= FRONT_WALL_PRESENT_SONAR_VALUE && sonar.getRightDistance() >= RIGHT_WALL_PRESENT_SONAR_VALUE) {
            dashboard.log("scenario 5 ---------------");
            scenario5();
        } //scenario 6
        else if (sonar.getLeftDistance() <= LEFT_WALL_PRESENT_SONAR_VALUE && sonar.getFrontDistance() >= FRONT_WALL_PRESENT_SONAR_VALUE && sonar.getRightDistance() >= RIGHT_WALL_PRESENT_SONAR_VALUE) {
            dashboard.log("scenario 6 ---------------");
            scenario6();
        } //scenario 7
        else if (sonar.getLeftDistance() >= LEFT_WALL_PRESENT_SONAR_VALUE && sonar.getFrontDistance() >= FRONT_WALL_PRESENT_SONAR_VALUE && sonar.getRightDistance() <= RIGHT_WALL_PRESENT_SONAR_VALUE) {
            dashboard.log("scenario 7 ---------------");
            scenario7();
        } //scenario 8
        else if (sonar.getLeftDistance() >= LEFT_WALL_PRESENT_SONAR_VALUE && sonar.getFrontDistance() >= FRONT_WALL_PRESENT_SONAR_VALUE && sonar.getRightDistance() >= RIGHT_WALL_PRESENT_SONAR_VALUE) {
            dashboard.log("scenario 8 ---------------");
            scenario8();
        }
    }

    public void turn(int angleToTurn, int directionToTurn) throws ConnectionLostException {

        dashboard.log("Engine.turn(" + angleToTurn + ", " + directionToTurn + ");");
        int angleSoFar = 0;
        drive(directionToTurn);
        dashboard.log("angleToTurn: " + angleToTurn);
        while (angleSoFar < angleToTurn) {
            readSensors(SENSORS_ANGLE);
            angleSoFar += Math.abs(getAngle());
            dashboard.log("angleSoFar: " + angleSoFar);
        }
        stop();
    }

    public void turnUsingScript(int direction) throws ConnectionLostException {
        IRobotCreateScript turnUsingScript = new IRobotCreateScript();
        if (direction == RIGHT) {
            turnUsingScript.turnInPlace(100, true);
            turnUsingScript.waitAngle(-90);
            turnUsingScript.drive(0, 0);
        } else if (direction == LEFT) {
            turnUsingScript.turnInPlace(100, false);
            turnUsingScript.waitAngle(90);
            turnUsingScript.drive(0, 0);
        } else if (direction == FORWARD) {
            turnUsingScript.turnInPlace(100, false);
            turnUsingScript.waitAngle(360);
            turnUsingScript.drive(0, 0);
        } else if (direction == BACKWARD) {
            turnUsingScript.turnInPlace(100, false);
            turnUsingScript.waitAngle(180);
            turnUsingScript.drive(0, 0);
        }
        byte[] script = turnUsingScript.getBytes();
        dashboard.log("Should stop now");
        playScript(script, false);

    }

    private void drive(int direction) throws ConnectionLostException {
        drive(direction, 0, 0);
    }

    private void drive(int direction, int leftSpeedUp, int rightSpeedUp) throws ConnectionLostException {
        if (direction == FORWARD) {
            driveDirect(leftSpeedUp, rightSpeedUp);
        } else if (direction == LEFT) {
            driveDirect(leftSpeedUp, -100);
        } else if (direction == RIGHT) {
            driveDirect(-100, rightSpeedUp);
        } else {
            throw new RuntimeException("I don't know how to go that way!");
        }
    }

    private void scenario5() throws ConnectionLostException {
        double x = Math.random();
        if (x <= .5) {
            turnUsingScript(RIGHT);
            driveDistance(speed, oneBlockDistance);
        } else {
            turnUsingScript(LEFT);
            driveDistance(speed, oneBlockDistance);
        }
    }

    private void scenario6() throws ConnectionLostException {
        double x = Math.random();
        if (x <= .5) {
            turnUsingScript(RIGHT);
            driveDistance(speed, oneBlockDistance);
        } else {
            driveDistance(speed, oneBlockDistance);
        }
    }

    private void scenario7() throws ConnectionLostException {
        double x = Math.random();
        if (x <= .5) {
            turnUsingScript(LEFT);
            driveDistance(speed, oneBlockDistance);
        } else {
            driveDistance(speed, oneBlockDistance);
        }
    }

    private void scenario8() throws ConnectionLostException {
        double x = Math.random();
        if (x <= .33) {
            turnUsingScript(LEFT);
            driveDistance(speed, oneBlockDistance);
        } else if (x <= .67 && x > .33) {
            turnUsingScript(RIGHT);
            driveDistance(speed, oneBlockDistance);
        } else {
            driveDistance(speed, oneBlockDistance);
        }
    }

    private void driveDistanceScript()
    {
        
    }
    
    private void driveDistance(int speed, int distance) throws ConnectionLostException {
        int distanceDriven = 0;
        while (distanceDriven < distance) {
            readSensors(SENSORS_DISTANCE);
            distanceDriven += getDistance();
            drive(FORWARD, speed, speed);
            //try {
            //    driveCorrection();
            //} catch (InterruptedException ex) {
            //    Logger.getLogger(Commander.class.getName()).log(Level.SEVERE, null, ex);
            //}
        }

    }

    private void driveUntilBump() throws ConnectionLostException {

        while (!isBumping()) {
            driveDirect(STANDARD_SPEED, STANDARD_SPEED);
            logDistance();
        }
        stop();
        done = true;
    }

    private boolean isBumping() throws ConnectionLostException {
        readSensors(SENSORS_BUMPS_AND_WHEEL_DROPS);
        return isBumpLeft() || isBumpRight();
    }

    private void logDistance() throws ConnectionLostException {
        readSensors(SENSORS_DISTANCE);
        distance += getDistance();
        dashboard.log("Distance" + distance);
        readPrintUltraSonic();
    }

    private boolean isProgramDone() {
        return done;
    }

    private void driveCorrection() throws ConnectionLostException, InterruptedException {
        sonar.readUltrasonicSensors();
        int leftSonarValue = sonar.getLeftDistance();
        int rightSonarValue = sonar.getRightDistance();
        dashboard.log("left sonar value " + leftSonarValue);
        dashboard.log("right sonar value " + rightSonarValue);
        dashboard.log("---------------------------------------");

        if (isLeftWallThere(leftSonarValue) && isRightWallThere(rightSonarValue)) {

            dashboard.log("Center the robot using both walls.");
            dashboard.log("------------------------------------");

            if (leftSonarValue > rightSonarValue) {

                dashboard.log("Speed up the right wheel.");
                drive(FORWARD, INCREASED_SPEED, STANDARD_SPEED);

            } else if (leftSonarValue < rightSonarValue) {

                dashboard.log("Speed up the left wheel.");
                drive(FORWARD, STANDARD_SPEED, INCREASED_SPEED);

            } else {

                dashboard.log("The robot is centered.  Nothing to do.");
            }

        } else if (isLeftWallThere(leftSonarValue)) {

            dashboard.log("Center the robot using left wall.");
            dashboard.log("------------------------------------");


            if (leftSonarValue > CORRECTION_EQUILIBRIUM) {

                dashboard.log("Speed up the right wheel.");
                drive(FORWARD, INCREASED_SPEED, STANDARD_SPEED);

            } else {

                dashboard.log("Speed up the left wheel.");
                drive(FORWARD, STANDARD_SPEED, INCREASED_SPEED);
            }

        } else if (isRightWallThere(rightSonarValue)) {

            dashboard.log("Center the robot using the right wall.");
            dashboard.log("------------------------------------");

            if (rightSonarValue > CORRECTION_EQUILIBRIUM) {

                dashboard.log("Speed up the left wheel.");
                drive(FORWARD, STANDARD_SPEED, INCREASED_SPEED);

            } else {

                dashboard.log("Speed up the right wheel.");
                drive(FORWARD, INCREASED_SPEED, STANDARD_SPEED);
            }
        } else {

            dashboard.log("No data to center the robot.");
            dashboard.log("------------------------------------");
        }
        SystemClock.sleep(1000);
    }

    private boolean isRightWallThere(int sonarValue) {
        return sonarValue < RIGHT_WALL_PRESENT_SONAR_VALUE;
    }

    private boolean isFrontWallThere(int sonarValue) {
        return sonarValue < FRONT_WALL_PRESENT_SONAR_VALUE;
    }

    private boolean isLeftWallThere(int sonarValue) {
        return sonarValue < LEFT_WALL_PRESENT_SONAR_VALUE;
    }
}
