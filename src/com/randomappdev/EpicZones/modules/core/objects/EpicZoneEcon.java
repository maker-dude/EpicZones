package com.randomappdev.EpicZones.modules.core.objects;

public class EpicZoneEcon
{
    private boolean forSale = false;
    private int purchasePrice = 0;
    private String seller = new String();
    private String signLocation = new String();

    public EpicZoneEcon()
    {
        this.forSale = false;
        this.purchasePrice = 0;
        this.seller = "";
        this.signLocation = "";
    }

    public EpicZoneEcon(String value)
    {

        this.forSale = false;
        this.purchasePrice = 0;
        this.seller = "";
        this.signLocation = "";

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
                this.forSale = Boolean.valueOf(split[0]);
            }
            if (split.length > 1)
            {
                this.purchasePrice = new Double(split[1]).intValue();
            }
            if (split.length > 2)
            {
                this.seller = split[2].trim();
            }
            if (split.length > 3)
            {
                this.signLocation = split[3];
            }
        }
    }

    public void setForSale(boolean value)
    {
        this.forSale = value;
    }

    public void setPurchasePrice(int value)
    {
        this.purchasePrice = value;
    }

    public void setSeller(String value)
    {
        this.seller = value;
    }

    public void setSignLocation(String value)
    {
        this.signLocation = value;
    }

    public boolean getForSale()
    {
        return this.forSale;
    }

    public Double getPurchasePrice()
    {
        return new Double(this.purchasePrice);
    }

    public String getSeller()
    {
        return this.seller;
    }

    public String getSignLocation()
    {
        return this.signLocation;
    }

}
