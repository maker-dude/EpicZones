package com.randomappdev.EpicZones.modules.protection;

import com.randomappdev.EpicZones.modules.protection.listeners.BlockEvents;
import com.randomappdev.EpicZones.modules.protection.listeners.EntityEvents;
import com.randomappdev.EpicZones.modules.protection.listeners.PlayerEvents;
import com.randomappdev.EpicZones.utilities.Globals;
import org.bukkit.plugin.PluginManager;

public class protectionManager
{
    private static PlayerEvents playerListener;
    private static BlockEvents blockListener;
    private static EntityEvents entityListener;

    public static void init(PluginManager pluginManager)
    {

        playerListener = new PlayerEvents();
        blockListener = new BlockEvents();
        entityListener = new EntityEvents();

        pluginManager.registerEvents(playerListener, Globals.plugin);
        pluginManager.registerEvents(blockListener, Globals.plugin);
        pluginManager.registerEvents(entityListener, Globals.plugin);

    }
}

