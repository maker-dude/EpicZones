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

package com.randomappdev.EpicZones.utilities;

import com.randomappdev.EpicZones.EpicZones;
import com.randomappdev.EpicZones.modules.core.objects.EpicZone;
import com.randomappdev.EpicZones.modules.core.objects.EpicZone.ZoneType;
import com.randomappdev.EpicZones.modules.core.objects.EpicZoneDAL;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer.EpicZoneMode;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

@SuppressWarnings({"ResultOfMethodCallIgnored"})
public class Globals
{

    public static Map<String, EpicZone> myZones = new HashMap<String, EpicZone>();
    public static Map<String, EpicZone> myGlobalZones = new HashMap<String, EpicZone>();
    public static Map<String, EpicZonePlayer> myPlayers = new HashMap<String, EpicZonePlayer>();

    public static boolean borderEnabled = false;
    public static boolean economyEnabled = false;
    public static boolean extrasEnabled = false;
    public static boolean herochatEnabled = false;
    public static boolean protectionEnabled = false;
    public static boolean rightsEnabled = false;
    public static boolean spoutEnabled = false;

    public static String version;
    public static EpicZones plugin;
    public static Economy economy;
    public static Set<Integer> interactiveItems = new HashSet<Integer>();

    private static final String ZONE_FILE = "zones.txt";

    // private static File myFile;

    public static EpicZonePlayer getPlayer(String name)
    {
        return myPlayers.get(name.toLowerCase());
    }

    public static void addPlayer(@Nullable Player player)
    {
        if (player != null)
        {
            if (!myPlayers.containsKey(player.getName().toLowerCase()))
            {
                myPlayers.put(player.getName().toLowerCase(), new EpicZonePlayer(player));
            }
            myPlayers.get(player.getName().toLowerCase()).updateSecurity();
        } else
        {
            myPlayers.put("console", new EpicZonePlayer("console"));
        }
    }

    public static void removePlayer(String playerName)
    {
        EpicZonePlayer ezp = myPlayers.get(playerName.toLowerCase());
        if (ezp != null)
        {
            if (ezp.getMode() != EpicZoneMode.None)
            {
                if (ezp.getEditZone() != null)
                {
                    ezp.getEditZone().HidePillars();
                }
            }
            myPlayers.remove(playerName.toLowerCase());
        }
    }

    public static void LoadZones()
    {

        myZones.clear();

        if (loadZonesFromText())
        {
            Log.write("Converting Zones.txt into new zone format...");
            SaveZones();
            File file = new File(plugin.getDataFolder() + File.separator + ZONE_FILE);
            file.renameTo(new File(plugin.getDataFolder() + File.separator + ZONE_FILE + ".old"));
            myZones.clear();
        }

        myZones = EpicZoneDAL.Load();

        reconsileGlobalZones();
        reconcileChildren();
        SaveZones();

    }

    public static boolean loadZonesFromText()
    {
        try
        {

            String line;
            File file = new File(plugin.getDataFolder() + File.separator + ZONE_FILE);

            if (file.exists())
            {
                try
                {

                    Scanner scanner = new Scanner(file);
                    myZones.clear();

                    try
                    {
                        while (scanner.hasNext())
                        {
                            EpicZone newZone;
                            line = scanner.nextLine().trim();
                            if (line.startsWith("#") || line.isEmpty())
                            {
                                continue;
                            }
                            newZone = new EpicZone(line);
                            Globals.myZones.put(newZone.getTag(), newZone);
                        }

                    } finally
                    {
                        scanner.close();
                    }
                } catch (Exception e)
                {
                    Log.write(e.getMessage());
                }

                reconcileChildren();

                return true;

            }

        } catch (Exception e)
        {
            Log.write(e.getMessage());
        }
        return false;

    }

    public static void SaveZones()
    {
        for (String zoneTag : myZones.keySet())
        {
            EpicZoneDAL.Save(myZones.get(zoneTag));
        }
    }

    public static void reconsileGlobalZones()
    {
        for (String zoneTag : myZones.keySet())
        {
            EpicZone zone = myZones.get(zoneTag);
            if (zone.getType() == ZoneType.GLOBAL)
            {
                myGlobalZones.put(zone.getTag().toLowerCase(), zone);
            }
        }

        for (World world : plugin.getServer().getWorlds())
        {
            AddWorld(world);
        }

    }

    public static void AddWorld(World world)
    {
        Log.write("Adding World: " + world.getName().toLowerCase());

        if (myGlobalZones.get(world.getName().toLowerCase()) == null)
        {

            EpicZone newGlobal = new EpicZone();


            newGlobal.setTag(world.getName().toLowerCase());
            newGlobal.setName(world.getName());
            newGlobal.setRadius(1000);
            newGlobal.setType("GLOBAL");
            newGlobal.setMobs("all");
            newGlobal.setWorld(world.getName());

            for (String zoneTag : myZones.keySet())
            {
                EpicZone zone = myZones.get(zoneTag);
                if (zone.getWorld().equalsIgnoreCase(world.getName()))
                {
                    newGlobal.addChild(zone);
                }
            }

            myZones.put(newGlobal.getTag(), newGlobal);
            myGlobalZones.put(world.getName().toLowerCase(), newGlobal);

            EpicZoneDAL.Save(myZones.get(newGlobal.getTag()));

            Log.write("Global Zone Created For World [" + world.getName() + "]");

        }
    }

    public static void removeWorld(World world)
    {
        myZones.remove(world.getName().toLowerCase());
        myGlobalZones.remove(world.getName().toLowerCase());
    }

    private static void reconcileChildren()
    {

        ArrayList<String> badChildren = new ArrayList<String>();

        try
        {
            for (String zoneTag : myZones.keySet())
            {
                EpicZone zone = myZones.get(zoneTag);
                if (zone.hasChildren())
                {
                    for (String child : zone.getChildrenTags())
                    {
                        EpicZone childZone = myZones.get(child);
                        if (childZone != null)
                        {
                            childZone.setParent(zone);
                            zone.addChild(childZone);
                        } else
                        {
                            Log.write("The zone [" + zoneTag + "] has an invalid child > [" + child + "]");
                            badChildren.add(child);
                        }
                    }
                    if (badChildren.size() > 0)
                    {
                        Log.write("Removing invalid children from [" + zoneTag + "]");
                        for (String badChild : badChildren)
                        {
                            zone.removeChild(badChild);
                        }
                        badChildren = new ArrayList<String>();
                    }
                }
            }
        } catch (Exception e)
        {
            Log.write(e.getMessage());
        }
    }

    public static void WarnPlayer(Player player, Messaging.Message_ID messageID)
    {
        EpicZonePlayer ezp = Globals.getPlayer(player.getName());
        if (ezp != null)
        {
            if (ezp.getLastWarned().before(new Date()))
            {
                Messaging.Send(player, messageID);
                ezp.Warn();
            }
        }
    }

    public static void WarnPlayer(Player player, EpicZonePlayer ezp, String message)
    {
        if (ezp.getLastWarned().before(new Date()))
        {
            Messaging.Send(player, message);
            ezp.Warn();
        }
    }

}
