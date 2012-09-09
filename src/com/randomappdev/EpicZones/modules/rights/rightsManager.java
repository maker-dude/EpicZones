package com.randomappdev.EpicZones.modules.rights;

import com.randomappdev.EpicZones.modules.core.permissionsManager;
import com.randomappdev.EpicZones.modules.core.objects.EpicZone;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePermission;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer;
import com.randomappdev.EpicZones.modules.rights.listeners.ChangesListener;
import com.randomappdev.EpicZones.modules.rights.listeners.MovementListener;
import com.randomappdev.EpicZones.utilities.Config;
import com.randomappdev.EpicZones.utilities.Globals;
import com.randomappdev.EpicZones.utilities.Log;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class rightsManager
{
    private static final String PERMS_IGNORE = "epiczones.ignorepermissions";

    private static ChangesListener changeListener;
    private static MovementListener movementListener;

    public static void init(PluginManager pluginManager)
    {

        changeListener = new ChangesListener();
        movementListener = new MovementListener();

        pluginManager.registerEvents(changeListener, Globals.plugin);
        pluginManager.registerEvents(movementListener, Globals.plugin);

    }

    public static boolean hasRights(Player player, EpicZone zone, String flag)
    {
        try
        {
            if (!permissionsManager.hasPermission(player, PERMS_IGNORE))
            {
                if (!zone.isOwner(player.getName()))
                {
                    if (zone.hasPermission(player, flag, EpicZonePermission.PermType.DENY)) // EpicZones.permissions.hasPermission(player, getPermNode(zone, flag, true)))
                    {
                        return false;
                    }
                    if (zone.hasPermission(player, flag, EpicZonePermission.PermType.ALLOW)) // EpicZones.permissions.hasPermission(player, getPermNode(zone, flag, true)))
                    {
                        return true;
                    }
                    if (zone.hasPermissionFromGroup(player, flag, EpicZonePermission.PermType.DENY)) // EpicZones.permissions.hasPermission(player, getPermNode(zone, flag, true)))
                    {
                        return false;
                    }
                    if (zone.hasPermissionFromGroup(player, flag, EpicZonePermission.PermType.ALLOW)) // EpicZones.permissions.hasPermission(player, getPermNode(zone, flag, true)))
                    {
                        return true;
                    }
                    if (zone.hasParent())
                    {
                        return hasRights(player, zone.getParent(), flag);
                    }
                    if (zone.getType() != EpicZone.ZoneType.GLOBAL)
                    {
                        return hasRights(player, Globals.myGlobalZones.get(player.getWorld().getName().toLowerCase()), flag);
                    } else
                    {
                        if (flag.equalsIgnoreCase("build"))
                        {
                            return Config.globalZoneDefaultBuild;
                        } else if (flag.equalsIgnoreCase("destroy"))
                        {
                            return Config.globalZoneDefaultDestroy;
                        } else //If for some reason anything other than build, deny or enter come thru, its going to default to the enter permission.
                        {
                            return Config.globalZoneDefaultEnter;
                        }
                    }
                } else
                {
                    return true;
                }
            } else
            {
                return true;
            }
        } catch (Exception e)
        {
            Log.write(e.getMessage());
            return false;
        }
    }

    public static boolean movementLogic(Player player, Location fromLoc, EpicZone toZone)
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
                        if (toZone != null)
                        {
                            if (hasRights(player, toZone, "entry"))
                            {
                                result = true;
                            } else
                            {
                                result = false;
                            }
                        }
                        ezp.Check();
                    }
                    ezp.setHasMoved(true);
                }
            }
        }
        return result;
    }
}
