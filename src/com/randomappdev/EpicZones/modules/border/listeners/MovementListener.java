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

package com.randomappdev.EpicZones.modules.border.listeners;

import com.randomappdev.EpicZones.modules.border.borderManager;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer;
import com.randomappdev.EpicZones.utilities.Globals;
import com.randomappdev.EpicZones.utilities.Log;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.Set;

public class MovementListener implements Listener
{
    private Set<Integer> interactiveItems = new HashSet<Integer>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        try                 //TODO: Verify that this catches the teleports as well.
        {
            if (!event.isCancelled())
            {
                EpicZonePlayer ezp = Globals.getPlayer(event.getPlayer().getName().toLowerCase());
                if (borderManager.borderLogic(event.getTo(), event.getPlayer()))
                {
                    if (ezp.getPastBorder())
                    {
                        Globals.WarnPlayer(event.getPlayer(), ezp, "You are outside the map border.");    //TODO: MessageID Needed
                    } else
                    {
                        Globals.WarnPlayer(event.getPlayer(), ezp, "You are inside the map border.");     //TODO: MessageID Needed
                    }
                } else
                {
                    Globals.WarnPlayer(event.getPlayer(), ezp, "You can not go past the map border.");   //TODO: MessageID Needed
                    event.setCancelled(true);
                }
            }
        } catch (Exception e)
        {
            Log.write(" BORDER MODULE " + e.getMessage());
        }
    }

//    @EventHandler
//    public void onPlayerTeleport(PlayerTeleportEvent event)
//    {
//        try
//        {
//            if (!event.isCancelled())
//            {
//                EpicZonePlayer ezp = Globals.getPlayer(event.getPlayer().getName().toLowerCase());
//                if (borderManager.borderLogic(event.getTo(), event.getPlayer()))
//                {
//                    if (ezp.getPastBorder())
//                    {
//                        Globals.WarnPlayer(event.getPlayer(), ezp, "You are outside the map border.");    //TODO: MessageID Needed
//                    } else
//                    {
//                        Globals.WarnPlayer(event.getPlayer(), ezp, "You are inside the map border.");     //TODO: MessageID Needed
//                    }
//                } else
//                {
//                    Globals.WarnPlayer(event.getPlayer(), ezp, "You can not go past the map border.");   //TODO: MessageID Needed
//                    event.setCancelled(true);
//                }
//            }
//        } catch (Exception e)
//        {
//            Log.write(" BORDER MODULE " + e.getMessage());
//        }
//    }

}