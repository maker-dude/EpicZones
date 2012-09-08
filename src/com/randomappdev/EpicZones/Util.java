package com.randomappdev.EpicZones;

import org.bukkit.Location;

import java.util.HashMap;

public class Util
{
    public static boolean IsNumeric(String data)
    {
        return data.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+");
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
            if (IsNumeric(temp.toString()))
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
        // Log.Write(result.toString());
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

    public static Location GetLocationFromString(String loc)
    {

        String[] split = loc.split(":");
        return new Location(General.plugin.getServer().getWorld(split[0]), new Double(split[1]), new Double(split[2]), new Double(split[3]));

    }

}
