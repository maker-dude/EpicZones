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

package com.randomappdev.EpicZones;

import com.herocraftonline.dthielke.herochat.HeroChat;
import com.randomappdev.EpicZones.commands.CommandHandler;
import com.randomappdev.EpicZones.commands.EZReload;
import com.randomappdev.EpicZones.commands.EZWho;
import com.randomappdev.EpicZones.commands.EZZone;
import com.randomappdev.EpicZones.integration.EpicSpout;
import com.randomappdev.EpicZones.integration.PermissionsManager;
import com.randomappdev.EpicZones.listeners.*;
import com.randomappdev.EpicZones.objects.EpicZonePlayer;
import com.randomappdev.EpicZones.objects.EpicZonePlayer.EpicZoneMode;
import com.randomappdev.pluginstats.Ping;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spout.Spout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@SuppressWarnings({"ResultOfMethodCallIgnored"})
public class EpicZones extends JavaPlugin
{

    private final PlayerEvents playerListener = new PlayerEvents();
    private final BlockEvents blockListener = new BlockEvents();
    private final EntityEvents entityListener = new EntityEvents();
    private final VehicleEvents vehicleListener = new VehicleEvents();
    private final WorldEvents worldListener = new WorldEvents();
    private SpoutEvents spoutListener;
    private SpoutInputEvents spoutInputListener;

    private Regen regen = new Regen(this);
    private Map<String, CommandHandler> handlers = new HashMap<String, CommandHandler>();

    private static final String[] ZONE_COMMANDS = {"ezzone", "zone"};
    private static final String[] WHO_COMMANDS = {"ezwho", "who", "online", "whois"};
    private static final String[] RELOAD_COMMANDS = {"ezreload", "reload"};

    private static CommandHandler reloadCommandHandler = new EZReload();
    private static CommandHandler zoneCommandHandler = new EZZone();
    private static CommandHandler whoCommandHandler = new EZWho();
    private static int scheduleID = -1;

    public static HeroChat heroChat = null;

