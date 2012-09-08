package com.randomappdev.EpicZones.commands;

import com.randomappdev.EpicZones.integration.PermissionsManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Util
{

    public static boolean isAdmin(CommandSender sender, String permission, boolean allowConsole)
    {

        boolean result = false;

        if (sender instanceof Player)
        {
            Player player = (Player) sender;

            if (player.isOp())
            {
                result = true;
            } else if (PermissionsManager.hasPermission(player, permission))
            {
                result = true;
            }
        } else
        {
            result = allowConsole;
        }

        return result;

    }

}
