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
import com.randomappdev.EpicZones.integration.EpicSpout;
import com.randomappdev.EpicZones.integration.PermissionsManager;
import com.randomappdev.EpicZones.objects.EpicZone;
import com.randomappdev.EpicZones.objects.EpicZonePlayer;
import com.randomappdev.EpicZones.objects.EpicZonePlayer.EpicZoneMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import java.awt.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class PlayerEvents implements Listener
{
    private Set<Integer> interactiveItems = new HashSet<Integer>();

    public PlayerEvents()
    {
        interactiveItems.add(324); // Wood Door
        interactiveItems.add(330); // Iron Door
        interactiveItems.add(323); // Sign
        interactiveItems.add(321); // Painting
        interactiveItems.add(354); // Bed
        interactiveItems.add(355); // Cake
        interactiveItems.add(356); // Redstone Repeater
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        try
        {
            if (!event.isCancelled())
            {
                if (!General.PlayerMovementLogic(event.getPlayer(), event.getFrom(), event.getTo()))
                {
                    event.getPlayer().teleport(General.getPlayer(event.getPlayer().getName()).getCurrentLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
                if (General.SpoutEnabled)
                {
                    EpicSpout.UpdatePlayerXYZ(event.getPlayer());
                }
            }
        } catch (Exception e)
        {
            Log.Write(e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        try
        {
            if (!event.isCancelled())
            {
                if (!General.PlayerMovementLogic(event.getPlayer(), event.getFrom(), event.getTo()))
                {
                    event.setTo(General.getPlayer(event.getPlayer().getName()).getCurrentLocation());
                    event.setCancelled(true);
                }
            }
        } catch (Exception e)
        {
            Log.Write(e.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        try
        {
            if (event.getResult() == Result.ALLOWED)
            {
                General.addPlayer(event.getPlayer());
            }
        } catch (Exception e)
        {
            Log.Write(e.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        try
        {
            General.removePlayer(event.getPlayer().getName());
        } catch (Exception e)
        {
            Log.Write(e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
    {
        try
        {
            if (!event.isCancelled())
            {
                Player player = event.getPlayer();
                EpicZonePlayer ezp = General.getPlayer(player.getName());
                Point blockPoint = new Point(event.getBlockClicked().getLocation().getBlockX(), event.getBlockClicked().getLocation().getBlockZ());
                String worldName = player.getWorld().getName();
                int blockHeight = event.getBlockClicked().getLocation().getBlockY();
                boolean hasPerms;
                EpicZone currentZone;
                if (General.BorderLogic(blockPoint, player))
                {
                    currentZone = General.GetZoneForPlayer(player, worldName, blockHeight, blockPoint);
                    hasPerms = ZonePermissionsHandler.hasPermissions(player, currentZone, "build");
                    if (!hasPerms)
                    {
                        if (ezp.getLastWarned().before(new Date()))
                        {
                            Message.Send(player, Message_ID.Warning_00036_Perm_GenericInZone);
                            ezp.Warn();
                        }
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
    public void onPlayerBucketFill(PlayerBucketFillEvent event)
    {
        try
        {
            if (!event.isCancelled())
            {
                Player player = event.getPlayer();
                EpicZonePlayer ezp = General.getPlayer(player.getName());
                Point blockPoint = new Point(event.getBlockClicked().getLocation().getBlockX(), event.getBlockClicked().getLocation().getBlockZ());
                String worldName = player.getWorld().getName();
                int blockHeight = event.getBlockClicked().getLocation().getBlockY();
                boolean hasPerms;
                EpicZone currentZone;
                if (General.BorderLogic(blockPoint, player))
                {
                    currentZone = General.GetZoneForPlayer(player, worldName, blockHeight, blockPoint);
                    hasPerms = ZonePermissionsHandler.hasPermissions(player, currentZone, "destroy");
                    if (!hasPerms)
                    {
                        if (ezp.getLastWarned().before(new Date()))
                        {
                            Message.Send(player, Message_ID.Warning_00036_Perm_GenericInZone);
                            ezp.Warn();
                        }
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
    public void onPaintingBreakByEntity(PaintingBreakByEntityEvent event)
    {
        try
        {
            if (!event.isCancelled())
            {
                if (event.getRemover() != null)
                {
                    if (event.getRemover() instanceof Player)
                    {
                        Player player = (Player) event.getRemover();
                        EpicZonePlayer ezp = General.getPlayer(player.getName());
                        Point blockPoint = new Point(event.getPainting().getLocation().getBlockX(), event.getPainting().getLocation().getBlockZ());
                        String worldName = player.getWorld().getName();
                        int blockHeight = event.getPainting().getLocation().getBlockY();
                        boolean hasPerms;

                        EpicZone currentZone;
                        if (General.BorderLogic(blockPoint, player))
                        {
                            currentZone = General.GetZoneForPlayer(player, worldName, blockHeight, blockPoint);
                            hasPerms = ZonePermissionsHandler.hasPermissions(player, currentZone, "destroy");

                            if (!hasPerms)
                            {
                                if (ezp.getLastWarned().before(new Date()))
                                {
                                    Message.Send(player, Message_ID.Warning_00036_Perm_GenericInZone);
                                    ezp.Warn();
                                }
                                event.setCancelled(true);
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
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        try
        {
            if (!event.isCancelled())
            {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
                {
                    if (event.getPlayer() != null)
                    {
                        if (interactiveItems.contains((event.getPlayer().getItemInHand().getTypeId())))
                        {

                            Player player = event.getPlayer();
                            EpicZonePlayer ezp = General.getPlayer(player.getName());
                            Point blockPoint = new Point(event.getClickedBlock().getLocation().getBlockX(), event.getClickedBlock().getLocation().getBlockZ());
                            String worldName = player.getWorld().getName();
                            int blockHeight = event.getClickedBlock().getLocation().getBlockY();
                            boolean hasPerms;

                            EpicZone currentZone;
                            if (General.BorderLogic(blockPoint, player))
                            {
                                currentZone = General.GetZoneForPlayer(player, worldName, blockHeight, blockPoint);
                                hasPerms = ZonePermissionsHandler.hasPermissions(player, currentZone, "build");

                                if (!hasPerms)
                                {
                                    if (ezp.getLastWarned().before(new Date()))
                                    {
                                        Message.Send(player, Message_ID.Warning_00036_Perm_GenericInZone);
                                        ezp.Warn();
                                    }
                                    event.setCancelled(true);
                                }
                            }
                        } else if (event.getPlayer().getItemInHand().getTypeId() == Config.zoneTool)
                        {
                            if (General.getPlayer(event.getPlayer().getName()).getMode() == EpicZoneMode.ZoneDraw)
                            {
                                Point point = new Point(event.getClickedBlock().getLocation().getBlockX(), event.getClickedBlock().getLocation().getBlockZ());
                                EpicZonePlayer ezp = General.getPlayer(event.getPlayer().getName());
                                ezp.getEditZone().addPoint(point);
                                ezp.getEditZone().addPillar(event.getClickedBlock());
                                ezp.getEditZone().ShowPillar(point);
                                Message.Send(event.getPlayer(), Message_ID.Info_00112_Point_XZ_Added, new String[]{Integer.toString(point.x), Integer.toString(point.y)});
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        try
        {
            if (!event.isCancelled())
            {
                Player player = event.getPlayer();
                if (!PermissionsManager.hasPermission(player, "epiczones.ignorepermissions"))
                {
                    EpicZone zone = General.GetZoneForPlayer(player, player.getWorld().getName(), player.getLocation().getBlockY(), new Point(player.getLocation().getBlockX(), player.getLocation().getBlockZ()));
                    if (zone != null)
                    {
                        String command = event.getMessage().toLowerCase().trim().replace("/", "");
                        if (command.contains(" "))
                        {
                            command = command.substring(0, command.indexOf(" ")).trim();
                        }
                        if (zone.getDisallowedCommands().contains(command))
                        {
                            event.setCancelled(true);
                            Message.Send(event.getPlayer(), Message_ID.Info_00133_CommandDenied, new String[]{command, zone.getName()});
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            Log.Write(e.getMessage());
        }
    }
}