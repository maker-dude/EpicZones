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
import com.randomappdev.EpicZones.modules.border.borderManager;
import com.randomappdev.EpicZones.modules.core.MetricsLite;
import com.randomappdev.EpicZones.modules.core.commands.CommandHandler;
import com.randomappdev.EpicZones.modules.core.commands.EZReload;
import com.randomappdev.EpicZones.modules.core.commands.EZWho;
import com.randomappdev.EpicZones.modules.core.commands.EZZone;
import com.randomappdev.EpicZones.modules.core.coreManager;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer.EpicZoneMode;
import com.randomappdev.EpicZones.modules.core.permissionsManager;
import com.randomappdev.EpicZones.modules.economy.economyManager;
import com.randomappdev.EpicZones.modules.extras.Regen;
import com.randomappdev.EpicZones.modules.protection.protectionManager;
import com.randomappdev.EpicZones.modules.rights.rightsManager;
import com.randomappdev.EpicZones.modules.spout.spoutManager;
import com.randomappdev.EpicZones.utilities.Config;
import com.randomappdev.EpicZones.utilities.Globals;
import com.randomappdev.EpicZones.utilities.Log;
import com.randomappdev.EpicZones.utilities.Messaging;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
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

    private Regen regen = new Regen(this);
    private Map<String, CommandHandler> handlers = new HashMap<String, CommandHandler>();

    private static final String[] ZONE_COMMANDS = {"ezzone", "zone"};
    private static final String[] WHO_COMMANDS = {"ezwho", "who", "online", "whois"};
    private static final String[] RELOAD_COMMANDS = {"ezreload", "reload"};

    private static CommandHandler reloadCommandHandler = new EZReload();
    private static CommandHandler zoneCommandHandler = new EZZone();
    private static CommandHandler whoCommandHandler = new EZWho();
    public static HeroChat heroChat = null;
    Permission permission = null;

    public void onEnable()
    {

        PluginDescriptionFile pdfFile = this.getDescription();
        Log.init(pdfFile.getName());

        try
        {

            setupEpicZones();

            MetricsLite metricsLite = new MetricsLite(this);
            metricsLite.start();

            Log.write("version " + pdfFile.getVersion() + " is enabled.");

        } catch (Throwable e)
        {
            Log.write(" error starting: " + e.getMessage() + " Disabling plugin");
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void onDisable()
    {

        getServer().getScheduler().cancelTasks(this);
        regen = null;

        PluginDescriptionFile pdfFile = this.getDescription();

        for (String playerName : Globals.myPlayers.keySet())
        {
            EpicZonePlayer ezp = Globals.myPlayers.get(playerName);
            if (ezp.getMode() != EpicZoneMode.None)
            {
                if (ezp.getEditZone() != null)
                {
                    ezp.getEditZone().HidePillars();
                }
            }
        }

        Log.write("version " + pdfFile.getVersion() + " is disabled.");
    }

    public void setupEpicZones()
    {

        Config.Load(this);
        PluginManager pluginManager = getServer().getPluginManager();
        setupPermissions();
        setupCore(pluginManager);
        setupRights(pluginManager);
        setupBorder(pluginManager);
        setupProtection(pluginManager);
        setupExtras(pluginManager);
        setupHeroChat();
        setupSpout(pluginManager);
        setupEconomy(pluginManager);
        loadInteractiveItems();

    }

    private void setupCore(PluginManager pluginManager)
    {

        Globals.plugin = this;
        Globals.myZones.clear();
        Globals.myGlobalZones.clear();
        Globals.myPlayers.clear();
        Globals.version = this.getDescription().getVersion();

        coreManager.init(pluginManager);

        LoadMessageList();

        Globals.LoadZones();
        Globals.addPlayer(null);

        for (Player p : getServer().getOnlinePlayers())
        {
            Globals.addPlayer(p);
        }

        registerCommands();

    }

    private void setupRights(PluginManager pluginManager)
    {
        if (Config.enableRights)
        {
            rightsManager.init(pluginManager);
            Globals.rightsEnabled = true;
        }
        if (Globals.rightsEnabled)
        {
            Log.write("Rights Module Enabled.");
        } else
        {
            Log.write("Rights Module NOT Enabled.");
        }
    }

    private void setupBorder(PluginManager pluginManager)
    {
        if (Config.enableBorder)
        {
            borderManager.init(pluginManager);
            Globals.borderEnabled = true;
        }
        if (Globals.borderEnabled)
        {
            Log.write("Border Module Enabled.");
        } else
        {
            Log.write("Border Module NOT Enabled.");
        }
    }

    private void setupProtection(PluginManager pluginManager)
    {
        if (Config.enableProtection)
        {
            protectionManager.init(pluginManager);
            Globals.protectionEnabled = true;
        }
        if (Globals.protectionEnabled)
        {
            Log.write("Protection Module Enabled.");
        } else
        {
            Log.write("Protection Module NOT Enabled.");
        }
    }

    private void setupExtras(PluginManager pluginManager)
    {
        if (Config.enableExtras)
        {
            getServer().getScheduler().scheduleSyncRepeatingTask(this, regen, 10, 10);
            Globals.extrasEnabled = true;
        }
        if (Globals.extrasEnabled)
        {
            Log.write("Extras Module Enabled.");
        } else
        {
            Log.write("Extras Module NOT Enabled.");
        }
    }

    private void setupPermissions()
    {
        if (getServer().getPluginManager().getPlugin("Vault") != null)
        {
            RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
            permission = rsp.getProvider();
        }
        permissionsManager.Init(permission);
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
                    Log.write("Detected " + pluginType + " Plugin > " + pluginName + " > Enabling...");
                    this.getServer().getPluginManager().enablePlugin(plg);
                } catch (Exception e)
                {
                    Log.write(e.getMessage());
                }
            }
        }
    }

    private void setupHeroChat()
    {
        if (Config.enableHeroChat)
        {
            Plugin test = this.getServer().getPluginManager().getPlugin("HeroChat");
            if (test != null)
            {
                heroChat = (HeroChat) test;
                Globals.herochatEnabled = true;
            }
        }
        if (Globals.herochatEnabled)
        {
            Log.write("HeroChat Module Enabled.");
        } else
        {
            Log.write("HeroChat Module NOT Enabled.");
        }
    }

    private void setupEconomy(PluginManager pluginManager)
    {
        if (Config.enableEconomy)
        {
            if (pluginManager.getPlugin("Vault") != null)
            {
                RegisteredServiceProvider<Economy> economyProvider = this.getServer().getServicesManager().getRegistration(Economy.class);
                if (economyProvider != null)
                {
                    Globals.economy = economyProvider.getProvider();
                }
                if (Globals.economy != null)
                {
                    economyManager.init(pluginManager);
                    Globals.economyEnabled = true;
                }
            }
        }
        if (Globals.economyEnabled)
        {
            Log.write("Economy Module Enabled.");
        } else
        {
            Log.write("Economy Module NOT Enabled.");
        }
    }

    private void setupSpout(PluginManager pm)
    {
        if (Config.enableSpout)
        {
            Plugin test = this.getServer().getPluginManager().getPlugin("Spout");
            if (test != null)
            {
                EnablePlugin("Spout", "Spout");
                spoutManager.init((Spout) test, pm);

                for (String tag : Globals.myPlayers.keySet())
                {
                    if (spoutManager.isSpoutActive(Globals.myPlayers.get(tag)))
                    {
                        spoutManager.removePlayerControls(Globals.myPlayers.get(tag));
                    }
                }
                Globals.spoutEnabled = true;
            }
        }
        if (Globals.spoutEnabled)
        {
            Log.write("Spout Module Enabled.");
        } else
        {
            Log.write("Spout Module NOT Enabled.");
        }
    }

    private void loadInteractiveItems()
    {
        Globals.interactiveItems.clear();
        Globals.interactiveItems.add(324); // Wood Door
        Globals.interactiveItems.add(330); // Iron Door
        Globals.interactiveItems.add(323); // Sign
        Globals.interactiveItems.add(321); // Painting
        Globals.interactiveItems.add(354); // Bed
        Globals.interactiveItems.add(355); // Cake
        Globals.interactiveItems.add(356); // Redstone Repeater
    }

    private void LoadMessageList()
    {
        String line;
        File file = new File(Globals.plugin.getDataFolder() + File.separator + "Language" + File.separator + Config.language + ".txt");
        Messaging.messageList = new HashMap<Integer, String>();
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
                                    if (!version.trim().equalsIgnoreCase(Globals.version))
                                    {
                                        updateNeeded = true;
                                        Log.write("Language version [" + version.trim() + "] does not match plugin version [" + Globals.version + "], Updating...");
                                        break;
                                    }
                                } else
                                {
                                    updateNeeded = true;
                                    Log.write("Invalid Language file, Updating...");
                                    break;
                                }
                            }
                        } else
                        {
                            Integer id;
                            String message;
                            id = Integer.parseInt(line.substring(0, line.indexOf(":")).trim());
                            message = line.substring(line.indexOf(":") + 1, line.length());
                            Messaging.messageList.put(id, message);
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
                Log.write("Language File Loaded [" + Config.language + ".txt" + "].");
            }
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void InitMessageList()
    {
        File file = new File(Globals.plugin.getDataFolder() + File.separator + "Language");

        if (!Globals.plugin.getDataFolder().exists())
        {
            Globals.plugin.getDataFolder().mkdir();
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
        File file = new File(Globals.plugin.getDataFolder() + File.separator + "Language" + File.separator + FileName + ".txt");
        if (!file.exists() || force)
        {
            InputStream jarURL;
            jarURL = getClass().getResourceAsStream("/com/randomappdev/EpicZones/res/" + FileName + ".txt");
            try
            {
                copyFile(jarURL, file);
            } catch (Exception ex)
            {
                Log.write(ex.getMessage());
            }
        }
    }

    private void copyFile(InputStream in, File out) throws Exception
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
            Log.write(e.getMessage());
        } finally
        {
            if (in != null)
            {
                in.close();
            }
            fos.close();
        }
    }

    private void registerCommand(String command, CommandHandler handler)
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
}
