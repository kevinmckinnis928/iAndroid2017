/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wintrisstech.erik.iaroc;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import org.wintrisstech.irobot.ioio.IRobotCreateAdapter;
import org.wintrisstech.irobot.ioio.IRobotCreateInterface;

/**
 *
 * @author kevinmckinnis
 */
public class MazeRun extends IRobotCreateAdapter {
     private final Dashboard dashboard;
     private int speed = 200;
     
    public MazeRun(IOIO ioio, IRobotCreateInterface create, Dashboard dashboard) throws ConnectionLostException
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
        dashboard.log("Start");
        readSensors(SENSORS_GROUP_ID6);//Resets all counters in the Create to 0.
    }
    
    public void driveUntilBumped(int speed) throws ConnectionLostException
    {
        dashboard.log("inside driveUntilBumped");
        while(isBumpLeft() == false && isBumpRight() == false)
        {    
            readSensors(SENSORS_GROUP_ID6);
            
            driveDirect(speed, speed);
        }
        stop();
        dashboard.log("stopping now");
    }
    
    public void loop() throws ConnectionLostException
    {
        driveUntilBumped(speed);
    }
    
    public void stop() throws ConnectionLostException
    {
        driveDirect(0, 0); // stop the Create
    }
}
