/*

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

package com.randomappdev.EpicZones.integration;

import com.randomappdev.EpicZones.Log;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PermissionsManager
{

    private static Permission permission = null;

    public static void Init(Permission thePermission)
    {

        permission = thePermission;

        if (permission == null)
        {
            Log.Write("Vault not detected. Using Bukkit Permissions for permission management.");
        }

    }

    public static boolean hasPermission(Player player, String perm)
    {
        boolean result = false;
        try
        {
            if (permission != null)
            {
                permission.has(player, perm);
            } else
            {
                result = player.hasPermission(perm);
            }
        } catch (Exception e)
        {
            Log.Write(e.getMessage());
        }
        return result;
    }

    public static String[] getGroupNames(Player player)
    {
        String[] result;
        if (permission != null)
        {
            result = permission.getPlayerGroups(player);
        } else
        {
            ArrayList<String> temp = new ArrayList<String>();
            if (player.isOp())
            {
                temp.add("op");
            }
            temp.add("default");
            result = temp.toArray(new String[temp.size() - 1]);
        }
        return result;
    }

}