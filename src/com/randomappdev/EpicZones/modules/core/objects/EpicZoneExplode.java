package com.randomappdev.EpicZones.modules.core.objects;

public class EpicZoneExplode
{
    private boolean tnt = false;
    private boolean creeper = false;
    private boolean ghast = false;
    private boolean other = false;

    public void setTNT(boolean value)
    {
        this.tnt = value;
    }

    public boolean getTNT()
    {
        return this.tnt;
    }

    public void setCreeper(boolean value)
    {
        this.creeper = value;
    }

    public boolean getCreeper()
    {
        return this.creeper;
    }

    public boolean getOther()
    {
        return this.other;
    }

    public void setGhast(boolean value)
    {
        this.ghast = value;
    }

    public boolean getGhast()
    {
        return this.ghast;
    }

    public void setOther(boolean value)
    {
        this.other = value;
    }

    public EpicZoneExplode()
    {
        tnt = false;
        creeper = false;
        ghast = false;
        other = false;
    }

    public EpicZoneExplode(String value)
    {

        String[] split = null;
        if (value.contains(":"))
        {
            split = value.split(":");
        } else if (value.contains(" "))
        {
            split = value.split(" ");
        }

        if (split != null)
        {
            if (split.length > 0)
            {
                this.tnt = Boolean.valueOf(split[0]);
            }
            if (split.length > 1)
            {
                this.creeper = Boolean.valueOf(split[1]);
            }
            if (split.length > 2)
            {
                this.ghast = Boolean.valueOf(split[2]);
            }
            if (split.length > 3)
            {
                this.other = Boolean.valueOf(split[3]);
            }
        }
    }

    public String toString()
    {
        return String.valueOf(this.tnt) + ":"
                + String.valueOf(this.creeper) + ":"
                + String.valueOf(this.ghast) + ":"
                + String.valueOf(this.other);
    }
}
