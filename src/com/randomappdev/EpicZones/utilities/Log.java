package com.randomappdev.EpicZones.utilities;

public class Log
{

    private static String PluginName;

    public static void init(String pluginName)
    {
        PluginName = pluginName;
    }

    public static void write(String message)
    {
        if (message != null)
        {
            System.out.println("[" + PluginName + "] " + message.trim());
        }
    }

}

