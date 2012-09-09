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
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer.EpicZoneMode;
import com.randomappdev.EpicZones.utilities.Globals;
import com.randomappdev.EpicZones.utilities.Messaging;
import com.randomappdev.EpicZones.utilities.Messaging.Message_ID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EZZoneName
{
    public EZZoneName(String[] data, CommandSender sender)
    {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            EpicZonePlayer ezp = Globals.getPlayer(player.getName());

            if (ezp.getMode() == EpicZoneMode.ZoneEdit)
            {
                if (data.length > 1)
                {
                    String message = "";
                    for (int i = 1; i < data.length; i++)
                    {
                        message = message + data[i] + " ";
                    }
                    if (message.length() > 0)
                    {
                        ezp.getEditZone().setName(message.trim());
                        Messaging.Send(sender, Message_ID.Info_00100_ZoneUpdatedSet_X_to_Y, new String[]{"name", message});
                    }
                }
            } else
            {
                new EZZoneHelp(ZoneCommand.NAME, sender, ezp);
            }
        }
    }
}
