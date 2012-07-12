package com.randomappdev.EpicZones.listeners;

import com.randomappdev.EpicZones.General;
import com.randomappdev.EpicZones.Log;
import com.randomappdev.EpicZones.integration.EpicSpout;
import com.randomappdev.EpicZones.objects.EpicZonePlayer;
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
            EpicZonePlayer ezp = General.getPlayer(event.getPlayer().getName());
            EpicSpout.UpdatePlayerZone(ezp, ezp.getCurrentZone());
        } catch (Exception e)
        {
            Log.Write(e.getMessage());
        }
    }

    // public @Override void onServerTick(ServerTickEvent event)
    // {
    //
    // }

}
