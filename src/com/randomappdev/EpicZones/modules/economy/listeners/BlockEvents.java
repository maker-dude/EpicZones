/*

This file is part of EpicZones

Copyright (C) 2011 by Team ESO

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

 */

/**
 * @author jblaske@gmail.com
 * @license MIT License
 */

package com.randomappdev.EpicZones.modules.economy.listeners;

import com.randomappdev.EpicZones.modules.core.coreManager;
import com.randomappdev.EpicZones.modules.core.objects.EpicZone;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer;
import com.randomappdev.EpicZones.utilities.Globals;
import com.randomappdev.EpicZones.utilities.Log;
import com.randomappdev.EpicZones.utilities.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

public class BlockEvents implements Listener
{

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        try
        {
            if (!event.isCancelled())
            {
                if (event.getBlock().getType() == Material.SIGN_POST)
                {
                    EpicZone currentZone = coreManager.getZoneForLocation(event.getBlock().getLocation());
                    if (currentZone.getEcon().getForSale())
                    {
                        if (Util.getStringFromLocation(event.getBlock().getLocation()).equalsIgnoreCase(currentZone.getEcon().getSignLocation()))
                        {
                            Globals.plugin.getServer().getPlayer(currentZone.getEcon().getSeller()).sendMessage(currentZone.getName() + " is no longer for sale. You are now the owner of that zone.");
                            currentZone.getOwners().add(currentZone.getEcon().getSeller());
                            currentZone.setEcon("");
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            Log.write(" ECONOMY MODULE " + e.getMessage());
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event)
    {
        if (!event.isCancelled())
        {
            String line1 = event.getLines()[0];
            if (line1.equalsIgnoreCase("[ezsell]"))
            {
                EpicZone ez = coreManager.getZoneForLocation(event.getBlock().getLocation());
                Player player = event.getPlayer();
                EpicZonePlayer ezp = Globals.getPlayer(player.getName());
                if (ez != null)
                {
                    Log.write(ez.getName());
                    if (ez.getType() != EpicZone.ZoneType.GLOBAL)
                    {
                        if (ezp.getAdmin() || ez.isOwner(ezp.getName()))
                        {

                            String line2 = event.getLines()[1];
                            String seller = event.getLines()[2];
                            Integer purchasePrice = 0;

                            if (line2.length() > 0)
                            {

                                if (seller.trim().length() == 0)
                                {
                                    seller = player.getName();
                                }

                                purchasePrice = new Integer(line2);

                                ez.getEcon().setForSale(true);
                                ez.getEcon().setPurchasePrice(purchasePrice);
                                ez.getEcon().setSeller(seller);
                                ez.getEcon().setSignLocation(Util.getStringFromLocation(event.getBlock().getLocation()));

                                ez.getOwners().clear();
                                ez.getPermissions().clear();

                                event.setLine(0, "For Sale by");
                                event.setLine(1, seller);
                                event.setLine(2, "Price:");
                                event.setLine(3, line2);

                                player.sendMessage(ez.getName() + " is now up for sale!");

                                Globals.SaveZones();

                            } else
                            {
                                player.sendMessage("You must specify a price on line 2 to sell this Zone.");
                            }
                        }
                    }
                }
            }
        }
    }
}
