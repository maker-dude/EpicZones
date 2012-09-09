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
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;

@SuppressWarnings({"ResultOfMethodCallIgnored"})
public class Config
{
    private static File file;
    public static boolean enableBorder;
    public static boolean enableHeroChat;
    public static boolean enableEconomy;
    public static boolean enableExtras;
    public static boolean enableSpout;
    public static boolean enableRights;
    public static boolean enableProtection;
    public static boolean globalZoneDefaultBuild;
    public static boolean globalZoneDefaultDestroy;
    public static boolean globalZoneDefaultEnter;
    public static String language = "EN_US";

    private static final String CONFIG_FILE = "config.yml";

    public static void Init(EpicZones plugin)
    {

        enableBorder = false;
        enableHeroChat = false;
        enableEconomy = false;
        enableExtras = false;
        enableSpout = false;
        enableRights = false;
        enableProtection = false;

        globalZoneDefaultBuild = true;
        globalZoneDefaultDestroy = true;
        globalZoneDefaultEnter = true;

        language = "EN_US";

        if (!plugin.getDataFolder().exists())
        {
            plugin.getDataFolder().mkdir();
        }

        file = new File(plugin.getDataFolder() + File.separator + CONFIG_FILE);
        if (!file.exists())
        {
            try
            {
                file.createNewFile();
            } catch (IOException e)
            {
                Log.write(e.getMessage());
            }
            Save();
        }

    }

    @SuppressWarnings("unchecked")
    public static void Load(EpicZones plugin)
    {
        Init(plugin);

        if (file.exists())
        {
            Yaml yaml = new Yaml();
            HashMap<String, Object> root;
            FileInputStream stream;
            try
            {
                stream = new FileInputStream(file);
                root = (HashMap<String, Object>) yaml.load(stream);

                enableBorder = Util.getBooleanValueFromHashSet("enableRadius", root); //Look for old config line first.
                enableBorder = Util.getBooleanValueFromHashSet("enableBorder", root);
                enableHeroChat = Util.getBooleanValueFromHashSet("enableHeroChat", root);
                enableEconomy = Util.getBooleanValueFromHashSet("enableEconomy", root);
                enableExtras = Util.getBooleanValueFromHashSet("enableExtras", root);
                enableSpout = Util.getBooleanValueFromHashSet("enableSpout", root);
                enableRights = Util.getBooleanValueFromHashSet("enableRights", root);
                enableProtection = Util.getBooleanValueFromHashSet("enableProtection", root);

                if (Util.getObjectValueFromHashSet("globalZoneDefaultAllow", root) == null)
                {
                    globalZoneDefaultBuild = Util.getBooleanValueFromHashSet("globalZoneDefaultBuild", root);
                    globalZoneDefaultDestroy = Util.getBooleanValueFromHashSet("globalZoneDefaultDestroy", root);
                    globalZoneDefaultEnter = Util.getBooleanValueFromHashSet("globalZoneDefaultEnter", root);
                } else
                {
                    globalZoneDefaultBuild = Util.getBooleanValueFromHashSet("globalZoneDefaultAllow", root);
                    globalZoneDefaultDestroy = globalZoneDefaultBuild;
                    globalZoneDefaultEnter = globalZoneDefaultBuild;
                }

                language = Util.getStringValueFromHashSet("language", root);

            } catch (FileNotFoundException e)
            {
                Log.write(e.getMessage());
            }
        }
        Save();
    }

    public static void Save()
    {

        Yaml yaml = new Yaml();
        HashMap<String, Object> root = new HashMap<String, Object>();
        FileOutputStream stream;
        BufferedWriter writer;

        root.put("enableBorder", enableBorder);
        root.put("enableHeroChat", enableHeroChat);
        root.put("enableEconomy", enableEconomy);
        root.put("enableExtras", enableExtras);
        root.put("enableSpout", enableSpout);
        root.put("enableRights", enableRights);
        root.put("enableProtection", enableProtection);

        root.put("globalZoneDefaultBuild", globalZoneDefaultBuild);
        root.put("globalZoneDefaultDestroy", globalZoneDefaultDestroy);
        root.put("globalZoneDefaultEnter", globalZoneDefaultEnter);

        root.put("language", language);

        try
        {

            stream = new FileOutputStream(file);
            stream.getChannel().truncate(0);
            writer = new BufferedWriter(new OutputStreamWriter(stream));

            try
            {
                writer.write(yaml.dump(root));
            } finally
            {
                writer.close();
            }
        } catch (IOException e)
        {
            Log.write(e.getMessage());
        }

    }
}