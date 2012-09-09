/*

This file is part of EpicZones

Copyright (C) 2011 by Team ESO

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

 */

/**
 * @author jblaske@gmail.com
 * @license MIT License
 */

package com.randomappdev.EpicZones.modules.core.commands;

import com.randomappdev.EpicZones.EpicZones;
import com.randomappdev.EpicZones.modules.core.objects.EpicZone;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer;
import com.randomappdev.EpicZones.utilities.Config;
import com.randomappdev.EpicZones.utilities.Globals;
import com.randomappdev.EpicZones.utilities.Log;
import com.randomappdev.EpicZones.utilities.Messaging;
import com.randomappdev.EpicZones.utilities.Messaging.Message_ID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.*;
import java.util.Date;

@SuppressWarnings({"ResultOfMethodCallIgnored"})
public class EZZoneDebug
{
    public EZZoneDebug(CommandSender sender)
    {
        boolean canRunCommand = true;
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            EpicZonePlayer ezp = Globals.getPlayer(player.getName());
            canRunCommand = ezp.getAdmin();
        }

        if (canRunCommand)
        {
            GenerateDebugInfo();
            Messaging.Send(sender, Message_ID.Info_00041_DebugGenerated);
        }
    }

    private static final String DATA_PATH = "debug.txt";

    private static File Init()
    {
        if (!Globals.plugin.getDataFolder().exists())
        {
            Globals.plugin.getDataFolder().mkdir();
        }

        File file = new File(Globals.plugin.getDataFolder() + File.separator + DATA_PATH);
        if (!file.exists())
        {
            try
            {
                file.createNewFile();
            } catch (IOException e)
            {
                Log.write(e.getMessage());
            }
        }

        return file;
    }

    private static void GenerateDebugInfo()
    {
        File file = Init();

        try
        {
            String data = BuildDebugData();
            Writer output = new BufferedWriter(new FileWriter(file, false));
            try
            {
                output.write(data);
            } finally
            {
                output.close();
            }
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

    }

    private static String BuildDebugData()
    {
        String result = "";
        EpicZones plugin = Globals.plugin;
        PluginDescriptionFile pdfFile = plugin.getDescription();

        result = result + "========================================\n";
        result = result + pdfFile.getName() + " Debug Info\n";
        result = result + "Version: " + pdfFile.getVersion() + "\n";
        result = result + "Generated On: " + new Date().toString() + "\n";
        result = result + "========================================\n";
        result = result + "Players Online: " + plugin.getServer().getOnlinePlayers().length + "/" + plugin.getServer().getMaxPlayers() + "\n";

        result = result + "\nNumber of Zones: " + Globals.myZones.size() + "\n";
        for (String key : Globals.myZones.keySet())
        {
            EpicZone zone = Globals.myZones.get(key);
            result = result + zone.getName() + "[" + zone.getTag() + "] Type: " + zone.getType() + "\n";
        }

        result = result + "\nNumber of Global Zones: " + Globals.myGlobalZones.size() + "\n";
        for (String key : Globals.myGlobalZones.keySet())
        {
            EpicZone zone = Globals.myGlobalZones.get(key);
            result = result + zone.getName() + "[" + zone.getTag() + "] World: " + zone.getWorld() + "\n";
        }

        result = result + "\nHeroChat Configured To Be On? " + Config.enableHeroChat + "\n";
//        result = result + "HeroChat Enabled? " + Globals.HeroChatEnabled + "\n";
//        if (Globals.HeroChatEnabled)
//        {
//            result = result + "HeroChat Version: " + GetPluginVersion("HeroChat") + "\n";
//        }
//        result = result + "\nSpout Enabled? " + Globals.SpoutEnabled + "\n";
//        if (Globals.SpoutEnabled)
//        {
//            result = result + "Spout Version: " + GetPluginVersion("Spout") + "\n";
//        }

        result = result + "\n========== Installed Plugins ===========\n";
        for (Plugin plg : plugin.getServer().getPluginManager().getPlugins())
        {
            result = result + plg.getDescription().getName() + " Version: " + plg.getDescription().getVersion() + "\n";
        }
        result = result + "========================================\n";

        return result;
    }

    private static String GetPluginVersion(String pluginName)
    {
        String result;

        try
        {
            result = Globals.plugin.getServer().getPluginManager().getPlugin(pluginName).getDescription().getVersion() + "\n";
        } catch (Exception e)
        {
            result = "";
        }

        return result;
    }
}
