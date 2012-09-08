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

package com.randomappdev.EpicZones.listeners;

import com.randomappdev.EpicZones.*;
import com.randomappdev.EpicZones.Message.Message_ID;
import com.randomappdev.EpicZones.objects.EpicZone;
import com.randomappdev.EpicZones.objects.EpicZonePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;

import java.awt.*;
import java.util.Date;

public class BlockEvents implements Listener
{

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event)
    {
        try
        {
            //Log.Write("Block Ignite Event!");
            if (!event.isCancelled())
            {
                EpicZone zone = General.GetZoneForPlayer(null, event.getBlock().getLocation().getWorld().getName(), event.getBlock().getLocation().getBlockY(), new Point(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockZ()));
                if (zone != null)
                {
                    if (event.getCause() == IgniteCause.FLINT_AND_STEEL) //Flint and steel
                    {
                        if (!zone.getFire().getIgnite())
                        {
                            event.setCancelled(true);
                        }
                    } else //Spread and Lava
                    {
                        if (!zone.getFire().getSpread())
                        {
                            event.setCancelled(true);
                        }
                    }

                }
            }
        } catch (Exception e)
        {
            Log.Write(e.getMessage());
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event)
    {
        try
        {
            //Log.Write("Block Burn Event!");
            if (!event.isCancelled())
            {
                EpicZone zone = General.GetZoneForPlayer(null, event.getBlock().getLocation().getWorld().getName(), event.getBlock().getLocation().getBlockY(), new Point(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockZ()));
                if (zone != null)
                {
                    if (!zone.getFire().getIgnite())
                    {
                        event.setCancelled(true);
                    }
                }
            }
        } catch (Exception e)
        {
            Log.Write(e.getMessage());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        try
        {
            //Log.Write("Block Break Event!");
            if (!event.isCancelled())
            {
                Player player = event.getPlayer();
                EpicZonePlayer ezp = General.getPlayer(player.getName());
                Point blockPoint = new Point(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockZ());
                if (General.BorderLogic(blockPoint, player))
                {
                    String worldName = player.getWorld().getName();
                    int blockHeight = event.getBlock().getLocation().getBlockY();
                    EpicZone currentZone = General.GetZoneForPlayer(player, worldName, blockHeight, blockPoint);
                    boolean hasPerms = ZonePermissionsHandler.hasPermissions(player, currentZone, "destroy");
                    if (!hasPerms)
                    {
                        if (ezp.getLastWarned().before(new Date()))
                        {
                            Message.Send(player, Message_ID.Warning_00032_Perm_DestroyInZone);
                            ezp.Warn();
                        }
                        event.setCancelled(true);
                    }
                } else
                {
                    if (ezp.getLastWarned().before(new Date()))
                    {
                        Message.Send(player, Message_ID.Warning_00033_Perm_DestroyOutsideRadius);
                        ezp.Warn();
                    }
                    event.setCancelled(true);
                }

                if (!event.isCancelled())
                {
                    if (event.getBlock().getType() == Material.SIGN_POST)
                    {
                        EpicZone currentZone = General.GetZoneForLocation(event.getBlock().getLocation());
                        if (currentZone.getEcon().getForSale())
                        {
                            if (Util.getStringFromLocation(event.getBlock().getLocation()).equalsIgnoreCase(currentZone.getEcon().getSignLocation()))
                            {
                                General.plugin.getServer().getPlayer(currentZone.getEcon().getSeller()).sendMessage(currentZone.getName() + " is no longer for sale. You are now the owner of that zone.");
                                currentZone.getOwners().add(currentZone.getEcon().getSeller());
                                currentZone.setEcon("");
                            }
                        }
                    }
                }

            }
        } catch (Exception e)
        {
            Log.Write(e.getMessage());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        try
        {
            //Log.Write("Block Place Event!");
            if (!event.isCancelled())
            {
                Player player = event.getPlayer();
                EpicZonePlayer ezp = General.getPlayer(player.getName());
                Point blockPoint = new Point(event.getBlock().getLocation().getBlockX(), event.getBlock().getLocation().getBlockZ());
                if (General.BorderLogic(blockPoint, player))
                {
                    String worldName = player.getWorld().getName();
                    int blockHeight = event.getBlock().getLocation().getBlockY();
                    EpicZone currentZone = General.GetZoneForPlayer(player, worldName, blockHeight, blockPoint);
                    boolean hasPerms = ZonePermissionsHandler.hasPermissions(player, currentZone, "build");
                    if (!hasPerms)
                    {
                        if (ezp.getLastWarned().before(new Date()))
                        {
                            Message.Send(player, Message_ID.Warning_00034_Perm_BuildInZone);
                            ezp.Warn();
                        }
                        event.setCancelled(true);
                    }
                } else
                {
                    if (ezp.getLastWarned().before(new Date()))
                    {
                        Message.Send(player, Message_ID.Warning_00035_Perm_BuildOutsideRadius);
                        ezp.Warn();
                    }
                    event.setCancelled(true);
                }
            }
        } catch (Exception e)
        {
            Log.Write(e.getMessage());
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
                EpicZone ez = General.GetZoneForLocation(event.getBlock().getLocation());
                Player player = event.getPlayer();
                EpicZonePlayer ezp = General.getPlayer(player.getName());
                if (ez != null)
                {
                    Log.Write(ez.getName());
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

                                General.SaveZones();

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
