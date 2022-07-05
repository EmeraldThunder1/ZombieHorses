package com.emerald.thunder;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import org.bukkit.Difficulty;

import java.util.Random;

public class ZombieHorses extends JavaPlugin implements Listener{
    @Override
    public void onEnable () {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getScheduler().scheduleSyncRepeatingTask(
            this,
            new Runnable() {
                public void run() {
                    updateZombies();
                }
            }, 
            0L,
            1L);
    }

    public void updateZombies() {
        for (World world : getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Zombie) {
                    Zombie zombie = (Zombie) entity;
                    Horse closest = getClosest(world, zombie);
                    
                    if (closest != null) {
                        // Target should not be cleared, causes wierd behaviour
                        zombie.setTarget(closest);
                    }
                }
            }
        }
    }

    public Horse getClosest(World world, Entity e) {
        Horse horse = null;

        for (Entity entity: world.getEntities()) {
            if (entity instanceof Horse) {
                if (entity.getLocation().distance(e.getLocation()) <= 16D) {
                    if (horse == null) {
                        horse = (Horse)entity;
                    } else {
                        if (entity.getLocation().distance(e.getLocation()) < horse.getLocation().distance(e.getLocation())) {
                            horse = (Horse)entity;
                        }
                    }
                }
            }
        }

        return horse;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity e1 = (LivingEntity) event.getEntity();
        Event damageEvent = e1.getLastDamageCause();

        if (damageEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageEvent = (EntityDamageByEntityEvent)damageEvent;

            if (entityDamageEvent.getDamager() instanceof Zombie) {
                if (e1 instanceof Horse) {
                    Random random = new Random();

                    int bound = 0;

                    if (e1.getWorld().getDifficulty() == Difficulty.PEACEFUL) {
                        bound = -1;
                    } else if (e1.getWorld().getDifficulty() == Difficulty.EASY) {
                        bound = 4;
                    } else if (e1.getWorld().getDifficulty() == Difficulty.NORMAL) {
                        bound = 2;
                    } else if (e1.getWorld().getDifficulty() == Difficulty.HARD) {
                        bound = 1;
                    }

                    if (bound > 0) {
                        if (random.nextInt(1, bound + 1) == 1) {
                            Location location = e1.getLocation();
                        
                            e1.getWorld().spawnEntity(location, EntityType.ZOMBIE_HORSE);
                            e1.remove();
                        }
                    }
                }
            }
        }
    }
}
