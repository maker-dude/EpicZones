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

package com.randomappdev.EpicZones.modules.rights.listeners;

import com.randomappdev.EpicZones.modules.core.coreManager;
import com.randomappdev.EpicZones.modules.core.objects.EpicZone;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer;
import com.randomappdev.EpicZones.modules.rights.rightsManager;
import com.randomappdev.EpicZones.utilities.Globals;
import com.randomappdev.EpicZones.utilities.Log;
import com.randomappdev.EpicZones.utilities.Messaging;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

public class MovementListener implements Listener
{

    private final Vector zero = new Vector(0, 0, 0);

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        try    //TODO: See if this catches teleport logic as well
        {
            if (!event.isCancelled())
            {
                Player player = event.getPlayer();
                EpicZone zone = coreManager.getZoneForPlayer(player, event.getTo());
                if (!rightsManager.movementLogic(event.getPlayer(), event.getFrom(), zone))
                {
                    EpicZonePlayer ezp = Globals.getPlayer(player.getName().toLowerCase());
                    Globals.WarnPlayer(player, ezp, Messaging.get(Messaging.Message_ID.Info_00130_NoPermToEnter, zone.getName()));

                    ezp.setIsTeleporting(true);
                    event.getPlayer().teleport(ezp.getCurrentLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    ezp.setIsTeleporting(false);
                }
            }
        } catch (Exception e)
        {
            Log.write(" RIGHTS MODULE " + e.getMessage());
        }
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event)
    {
        try
        {
            Vehicle vehicle = event.getVehicle();
            Entity passenger = vehicle.getPassenger();
            if (passenger != null)
            {
                if (passenger instanceof Player)
                {
                    Player player = (Player) passenger;
                    EpicZone zone = coreManager.getZoneForPlayer(player, event.getTo());
                    if (!rightsManager.movementLogic(player, event.getFrom(), zone))
                    {
                        Location loc = Globals.getPlayer(player.getName()).getCurrentLocation();
                        loc.setY(loc.getY() + 1);
                        vehicle.teleport(loc);
                        vehicle.setVelocity(zero);
                    }
                }
            }
        } catch (Exception e)
        {
            Log.write(" RIGHTS MODULE " + e.getMessage());
        }
    }

//    @EventHandler
//    public void onPlayerTeleport(PlayerTeleportEvent event)
//    {
//        try
//        {
//            if (!event.isCancelled())
//            {
//                Player player = event.getPlayer();
//                EpicZone zone = coreManager.getZoneForPlayer(player, event.getTo());
//                if (!rightsManager.movementLogic(event.getPlayer(), event.getFrom(), zone))
//                {
//                    EpicZonePlayer ezp = Globals.getPlayer(player.getName().toLowerCase());
//                    Globals.WarnPlayer(player, ezp, Messaging.get(Messaging.Message_ID.Info_00130_NoPermToEnter, zone.getName()));
//
//                    ezp.setIsTeleporting(true);
//                    event.getPlayer().teleport(ezp.getCurrentLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
//                    ezp.setIsTeleporting(false);
//                }
//            }
//        } catch (Exception e)
//        {
//            Log.write(e.getMessage());
//        }
//    }


}