    public void onEnable()
    {

        Config.Load(this);
        PluginDescriptionFile pdfFile = this.getDescription();
        Log.Init(pdfFile.getName());
        spoutListener = null;
        spoutInputListener = null;

        try
        {

            PluginManager pm = getServer().getPluginManager();

            pm.registerEvents(this.playerListener, this);
            pm.registerEvents(this.blockListener, this);
            pm.registerEvents(this.entityListener, this);
            pm.registerEvents(this.vehicleListener, this);
            pm.registerEvents(this.worldListener, this);

            scheduleID = getServer().getScheduler().scheduleSyncRepeatingTask(this, regen, 10, 10);

            registerCommands();

            setupPermissions();
            setupEpicZones();
            setupHeroChat();
            setupSpout(pm);

            Ping png = new Ping();
            png.init(this);

            Log.Write("version " + pdfFile.getVersion() + " is enabled.");

        } catch (Throwable e)
        {
            Log.Write(" error starting: " + e.getMessage() + " Disabling plugin");
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void onDisable()
    {

        while (getServer().getScheduler().isCurrentlyRunning(scheduleID))
        {
        }
        getServer().getScheduler().cancelTask(scheduleID);
        regen = null;

        PluginDescriptionFile pdfFile = this.getDescription();
        for (String playerName : General.myPlayers.keySet())
        {
            EpicZonePlayer ezp = General.myPlayers.get(playerName);
            if (ezp.getMode() != EpicZoneMode.None)
            {
                if (ezp.getEditZone() != null)
                {
                    ezp.getEditZone().HidePillars();
                }
            }
        }

        General.HeroChatEnabled = false;
        General.SpoutEnabled = false;
        Log.Write("version " + pdfFile.getVersion() + " is disabled.");
    }

    public void registerCommand(String command, CommandHandler handler)
    {
        handlers.put(command.toLowerCase(), handler);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        boolean result = true;
        CommandHandler handler = handlers.get(commandLabel.toLowerCase());
        if (handler != null)
        {
            result = handler.onCommand(commandLabel, sender, args);
        }
        return result;
    }

    public void setupPermissions()
    {
        PermissionsManager.Init(this);
    }

    private void EnablePlugin(String pluginName, String pluginType)
    {
        Plugin plg;
        plg = this.getServer().getPluginManager().getPlugin(pluginName);
        if (plg != null)
        {
            if (!plg.isEnabled())
            {
                try
                {
                    Log.Write("Detected " + pluginType + " Plugin > " + pluginName + " > Enabling...");
                    this.getServer().getPluginManager().enablePlugin(plg);
                } catch (Exception e)
                {
                    Log.Write(e.getMessage());
                }
            }
        }
    }

    public void setupHeroChat()
    {
        if (Config.enableHeroChat)
        {
            Plugin test = this.getServer().getPluginManager().getPlugin("HeroChat");
            if (test != null)
            {
                heroChat = (HeroChat) test;
                General.HeroChatEnabled = true;
                Log.Write("HeroChat Integration Enabled.");
            }
        }
    }

    public void setupSpout(PluginManager pm)
    {
        if (Config.enableSpout)
        {
            Plugin test = this.getServer().getPluginManager().getPlugin("Spout");
            if (test != null)
            {
                EnablePlugin("Spout", "Spout");
                EpicSpout.Init((Spout) test);
                this.spoutListener = new SpoutEvents();
                this.spoutInputListener = new SpoutInputEvents();
                pm.registerEvents(this.spoutListener, this);
                pm.registerEvents(this.spoutInputListener, this);
                General.SpoutEnabled = true;
                Log.Write("Spout Integration Enabled.");
            } else
            {
                Log.Write("Spout plugin not detected, unable to enable Spout integration.");
            }
        }
    }

    private void registerCommands()
    {
        for (String cmd : ZONE_COMMANDS)
        {
            registerCommand(cmd, zoneCommandHandler);
        }

        for (String cmd : WHO_COMMANDS)
        {
            registerCommand(cmd, whoCommandHandler);
        }

        for (String cmd : RELOAD_COMMANDS)
        {
            registerCommand(cmd, reloadCommandHandler);
        }
    }

    public void setupEpicZones()
    {

        if (General.SpoutEnabled)
        {
            for (String tag : General.myPlayers.keySet())
            {
                if (EpicSpout.UseSpout(General.myPlayers.get(tag)))
                {
                    EpicSpout.RemovePlayerControls(General.myPlayers.get(tag));
                }
            }
        }

        General.plugin = this;
        General.myZones.clear();
        General.myGlobalZones.clear();


        General.myPlayers.clear();
        Config.Load(this);
        General.version = this.getDescription().getVersion();
        LoadMessageList();
        General.LoadZones();
        General.addPlayer(null);
        for (Player p : getServer().getOnlinePlayers())
        {
            General.addPlayer(p);
        }
    }

    public void LoadMessageList()
    {
        String line;
        File file = new File(General.plugin.getDataFolder() + File.separator + "Language" + File.separator + Config.language + ".txt");
        Message.messageList = new HashMap<Integer, String>();
        boolean updateNeeded = false;
        boolean foundVersion = false;

        try
        {

            InitMessageList();
            Scanner scanner = new Scanner(file);

            try
            {
                while (scanner.hasNext())
                {
                    line = scanner.nextLine().trim();
                    if (!line.isEmpty())
                    {
                        if (line.startsWith("#"))
                        {
                            int versionIndex = line.indexOf("VERSION");
                            if (versionIndex > -1)
                            {
                                foundVersion = true;
                                String version = line.substring(line.indexOf(":") + 1, line.length());
                                if (version != null && version.length() > 0)
                                {
                                    if (!version.trim().equalsIgnoreCase(General.version))
                                    {
                                        updateNeeded = true;
                                        Log.Write("Language version [" + version.trim() + "] does not match plugin version [" + General.version + "], Updating...");
                                        break;
                                    }
                                } else
                                {
                                    updateNeeded = true;
                                    Log.Write("Invalid Language file, Updating...");
                                    break;
                                }
                            }
                        } else
                        {
                            Integer id;
                            String message;
                            id = Integer.parseInt(line.substring(0, line.indexOf(":")).trim());
                            message = line.substring(line.indexOf(":") + 1, line.length());
                            Message.messageList.put(id, message);
                        }
                    }
                }
            } finally
            {
                scanner.close();
            }

            if (updateNeeded || !foundVersion)
            {
                BuildLanguageFile(Config.language.toUpperCase(), true);
                LoadMessageList();
            } else
            {
                Log.Write("Language File Loaded [" + Config.language + ".txt" + "].");
            }
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void InitMessageList()
    {
        File file = new File(General.plugin.getDataFolder() + File.separator + "Language");

        if (!General.plugin.getDataFolder().exists())
        {
            General.plugin.getDataFolder().mkdir();
        }
        if (!file.exists())
        {
            file.mkdir();
        }
        BuildLanguageFiles();
    }

    private void BuildLanguageFiles()
    {
        BuildLanguageFile("EN_US", false);
        BuildLanguageFile("FR_FR", false);
        BuildLanguageFile("DE_DE", false);
    }

    private void BuildLanguageFile(String FileName, boolean force)
    {
        File file = new File(General.plugin.getDataFolder() + File.separator + "Language" + File.separator + FileName + ".txt");
        if (!file.exists() || force)
        {
            InputStream jarURL;
            jarURL = getClass().getResourceAsStream("/com/randomappdev/EpicZones/res/" + FileName + ".txt");
            try
            {
                copyFile(jarURL, file);
            } catch (Exception ex)
            {
                Log.Write(ex.getMessage());
            }
        }
    }

    public void copyFile(InputStream in, File out) throws Exception
    {
        FileOutputStream fos = new FileOutputStream(out);
        try
        {
            byte[] buf = new byte[1024];
            int i;
            while ((i = in.read(buf)) != -1)
            {
                fos.write(buf, 0, i);
            }
        } catch (Exception e)
        {
            Log.Write(e.getMessage());
        } finally
        {
            if (in != null)
            {
                in.close();
            }
            fos.close();
        }
    }

}
