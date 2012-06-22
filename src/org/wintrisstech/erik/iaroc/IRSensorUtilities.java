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
   
    public BeaconState getBeaconState(byte infraredByte)
    {
        BeaconState beaconstate = new BeaconState();
        byte redBuoyMask = 0x8;
        byte redBuoyResult = (byte) (infraredByte & redBuoyMask);
        byte greenBuoyMask = 0x4;
        byte greenBuoyResult = (byte) (infraredByte & greenBuoyMask); 
        byte forcefieldMask = 0x2;
        byte forcefieldResult = (byte) (infraredByte & forcefieldMask); 
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
