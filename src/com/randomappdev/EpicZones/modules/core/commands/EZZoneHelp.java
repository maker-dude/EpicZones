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

import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer.EpicZoneMode;
import com.randomappdev.EpicZones.utilities.Globals;
import com.randomappdev.EpicZones.utilities.Messaging;
import com.randomappdev.EpicZones.utilities.Messaging.Message_ID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EZZoneHelp
{

    public enum ZoneCommand
    {
        CANCEL, CEILING, CHILD, CONFIRM, CREATE, DELETE, DRAW, EDIT, FLAG, FLOOR, HELP, INFO, LIST, MESSAGE, NAME, NONE, OWNER, PARENT, PERM, RADIUS, SAVE, WORLD, COMMAND
    }

    public EZZoneHelp(ZoneCommand command, CommandSender sender, EpicZonePlayer ezp)
    {
        if (ezp == null)
        {
            if (sender instanceof Player)
            {
                ezp = Globals.myPlayers.get(((Player) sender).getName());
            }
        }
        if (ezp != null)
        {
            Messaging.Send(sender, Message_ID.Help_01000);
            Messaging.Send(sender, Message_ID.Info_00010_Parameter_Description);
            if (ezp.getMode() == EpicZoneMode.ZoneEdit)
            {
                Messaging.Send(sender, Message_ID.Mode_00011_Edit);
                Messaging.Send(sender, Message_ID.Help_01001_Name);
                Messaging.Send(sender, Message_ID.Help_01002_Flag);
                Messaging.Send(sender, Message_ID.Help_01003_Flag_PVP);
                Messaging.Send(sender, Message_ID.Help_01004_Flag_Fire);
                Messaging.Send(sender, Message_ID.Help_01005_Flag_Explode);
                Messaging.Send(sender, Message_ID.Help_01006_Flag_Mobs);
                Messaging.Send(sender, Message_ID.Help_01007_Flag_Regen);
                Messaging.Send(sender, Message_ID.Help_01008_Flag_Sanctuary);
                Messaging.Send(sender, Message_ID.Help_01009_Flag_FireBunsMobs);
                Messaging.Send(sender, Message_ID.Help_01010_Floor);
                Messaging.Send(sender, Message_ID.Help_01011_Ceiling);
                Messaging.Send(sender, Message_ID.Help_01012_Child);
                Messaging.Send(sender, Message_ID.Help_01013_Owner);
                Messaging.Send(sender, Message_ID.Help_01014_Message);
                Messaging.Send(sender, Message_ID.Help_01015_World);
                Messaging.Send(sender, Message_ID.Help_01016_Draw);
                Messaging.Send(sender, Message_ID.Help_01017_Cancel);
                Messaging.Send(sender, Message_ID.Help_01018_Edit_Delete);
                Messaging.Send(sender, Message_ID.Help_01019_Edit_Save);
            } else if (ezp.getMode() == EpicZoneMode.ZoneDraw)
            {
                Messaging.Send(sender, Message_ID.Mode_00012_Draw);
                Messaging.Send(sender, Message_ID.Help_01020_Draw_Save);
                Messaging.Send(sender, Message_ID.Help_01022_DrawConfirm_Cancel);
            } else if (ezp.getMode() == EpicZoneMode.ZoneDrawConfirm)
            {
                Messaging.Send(sender, Message_ID.Mode_00013_DrawConfirm);
                Messaging.Send(sender, Message_ID.Help_01021_DrawConfirm_Confirm);
                Messaging.Send(sender, Message_ID.Help_01022_DrawConfirm_Cancel);
            } else if (ezp.getMode() == EpicZoneMode.ZoneDeleteConfirm)
            {
                Messaging.Send(sender, Message_ID.Mode_00014_DeleteConfirm);
                Messaging.Send(sender, Message_ID.Help_01021_DrawConfirm_Confirm);
                Messaging.Send(sender, Message_ID.Help_01022_DrawConfirm_Cancel);
            } else
            {
                Messaging.Send(sender, Message_ID.Help_01024_Edit);
                Messaging.Send(sender, Message_ID.Help_01025_Create);
                Messaging.Send(sender, Message_ID.Help_01026_List);
                Messaging.Send(sender, Message_ID.Help_01027_Info);
            }
        } else
        {
            Messaging.Send(sender, Message_ID.Help_01026_List);
            Messaging.Send(sender, Message_ID.Help_01027_Info);
        }
    }
}