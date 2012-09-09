package com.randomappdev.EpicZones.modules.spout.listeners;

import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer;
import com.randomappdev.EpicZones.modules.spout.spoutManager;
import com.randomappdev.EpicZones.utilities.Globals;
import com.randomappdev.EpicZones.utilities.Log;
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
                EpicZonePlayer ezp = Globals.getPlayer(event.getPlayer().getName());
                ezp.UI.setDisplayXYZ(!ezp.UI.getDisplayXYZ());
                spoutManager.UpdatePlayerXYZ(event.getPlayer());
            }
        } catch (Exception e)
        {
            Log.write(e.getMessage());
        }
    }

    // public @Override void onKeyReleasedEvent(KeyReleasedEvent event)
    // {}
    //
    // public @Override void onRenderDistanceChange(RenderDistanceChangeEvent
    // event)
    // {}
}
