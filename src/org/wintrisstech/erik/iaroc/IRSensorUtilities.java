/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wintrisstech.erik.iaroc;

/**
 *
 * @author barney
 */
public class IRSensorUtilities
{
    public BeaconState getBeaconState(int infraredByte)
    {
        BeaconState beaconstate = new BeaconState();
        int redBuoyMask = 0x8;
        int redBuoyResult = infraredByte & redBuoyMask;
        int greenBuoyMask = 0x4;
        int greenBuoyResult = infraredByte & greenBuoyMask; 
        int forcefieldMask = 0x2;
        int forcefieldResult = infraredByte & forcefieldMask; 
        if(redBuoyResult == 0x8)
        {
            beaconstate.setRedBuoy(true);
        }
        if(greenBuoyResult == 0x4)
        {
            beaconstate.setGreenBuoy(true);
        }
        if(forcefieldResult == 0x2)
        {
            beaconstate.setForcefield(true);
        }
        return beaconstate;
    }
}
