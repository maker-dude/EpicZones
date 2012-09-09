package com.randomappdev.EpicZones.modules.border;

import com.randomappdev.EpicZones.modules.border.listeners.ChangesListener;
import com.randomappdev.EpicZones.modules.border.listeners.MovementListener;
import com.randomappdev.EpicZones.modules.core.permissionsManager;
import com.randomappdev.EpicZones.modules.core.objects.EpicZone;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer;
import com.randomappdev.EpicZones.utilities.Globals;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.awt.*;

public class borderManager
{

    private static ChangesListener changesListener;
    private static MovementListener movementListener;

    public static void init(PluginManager pluginManager)
    {

        changesListener = new ChangesListener();
        movementListener = new MovementListener();

        pluginManager.registerEvents(changesListener, Globals.plugin);
        pluginManager.registerEvents(movementListener, Globals.plugin);

    }

    public static boolean borderLogic(Location location, Player player)
    {

        Point point = new Point(location.getBlockX(), location.getBlockZ());

        if (point != null && player != null)
        {

            EpicZonePlayer ezp = Globals.getPlayer(player.getName());
            EpicZone globalZone = Globals.myGlobalZones.get(player.getWorld().getName().toLowerCase());

            double xSquared = point.x * point.x;
            double ySquared = point.y * point.y;
            double distanceFromCenter = Math.sqrt(xSquared + ySquared);

            if (globalZone != null && ezp != null)
            {
                if (distanceFromCenter <= globalZone.getRadius())
                {
                    if (ezp.getPastBorder())
                    {
                        ezp.setPastBorder(false);
                    }
                    return true;
                } else
                {
                    if (permissionsManager.hasPermission(player, "epiczones.ignoremapradius"))
                    {
                        if (!ezp.getPastBorder())
                        {
                            ezp.setPastBorder(true);
                        }
                        return true;
                    } else
                    {
                        return false;
                    }
                }
            } else
            {   // No border defined for the world in config.
                return true;
            }
        } else
        {
            return true;
        }
    }
}
