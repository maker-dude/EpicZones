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

import com.randomappdev.EpicZones.modules.core.commands.EZZoneHelp.ZoneCommand;
import com.randomappdev.EpicZones.modules.core.objects.EpicZone;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer;
import com.randomappdev.EpicZones.utilities.Globals;
import com.randomappdev.EpicZones.utilities.Messaging;
import com.randomappdev.EpicZones.utilities.Messaging.Message_ID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EZZoneList
{
    public EZZoneList(CommandSender sender)
    {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            EpicZonePlayer ezp = Globals.getPlayer(player.getName());
            boolean sentMessage = false;
            for (String zoneTag : Globals.myZones.keySet())
            {
                EpicZone zone = Globals.myZones.get(zoneTag);
                String messageText = "";
                if (ezp.getAdmin() || zone.isOwner(ezp.getName()))
                {
                    messageText = Messaging.get(Message_ID.Format_KeyValue, new String[]{zone.getName(), zone.getTag()});
                    if (zone.hasChildren())
                    {
                        messageText = messageText + " " + Messaging.get(Message_ID.Info_00123_Zone_Children, new String[]{zone.getChildren().size() + ""});
                    }
                    if (zone.hasParent())
                    {
                        messageText = messageText + " " + Messaging.get(Message_ID.Info_00126_Zone_Parent, new String[]{zone.getParent().getTag()});
                    }
                }
                if (messageText.length() > 0)
                {
                    Messaging.Send(sender, messageText);
                    sentMessage = true;
                }
            }
            if (!sentMessage)
            {
                if (ezp.getAdmin())
                {
                    Messaging.Send(sender, Message_ID.Warning_00023_NoZones);
                } else
                {
                    Messaging.Send(sender, Message_ID.Warning_00024_NoZones_Owner);
                }
            }
        } else
        {
            new EZZoneHelp(ZoneCommand.LIST, sender, null);
        }
    }
}
