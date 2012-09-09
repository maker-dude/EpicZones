package com.randomappdev.EpicZones.modules.core;

import com.randomappdev.EpicZones.modules.core.listeners.PlayerEvents;
import com.randomappdev.EpicZones.modules.core.listeners.VehicleEvents;
import com.randomappdev.EpicZones.modules.core.listeners.WorldEvents;
import com.randomappdev.EpicZones.modules.core.objects.EpicZone;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer;
import com.randomappdev.EpicZones.utilities.Globals;
import com.randomappdev.EpicZones.utilities.Messaging;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.awt.*;

public class coreManager
{

    private static PlayerEvents playerListener;
    private static VehicleEvents vehicleListener;
    private static WorldEvents worldListener;

    public static void init(PluginManager pluginManager)
    {

        playerListener = new PlayerEvents();
        vehicleListener = new VehicleEvents();
        worldListener = new WorldEvents();

        pluginManager.registerEvents(playerListener, Globals.plugin);
        pluginManager.registerEvents(vehicleListener, Globals.plugin);
        pluginManager.registerEvents(worldListener, Globals.plugin);

    }

    public static EpicZone getZoneForLocation(Location location)
    {
        return getZoneForPlayer(null, location);
    }

    public static EpicZone getZoneForPlayer(Player player, Location location)
    {

        String worldName = location.getWorld().getName().toLowerCase();
        int playerHeight = location.getBlockY();
        Point playerPoint = new Point(location.getBlockX(), location.getBlockZ());

        EpicZone result = null;
        if (player != null)
        {
            EpicZonePlayer ezp = Globals.getPlayer(player.getName());
            if (ezp.getCurrentZone() != null)
            {
                result = isPlayerWithinZone(ezp.getCurrentZone(), worldName, playerHeight, playerPoint);
            }
        }
        if (result == null)
        {
            for (String zoneTag : Globals.myZones.keySet())
            {
                EpicZone zone = Globals.myZones.get(zoneTag);
                result = isPlayerWithinZone(zone, worldName, playerHeight, playerPoint);
                if (result != null)
                {
                    break;
                }
            }
        }
        return result;
    }

    private static EpicZone isPlayerWithinZone(EpicZone zone, String worldName, int playerHeight, Point playerPoint)
    {
        EpicZone result = null;

        if (zone.IsPointWithin(worldName, playerHeight, playerPoint))
        {
            result = zone;
            if (zone.hasChildren())
            {
                EpicZone childResult;
                for (String zoneTag : zone.getChildren().keySet())
                {
                    childResult = isPlayerWithinZone(Globals.myZones.get(zoneTag), worldName, playerHeight, playerPoint);
                    if (childResult != null)
                    {
                        result = childResult;
                        break;
                    }
                }
            }
        }
        return result;
    }

    public static boolean playerMovementLogic(Player player, Location fromLoc, EpicZone toZone)
    {
        boolean result = true;
        if (player != null && fromLoc != null)
        {
            EpicZonePlayer ezp = Globals.getPlayer(player.getName());
            if (ezp != null)
            {
                if (ezp.shouldCheck())
                {
                    if (!ezp.isTeleporting())
                    {
                        if ((ezp.getCurrentZone() != null && !ezp.getCurrentZone().getTag().equals(toZone.getTag())) || ezp.getCurrentZone() == null)
                        {
                            if (ezp.getCurrentZone() != null)
                            {
                                ezp.setPreviousZoneTag(ezp.getCurrentZone().getTag());
                                if (ezp.getCurrentZone().getExitText().length() > 0)
                                {
                                    Messaging.Send(player, ezp.getCurrentZone().getExitText());
                                }
                            }
                            ezp.setCurrentZone(toZone);
                            if (toZone.getEnterText().length() > 0)
                            {
                                Messaging.Send(player, toZone.getEnterText());
                            }
                        }
                        ezp.setCurrentLocation(fromLoc);
                        ezp.Check();
                    }
                    ezp.setHasMoved(true);
                }
            }
        }
        return result;
    }
}
