package org.wintrisstech.erik.iaroc;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import org.wintrisstech.irobot.ioio.IRobotCreateAdapter;
import org.wintrisstech.irobot.ioio.IRobotCreateInterface;
import org.wintrisstech.sensors.UltraSonicSensors;

/**
 *
 * @author Barnett && Kevin
 */
public class DragRace extends IRobotCreateAdapter {

    private static final String TAG = "Lada";
    private final Dashboard dashboard;
    public UltraSonicSensors sonar;
    public IOIO ioio;

    public DragRace(IOIO ioio, IRobotCreateInterface create, Dashboard dashboard) throws ConnectionLostException {
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
        driveDirect(500, 500);
        while (true) {
            //drive correction 
            return;
        }
    }
}
