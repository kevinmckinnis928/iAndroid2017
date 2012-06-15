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
    public BeaconCode getBeaconCode(int infraredByte)
    {
            if(infraredByte == 255)
            {
                return BeaconCode.NO_BEACON_DETECTED;
            }
            else if(infraredByte == 248)
            {
                return BeaconCode.RED_BUOY;
            }
            else if(infraredByte == 244)
            {
                return BeaconCode.GREEN_BUOY;
            }
            else if(infraredByte == 242)
            {
                return BeaconCode.FORCEFIELD;
            }
            else if(infraredByte == 252)
            {
                return BeaconCode.RED_AND_GREEN_BUOY;
            }
            else if(infraredByte == 250)
            {
                return BeaconCode.RED_BUOY_AND_FORCEFIELD;
            }
            else if(infraredByte == 248)
            {
                return BeaconCode.GREEN_BUOY_AND_FORCEFIELD;
            }
            else if(infraredByte == 254)
            {
                return BeaconCode.RED_AND_GREEN_BUOY_AND_FORCEFIELD;
            }
            else throw new IllegalArgumentException("Unknown infrared byte");
    }
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
