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

package com.randomappdev.EpicZones.modules.protection.listeners;

import com.randomappdev.EpicZones.modules.core.permissionsManager;
import com.randomappdev.EpicZones.modules.core.coreManager;
import com.randomappdev.EpicZones.modules.core.objects.EpicZone;
import com.randomappdev.EpicZones.utilities.Log;
import com.randomappdev.EpicZones.utilities.Messaging;
import com.randomappdev.EpicZones.utilities.Messaging.Message_ID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerEvents implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        try
        {
            if (!event.isCancelled())
            {
                Player player = event.getPlayer();
                if (!permissionsManager.hasPermission(player, "epiczones.ignorepermissions"))
                {
                    EpicZone zone = coreManager.getZoneForLocation(event.getPlayer().getLocation());
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
                            Messaging.Send(event.getPlayer(), Message_ID.Info_00133_CommandDenied, new String[]{command, zone.getName()});
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            Log.write(e.getMessage());
        }
    }
}