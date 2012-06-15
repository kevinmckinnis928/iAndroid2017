/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wintrisstech.erik.iaroc;

/**
 *
 * @author barney
 */
public class BeaconState
{
    private boolean redBuoy = false;
    private boolean greenBuoy = false;
    private boolean forcefield = false;

    public boolean isForcefield()
    {
        return forcefield;
    }

    public void setForcefield(boolean forcefield)
    {
        this.forcefield = forcefield;
    }

    public boolean isGreenBuoy()
    {
        return greenBuoy;
    }

    public void setGreenBuoy(boolean greenBuoy)
    {
        this.greenBuoy = greenBuoy;
    }

    public boolean isRedBuoy()
    {
        return redBuoy;
    }

    public void setRedBuoy(boolean redBuoy)
    {
        this.redBuoy = redBuoy;
    }
    
    
}
