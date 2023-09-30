package com.pl.edu.prz.robotui.control.panel;

public class Coords {
    private float posX = 0;
    private float posY = 0;
    private float posZ = 0;

    public float getPosX()
    {
        return posX;
    }
    public float getPosY()
    {
        return posY;
    }
    public float getPosZ()
    {
        return posZ;
    }

    public void increaseX(float value)
    {
        posX = posX + value;
    }
    public void increaseY(float value) { posY = posY + value; }
    public void increaseZ(float value) { posZ = posZ + value; }

    public void setPosX(float value) { posX = value; }
    public void setPosY(float value) { posY = value; }
    public void setPosZ(float value) { posZ = value; }

}
