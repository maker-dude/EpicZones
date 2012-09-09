package com.randomappdev.EpicZones.modules.spout.listeners;

import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer;
import com.randomappdev.EpicZones.modules.spout.spoutManager;
import com.randomappdev.EpicZones.utilities.Globals;
import com.randomappdev.EpicZones.utilities.Log;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;

public class SpoutEvents implements Listener
{
    @EventHandler
    public void onSpoutCraftEnable(SpoutCraftEnableEvent event)
    {
        try
        {
            EpicZonePlayer ezp = Globals.getPlayer(event.getPlayer().getName());
            spoutManager.UpdatePlayerZone(ezp, ezp.getCurrentZone());
        } catch (Exception e)
        {
            Log.write(e.getMessage());
        }
    }

    // public @Override void onServerTick(ServerTickEvent event)
    // {
    //
    // }

}
