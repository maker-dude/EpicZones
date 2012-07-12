package com.randomappdev.EpicZones.listeners;

import com.randomappdev.EpicZones.General;
import com.randomappdev.EpicZones.Log;
import com.randomappdev.EpicZones.integration.EpicSpout;
import com.randomappdev.EpicZones.objects.EpicZonePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.keyboard.Keyboard;

public class SpoutInputEvents implements Listener
{

    @EventHandler
    public void onKeyPressedEvent(KeyPressedEvent event)
    {
        try
        {
            if (event.getKey() == Keyboard.KEY_F4)
            {
                EpicZonePlayer ezp = General.getPlayer(event.getPlayer().getName());
                ezp.UI.setDisplayXYZ(!ezp.UI.getDisplayXYZ());
                EpicSpout.UpdatePlayerXYZ(event.getPlayer());
            }
        } catch (Exception e)
        {
            Log.Write(e.getMessage());
        }
    }

    // public @Override void onKeyReleasedEvent(KeyReleasedEvent event)
    // {}
    //
    // public @Override void onRenderDistanceChange(RenderDistanceChangeEvent
    // event)
    // {}
}
