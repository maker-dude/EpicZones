package com.randomappdev.EpicZones.utilities;

import com.randomappdev.EpicZones.modules.core.permissionsManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Util
{
    public static boolean isNumeric(String data)
    {
        return data.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+");
    }

    public static boolean isAdmin(CommandSender sender, String permission, boolean allowConsole)
    {

        boolean result = false;

        if (sender instanceof Player)
        {
            Player player = (Player) sender;

            if (player.isOp())
            {
                result = true;
            } else if (permissionsManager.hasPermission(player, permission))
            {
                result = true;
            }
        } else
        {
            result = allowConsole;
        }

        return result;

    }

    public static String getStringValueFromHashSet(String key, HashMap<String, Object> data)
    {
        String result = "";
        Object temp = getObjectValueFromHashSet(key, data);
        if (temp != null)
        {
            result = temp.toString();
        }
        return result;
    }

    public static Integer getIntegerValueFromHashSet(String key, HashMap<String, Object> data)
    {
        return getIntegerValueFromHashSet(key, data, 0);
    }

    public static Integer getIntegerValueFromHashSet(String key, HashMap<String, Object> data, Integer defaultValue)
    {
        Integer result = defaultValue;
        Object temp = getObjectValueFromHashSet(key, data);
        if (temp != null)
        {
            if (isNumeric(temp.toString()))
            {
                result = Integer.valueOf(temp.toString());
            }
        }
        return result;
    }

    public static Boolean getBooleanValueFromHashSet(String key, HashMap<String, Object> data)
    {
        return getBooleanValueFromHashSet(key, data, false);
    }

    public static Boolean getBooleanValueFromHashSet(String key, HashMap<String, Object> data, Boolean defaultValue)
    {
        Boolean result = defaultValue;
        Object temp = getObjectValueFromHashSet(key, data);
        if (temp != null)
        {
            result = Boolean.valueOf(temp.toString());
        }
        return result;
    }

    public static Object getObjectValueFromHashSet(String key, HashMap<String, Object> data)
    {
        Object result = null;
        if (data != null)
        {
            result = data.get(key);
            if (result == null)
            {
                result = data.get(key.toLowerCase());
                if (result == null)
                {
                    result = data.get(key.toUpperCase());
                }
            }
        }
        return result;
    }

    public static String getStringFromLocation(Location loc)
    {
        String result = "";
        if (loc != null)
        {
            result = loc.getWorld().getName() + ":";
            result = result + loc.getBlockX() + ":";
            result = result + loc.getBlockY() + ":";
            result = result + loc.getBlockZ() + ":";
        }
        return result;
    }

    public static Location getLocationFromString(String loc)
    {
        String[] split = loc.split(":");
        return new Location(Globals.plugin.getServer().getWorld(split[0]), new Double(split[1]), new Double(split[2]), new Double(split[3]));
    }

}
