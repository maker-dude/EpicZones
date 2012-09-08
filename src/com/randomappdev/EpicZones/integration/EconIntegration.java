package com.randomappdev.EpicZones.integration;

import com.randomappdev.EpicZones.General;
import com.randomappdev.EpicZones.Util;
import com.randomappdev.EpicZones.objects.EpicZone;
import com.randomappdev.EpicZones.objects.EpicZonePlayer;
import org.bukkit.entity.Player;

public class EconIntegration
{

    public static void buyZone(Player player, EpicZone ez)
    {
        EpicZonePlayer ezp = General.getPlayer(player.getName());
        if (ezp != null)
        {
            if (ez.getEcon().getForSale())
            {
                if (General.economy.withdrawPlayer(player.getName(), ez.getEcon().getPurchasePrice()).transactionSuccess())
                {
                    Player seller = General.plugin.getServer().getPlayer(ez.getEcon().getSeller());

                    if (seller != null)
                    {
                        seller.sendMessage(ez.getName() + " has been sold.");
                    }

                    //Pay Seller
                    General.economy.depositPlayer(ez.getEcon().getSeller(), ez.getEcon().getPurchasePrice());

                    ez.addOwner(player.getName());
                    ez.getEcon().setForSale(false);
                    General.plugin.getServer().getWorld(ez.getWorld()).getBlockAt(Util.GetLocationFromString(ez.getEcon().getSignLocation())).breakNaturally();
                    player.sendMessage("Congratulations! You are now the owner of " + ez.getName() + ".");
                    General.SaveZones();

                } else
                {
                    player.sendMessage("You are not able to buy this Zone.");
                }
            }
        }
    }
}
