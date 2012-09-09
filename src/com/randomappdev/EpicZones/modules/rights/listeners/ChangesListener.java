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
import com.randomappdev.EpicZones.modules.rights.rightsManager;
import com.randomappdev.EpicZones.utilities.Globals;
import com.randomappdev.EpicZones.utilities.Log;
import com.randomappdev.EpicZones.utilities.Messaging.Message_ID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ChangesListener implements Listener
{

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        try
        {
            if (!event.isCancelled())
            {
                Player player = event.getPlayer();
                EpicZone zone = coreManager.getZoneForLocation(event.getBlock().getLocation());
                if (!rightsManager.hasRights(player, zone, "destroy"))
                {
                    Globals.WarnPlayer(player, Message_ID.Warning_00032_Perm_DestroyInZone);
                    event.setCancelled(true);
                }
            }
        } catch (Exception e)
        {
            Log.write(" RIGHTS MODULE " + e.getMessage());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        try
        {
            if (!event.isCancelled())
            {
                Player player = event.getPlayer();
                EpicZone zone = coreManager.getZoneForLocation(event.getBlock().getLocation());
                if (!rightsManager.hasRights(player, zone, "build"))
                {
                    Globals.WarnPlayer(player, Message_ID.Warning_00034_Perm_BuildInZone);
                    event.setCancelled(true);
                }
            }
        } catch (Exception e)
        {
            Log.write(" RIGHTS MODULE " + e.getMessage());
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
                EpicZone zone = coreManager.getZoneForLocation(event.getBlockClicked().getLocation());
                if (!rightsManager.hasRights(player, zone, "build"))
                {
                    Globals.WarnPlayer(player, Message_ID.Warning_00036_Perm_GenericInZone);
                    event.setCancelled(true);
                }
            }
        } catch (Exception e)
        {
            Log.write(" RIGHTS MODULE " + e.getMessage());
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
                EpicZone zone = coreManager.getZoneForLocation(event.getBlockClicked().getLocation());
                if (!rightsManager.hasRights(player, zone, "destroy"))
                {
                    Globals.WarnPlayer(player, Message_ID.Warning_00036_Perm_GenericInZone);
                    event.setCancelled(true);
                }
            }
        } catch (Exception e)
        {
            Log.write(" RIGHTS MODULE " + e.getMessage());
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
                        EpicZone zone = coreManager.getZoneForLocation(event.getPainting().getLocation());
                        if (!rightsManager.hasRights(player, zone, "destroy"))
                        {
                            Globals.WarnPlayer(player, Message_ID.Warning_00036_Perm_GenericInZone);
                            event.setCancelled(true);
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            Log.write(" RIGHTS MODULE " + e.getMessage());
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
                        if (Globals.interactiveItems.contains((event.getPlayer().getItemInHand().getTypeId())))
                        {
                            Player player = event.getPlayer();
                            EpicZone zone = coreManager.getZoneForLocation(event.getClickedBlock().getLocation());
                            if (!rightsManager.hasRights(player, zone, "build"))
                            {
                                Globals.WarnPlayer(player, Message_ID.Warning_00036_Perm_GenericInZone);
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            Log.write(" RIGHTS MODULE " + e.getMessage());
        }
    }


}
