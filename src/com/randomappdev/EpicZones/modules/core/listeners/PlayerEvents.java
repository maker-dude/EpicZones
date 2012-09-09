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

package com.randomappdev.EpicZones.modules.core.listeners;

import com.randomappdev.EpicZones.modules.core.coreManager;
import com.randomappdev.EpicZones.modules.core.objects.EpicZone;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer.EpicZoneMode;
import com.randomappdev.EpicZones.utilities.Globals;
import com.randomappdev.EpicZones.utilities.Log;
import com.randomappdev.EpicZones.utilities.Messaging;
import com.randomappdev.EpicZones.utilities.Messaging.Message_ID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.awt.*;

public class PlayerEvents implements Listener
{

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        try   //TODO: Verify that this catches teleport as well
        {
            if (!event.isCancelled())
            {
                EpicZone zone = coreManager.getZoneForLocation(event.getTo());
                coreManager.playerMovementLogic(event.getPlayer(), event.getFrom(), zone);
            }
        } catch (Exception e)
        {
            Log.write(" CORE MODULE " + e.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        try
        {
            if (event.getResult() == Result.ALLOWED)
            {
                Globals.addPlayer(event.getPlayer());
            }
        } catch (Exception e)
        {
            Log.write(" CORE MODULE " + e.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        try
        {
            Globals.removePlayer(event.getPlayer().getName());
        } catch (Exception e)
        {
            Log.write(" CORE MODULE " + e.getMessage());
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
                        if (Globals.getPlayer(event.getPlayer().getName()).getMode() == EpicZoneMode.ZoneDraw)
                        {
                            Point point = new Point(event.getClickedBlock().getLocation().getBlockX(), event.getClickedBlock().getLocation().getBlockZ());
                            EpicZonePlayer ezp = Globals.getPlayer(event.getPlayer().getName());
                            ezp.getEditZone().addPoint(point);
                            ezp.getEditZone().addPillar(event.getClickedBlock());
                            ezp.getEditZone().ShowPillar(point);
                            Messaging.Send(event.getPlayer(), Message_ID.Info_00112_Point_XZ_Added, new String[]{Integer.toString(point.x), Integer.toString(point.y)});
                            event.setCancelled(true);
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            Log.write(" CORE MODULE " + e.getMessage());
        }
    }

}