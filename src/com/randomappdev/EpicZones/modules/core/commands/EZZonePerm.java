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
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePermission;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer.EpicZoneMode;
import com.randomappdev.EpicZones.utilities.Globals;
import com.randomappdev.EpicZones.utilities.Messaging;
import com.randomappdev.EpicZones.utilities.Messaging.Message_ID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class EZZonePerm
{
    public EZZonePerm(String[] data, CommandSender sender)
    {
        EpicZonePlayer ezp;
        if (sender instanceof Player)
        {
            ezp = Globals.getPlayer(sender.getName());
        } else
        {
            ezp = Globals.getPlayer("console");
        }
        if (ezp.getMode() == EpicZoneMode.ZoneEdit)
        {
            if (data.length > 3)
            {
                String member = data[1];
                String node = data[2];
                String perm = data[3];
                if (ValidNode(node))
                {
                    if (ValidPerm(perm))
                    {
                        if (node.equals("command"))
                        {
                            if (data.length > 4)
                            {
                                String command = data[4].toLowerCase();
                                if (perm.equals("allow"))
                                {
                                    ezp.getEditZone().getDisallowedCommands().remove(command);
                                    Messaging.Send(sender, Message_ID.Info_00131_CommandNotDenied, new String[]{command, ezp.getEditZone().getName()});
                                } else
                                {
                                    ezp.getEditZone().getDisallowedCommands().add(command);
                                    Messaging.Send(sender, Message_ID.Info_00132_CommandDenied, new String[]{command, ezp.getEditZone().getName()});
                                }
                            } else
                            {
                                new EZZoneHelp(ZoneCommand.PERM, sender, ezp);
                            }
                        } else
                        {
                            ezp.getEditZone().addPermission(member, node, perm);
                            Messaging.Send(sender, Message_ID.Info_00109_PermissionAdded, new String[]{member, node, perm});
                        }
                    } else
                    {
                        Messaging.Send(sender, Message_ID.Warning_00110_InvalidPermissionType, new String[]{perm});
                    }
                } else
                {
                    Messaging.Send(sender, Message_ID.Warning_00111_InvalidPermissionNode, new String[]{node});
                }
            } else if (data.length > 2)
            {
                String cmd = data[1];
                String tag = data[2];
                if (cmd.equalsIgnoreCase("copy"))
                {
                    EpicZone srcZone = Globals.myZones.get(tag);
                    if (srcZone != null)
                    {
                        for (String permTag : srcZone.getPermissions().keySet())
                        {
                            EpicZonePermission perm = srcZone.getPermissions().get(permTag);
                            ezp.getEditZone().addPermission(perm.getMember(), perm.getNode().toString(), perm.getPermission().toString());
                        }
                        Messaging.Send(sender, Message_ID.Info_00128_CopiedPermissions, new String[]{tag});
                    } else
                    {
                        Messaging.Send(sender, Message_ID.Warning_00117_Zone_X_DoesNotExist, new String[]{tag});
                    }
                } else if (cmd.equalsIgnoreCase("clear"))
                {
                    ArrayList<EpicZonePermission> remPerms = new ArrayList<EpicZonePermission>();
                    for (String permTag : ezp.getEditZone().getPermissions().keySet())
                    {
                        EpicZonePermission perm = ezp.getEditZone().getPermissions().get(permTag);
                        if (perm.getMember().equalsIgnoreCase(tag))
                        {
                            remPerms.add(perm);
                        }
                    }
                    for (EpicZonePermission perm : remPerms)
                    {
                        ezp.getEditZone().removePermission(perm.getMember(), perm.getNode().toString(), perm.getPermission().toString());
                    }
                    Messaging.Send(sender, Message_ID.Info_00129_PermissionsClearedFor_X, new String[]{tag});
                }
            } else if (data.length > 1)
            {
                String cmd = data[1];
                if (cmd.equalsIgnoreCase("clear"))
                {
                    ezp.getEditZone().setPermissions(new HashMap<String, EpicZonePermission>());
                }
                Messaging.Send(sender, Message_ID.Info_00040_PermissionsCleared);
            } else
            {
                new EZZoneHelp(ZoneCommand.PERM, sender, ezp);
            }
        } else
        {
            new EZZoneHelp(ZoneCommand.PERM, sender, ezp);
        }

    }

    private static boolean ValidNode(String node)
    {
        return node.equals("build") || node.equals("destroy") || node.equals("entry") || node.equals("command");
    }

    private static boolean ValidPerm(String perm)
    {
        return perm.equals("allow") || perm.equals("deny");
    }
}
