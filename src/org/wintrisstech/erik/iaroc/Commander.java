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
            sonar.readUltrasonicSensors();
            dashboard.log("Left Sonar" + sonar.getLeftDistance());// reads the distance from left wall
            dashboard.log("Front Sonar" + sonar.getFrontDistance());// reads the distance from the front wall
            dashboard.log("Right Sonar" + sonar.getRightDistance());// reads the distance from the right wall
            dashboard.log("--------------------------------------");
            //scenario 1
            if (sonar.getLeftDistance() <= 950 && sonar.getRightDistance() <= 900 && sonar.getFrontDistance() >= 850) {
                driveDirect(100, 100);
                getDistance();
            }

            SystemClock.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Commander.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stop() throws ConnectionLostException {

        dashboard.log("===========Stop===========");
        driveDirect(0, 0); // stop the Create
    }
}
