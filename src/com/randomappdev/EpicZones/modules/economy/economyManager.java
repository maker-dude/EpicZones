package com.randomappdev.EpicZones.modules.economy;

import com.randomappdev.EpicZones.modules.core.objects.EpicZone;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer;
import com.randomappdev.EpicZones.modules.economy.listeners.BlockEvents;
import com.randomappdev.EpicZones.modules.economy.listeners.PlayerEvents;
import com.randomappdev.EpicZones.utilities.Globals;
import com.randomappdev.EpicZones.utilities.Util;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class economyManager
{

    private static PlayerEvents playerListener;
    private static BlockEvents blockListener;

    public static void init(PluginManager pluginManager)
    {

        playerListener = new PlayerEvents();
        blockListener = new BlockEvents();

        pluginManager.registerEvents(playerListener, Globals.plugin);
        pluginManager.registerEvents(blockListener, Globals.plugin);

    }

    public static void buyZone(Player player, EpicZone ez)
    {
        EpicZonePlayer ezp = Globals.getPlayer(player.getName());
        if (ezp != null)
        {
            if (ez.getEcon().getForSale())
            {
                if (Globals.economy.withdrawPlayer(player.getName(), ez.getEcon().getPurchasePrice()).transactionSuccess())
                {
                    Player seller = Globals.plugin.getServer().getPlayer(ez.getEcon().getSeller());

                    if (seller != null)
                    {
                        seller.sendMessage(ez.getName() + " has been sold.");
                    }

                    //Pay Seller
                    Globals.economy.depositPlayer(ez.getEcon().getSeller(), ez.getEcon().getPurchasePrice());

                    ez.addOwner(player.getName());
                    ez.getEcon().setForSale(false);
                    Globals.plugin.getServer().getWorld(ez.getWorld()).getBlockAt(Util.getLocationFromString(ez.getEcon().getSignLocation())).breakNaturally();
                    player.sendMessage("Congratulations! You are now the owner of " + ez.getName() + ".");
                    Globals.SaveZones();

                } else
                {
                    player.sendMessage("You are not able to buy this Zone.");
                }
            }
        }
    }
}
