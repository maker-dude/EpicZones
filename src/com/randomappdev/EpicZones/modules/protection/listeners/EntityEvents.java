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

package com.randomappdev.EpicZones.modules.protection.listeners;

import com.randomappdev.EpicZones.modules.core.coreManager;
import com.randomappdev.EpicZones.modules.core.objects.EpicZone;
import com.randomappdev.EpicZones.modules.core.objects.EpicZonePlayer;
import com.randomappdev.EpicZones.utilities.Globals;
import com.randomappdev.EpicZones.utilities.Log;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import java.util.HashSet;

public class EntityEvents implements Listener
{

    private HashSet<SpawnReason> spawnReasonsToBlock = new HashSet<SpawnReason>();

    public EntityEvents()
    {
        spawnReasonsToBlock.add(SpawnReason.NATURAL);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event)
    {
        try
        {
            EpicZone zone = coreManager.getZoneForLocation(event.getLocation());
            if (zone != null)
            {
                if (event.getEntity() == null)
                {
                    if (!zone.getExplode().getOther())
                    {
                        event.setYield(0);
                        event.setCancelled(true);
                    }
                } else
                {
                    if (event.getEntity().getType() == EntityType.PRIMED_TNT)
                    {
                        if (!zone.getExplode().getTNT())
                        {
                            event.setYield(0);
                            event.setCancelled(true);
                        }
                    } else if (event.getEntity().getType() == EntityType.CREEPER)
                    {
                        if (!zone.getExplode().getCreeper())
                        {
                            event.setYield(0);
                            event.setCancelled(true);
                        }
                    } else if (event.getEntity().getType() == EntityType.FIREBALL)
                    {
                        if (!zone.getExplode().getGhast())
                        {
                            event.setYield(0);
                            event.setCancelled(true);
                        }
                    } else if (event.getEntity().getType() == EntityType.GHAST)
                    {
                        if (!zone.getExplode().getGhast())
                        {
                            event.setYield(0);
                            event.setCancelled(true);
                        }
                    } else
                    {
                        if (!zone.getExplode().getOther())
                        {
                            event.setYield(0);
                            event.setCancelled(true);
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            Log.write(e.getMessage());
        }
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event)
    {
        try
        {
            if (!event.isCancelled())
            {
                Entity e = event.getEntity();
                EpicZone zone = coreManager.getZoneForLocation(event.getEntity().getLocation());
                if (zone != null)
                {
                    if (!zone.getFire().getIgnite())
                    {
                        if (e.getType() == EntityType.PLAYER)
                        {
                            e.setFireTicks(0);
                            event.setCancelled(true);
                        } else if (!zone.getFireBurnsMobs())
                        {
                            e.setFireTicks(0);
                            event.setCancelled(true);
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            Log.write(e.getMessage());
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event)
    {
        try
        {
            if (!event.isCancelled())
            {
                Entity e = event.getEntity();
                EpicZone sancZone = coreManager.getZoneForLocation(e.getLocation());
                if ((sancZone != null && !sancZone.getSanctuary()) || sancZone == null)
                {
                    if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)
                    {
                        if (event instanceof EntityDamageByEntityEvent)
                        {
                            EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent) event;
                            if (sub.getEntity().getType() == EntityType.PLAYER && sub.getDamager().getType() == EntityType.PLAYER)
                            {
                                Player player = (Player) sub.getEntity();
                                EpicZonePlayer ezp = Globals.getPlayer(player.getName());
                                EpicZone zone = ezp.getCurrentZone();
                                if (zone != null)
                                {
                                    if (!zone.getPVP())
                                    {
                                        event.setCancelled(true);
                                    }
                                } else
                                {
                                    if (!Globals.myGlobalZones.get(e.getWorld().getName().toLowerCase()).getPVP())
                                    {
                                        event.setCancelled(true);
                                    }
                                }
                            } else if (sub.getDamager().getType() == EntityType.GHAST)
                            {
                                if (sancZone != null)
                                {
                                    if (!sancZone.getExplode().getGhast())
                                    {
                                        event.setCancelled(true);
                                    }
                                }
                            }
                        }
                    } else if (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)
                    {
                        if (sancZone != null)
                        {
                            if (!sancZone.getExplode().getTNT())
                            {
                                event.setCancelled(true);
                            }
                        }
                    } else if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
                    {
                        if (sancZone != null)
                        {
                            if (!sancZone.getExplode().getCreeper())
                            {
                                event.setCancelled(true);
                            }
                        }
                    } else if (event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK)
                    {
                        if (sancZone != null)
                        {
                            if (!sancZone.getFire().getIgnite())
                            {
                                if (e.getType() == EntityType.PLAYER)
                                {
                                    e.setFireTicks(0);
                                    event.setCancelled(true);
                                } else if (!sancZone.getFireBurnsMobs())
                                {
                                    e.setFireTicks(0);
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                } else //This is a sanctuary zone, no damage allowed to players.
                {
                    if (e.getType() == EntityType.PLAYER)
                    {
                        e.setFireTicks(0);
                        event.setCancelled(true);
                    }
                }
            }
        } catch (Exception e)
        {
            Log.write(e.getMessage());
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        try
        {
            if (!event.isCancelled() && spawnReasonsToBlock.contains(event.getSpawnReason()))
            {
                EpicZone zone = coreManager.getZoneForLocation(event.getLocation());
                if (zone != null)
                {
                    if (zone.getMobs() != null) //If null assume all
                    {
                        if (zone.getMobs().size() > 0) //If size = 0 assume all
                        {
                            if (!zone.getMobs().contains("ALL"))
                            {
                                if (zone.getMobs().contains("NONE") || !zone.getMobs().contains(event.getEntityType().toString()))
                                {
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            Log.write(e.getMessage());
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event)
    {
        try
        {
            if (!event.isCancelled())
            {
                if (event.getEntity().getType() == EntityType.ENDERMAN)
                {
                    EpicZone zone = coreManager.getZoneForLocation(event.getBlock().getLocation());
                    if (zone != null)
                    {
                        if (!zone.getAllowEndermenPick())
                        {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            Log.write(e.getMessage());
        }
    }

}
