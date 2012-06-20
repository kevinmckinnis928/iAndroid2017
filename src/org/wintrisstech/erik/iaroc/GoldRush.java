package org.wintrisstech.erik.iaroc;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import org.wintrisstech.irobot.ioio.IRobotCreateAdapter;
import org.wintrisstech.irobot.ioio.IRobotCreateInterface;
import org.wintrisstech.sensors.UltraSonicSensors;

/**
 *
 * @author vicwintriss
 */
public class GoldRush extends IRobotCreateAdapter {

    private static final String TAG = "Lada";
    private final Dashboard dashboard;
    public UltraSonicSensors sonar;
    public IOIO ioio;

    public GoldRush(IOIO ioio, IRobotCreateInterface create, Dashboard dashboard) throws ConnectionLostException {
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
        driveDirect(100, 100);
        while (true) {
            //use bump sesors
            //beacon sensor
            return;
        }
    }
}