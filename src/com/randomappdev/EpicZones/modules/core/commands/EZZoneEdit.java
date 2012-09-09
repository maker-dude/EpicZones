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
import com.randomappdev.EpicZones.modules.core.objects.EpicZone.ZoneType;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer.EpicZoneMode;
import com.randomappdev.EpicZones.utilities.Globals;
import com.randomappdev.EpicZones.utilities.Messaging;
import com.randomappdev.EpicZones.utilities.Messaging.Message_ID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EZZoneEdit
{
    public EZZoneEdit(String[] data, CommandSender sender)
    {
        EpicZonePlayer ezp;
        if (sender instanceof Player)
        {
            ezp = Globals.getPlayer(sender.getName());
        } else
        {
            ezp = Globals.getPlayer("console");
        }
        if (ezp != null)
        {
            if (ezp.getMode() == EpicZoneMode.None)
            {
                if (data.length > 1)
                {
                    if (data[1].length() > 0)
                    {
                        if (Globals.myZones.get(data[1]) != null)
                        {
                            String tag = data[1].replaceAll("[^a-zA-Z0-9_]", "");

                            EpicZone zone = Globals.myZones.get(tag);
                            if (zone.getType() != ZoneType.GLOBAL)
                            {

                                if (ezp.getAdmin() || zone.isOwner(ezp.getName()))
                                {

                                    ezp.setEditZone(new EpicZone(zone));
                                    ezp.setMode(EpicZoneMode.ZoneEdit);
                                    ezp.getEditZone().ShowPillars();
                                    Messaging.Send(sender, Message_ID.Info_00105_Mode_Edit, new String[]{tag});

                                } else
                                {
                                    Messaging.Send(sender, Message_ID.Warning_00106_Perm_EditZone, new String[]{tag});
                                }
                            } else
                            {
                                Messaging.Send(sender, Message_ID.Warning_00022_CannotDeleteGlobalZones);
                            }
                        } else
                        {
                            new EZZoneCreate(data, sender);
                        }
                    }
                }
            } else
            {
                new EZZoneHelp(ZoneCommand.EDIT, sender, ezp);
            }
        }
    }
}
