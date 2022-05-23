package parallelmc.ctf.events;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import parallelmc.ctf.ArrowShot;
import parallelmc.ctf.CTFPlayer;
import parallelmc.ctf.ParallelCTF;
import parallelmc.ctf.classes.*;

import java.util.ArrayList;
import java.util.Collection;

public class OnProjectileHit implements Listener {
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Entity hitEntity = event.getHitEntity();
        Block hitBlock = event.getHitBlock();
        Entity projectile = event.getEntity();
        if (hitEntity instanceof Player hitPlayer) {
            if (projectile instanceof Arrow arrow) {
                ArrowShot shot = ParallelCTF.gameManager.getShot(projectile);
                CTFPlayer hpl = ParallelCTF.gameManager.getPlayer(hitPlayer);
                if (shot != null) {
                    CTFPlayer pl = ParallelCTF.gameManager.getPlayer(shot.shooter());
                    if (pl.getCtfClass() instanceof ArcherClass) {
                        // arrow 30+ blocks away is snipe
                        double dist = shot.shotLocation().distance(hitEntity.getLocation());
                        if (dist > 30D) {
                            hpl.kill();
                            ParallelCTF.sendMessageTo(hitPlayer, "You were sniped by " + shot.shooter().getName() + " from " + Math.round(dist) + " blocks away!");
                            ParallelCTF.sendMessageTo(shot.shooter(), "You sniped " + hitPlayer.getName() + " from " + Math.round(dist) + " blocks away!");
                            ParallelCTF.sendMessage((hpl.getColorFormatting() + hitPlayer.getName() + " §awas sniped by " + pl.getColorFormatting() + shot.shooter().getName()));
                        }
                    }
                    // remove the shot either way
                    ParallelCTF.gameManager.removeShot(projectile);
                    projectile.remove();

                }
                if (hitPlayer.getHealth() - arrow.getDamage() <= 0D) {
                    CTFPlayer pl = ParallelCTF.gameManager.getPlayer((Player)arrow.getShooter());
                    event.setCancelled(true);
                    hpl.kill();
                    ParallelCTF.sendMessage(hpl.getColorFormatting() + hitPlayer.getName() + " §awas shot by " + pl.getColorFormatting() + pl.getMcPlayer().getName());
                }
            }
            else if (projectile instanceof Egg) {
                hitPlayer.getWorld().playSound(hitEntity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
                hitPlayer.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, hitPlayer.getLocation(), 1);
                ArrayList<PotionEffect> effects = new ArrayList<>();
                effects.add(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0, true));
                effects.add(new PotionEffect(PotionEffectType.SLOW, 60, 0, true));
                hitPlayer.addPotionEffects(effects);
            }
            else if (projectile instanceof EnderPearl) {
                // cast-o-rama
                CTFPlayer pl = ParallelCTF.gameManager.getPlayer((Player)((EnderPearl)projectile).getShooter());
                if (pl != null && pl.getCtfClass() instanceof NinjaClass ninja) {
                    ninja.thrownPearls.remove(projectile);
                }
            }
        }
        if (hitBlock != null) {
            if (projectile instanceof Arrow) {
                ArrowShot shot = ParallelCTF.gameManager.getShot(projectile);
                if (shot != null) {
                    CTFPlayer pl = ParallelCTF.gameManager.getPlayer(shot.shooter());
                    if (pl.getCtfClass() instanceof PyroClass) {
                        hitBlock.getWorld().playSound(hitBlock.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
                        hitBlock.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, hitBlock.getLocation(), 1);
                        Collection<Entity> nearby = hitBlock.getWorld().getNearbyEntities(hitBlock.getLocation(), 3, 3, 3);
                        for (Entity e : nearby) {
                            if (e instanceof Player explosionHit) {
                                CTFPlayer eh = ParallelCTF.gameManager.getPlayer(explosionHit);
                                if (eh.getCtfClass() instanceof DwarfClass dwarf) {
                                    // level 10 dwarves are immune
                                    if (dwarf.getLevel() == 10) return;
                                }
                                // medics cannot be set on fire
                                if (eh.getTeam() != pl.getTeam() && !(eh.getCtfClass() instanceof MedicClass)) {
                                    explosionHit.setFireTicks(70);
                                }
                            }
                        }
                        projectile.remove();
                    }
                    ParallelCTF.gameManager.removeShot(projectile);
                }
            }
            else if (projectile instanceof Snowball) {
                BlockFace hitFace = event.getHitBlockFace();
                if (hitFace != null && hitFace.isCartesian()) {
                    // cast-o-rama
                    CTFPlayer pl = ParallelCTF.gameManager.getPlayer((Player)((Snowball) projectile).getShooter());
                    if (pl.getCtfClass() instanceof MedicClass medic) {
                        Block next = hitBlock.getRelative(hitFace);
                        next.setType(Material.COBWEB);
                        medic.placedWebs.add(next);
                    }

                }
            }
            else if (projectile instanceof Egg) {
                // still play if it hits a block just for the effect
                hitBlock.getWorld().playSound(hitBlock.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
                hitBlock.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, hitBlock.getLocation(), 5);
            }
            else if (projectile instanceof EnderPearl) {
                // cast-o-rama
                CTFPlayer pl = ParallelCTF.gameManager.getPlayer((Player)((EnderPearl)projectile).getShooter());
                if (pl != null && pl.getCtfClass() instanceof NinjaClass ninja) {
                    ninja.thrownPearls.remove(projectile);
                }
            }
        }
    }
}
