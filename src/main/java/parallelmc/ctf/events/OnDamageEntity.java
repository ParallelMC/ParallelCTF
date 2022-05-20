package parallelmc.ctf.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import parallelmc.ctf.CTFPlayer;
import parallelmc.ctf.CTFTeam;
import parallelmc.ctf.ParallelCTF;
import parallelmc.ctf.classes.AssassinClass;
import parallelmc.ctf.classes.MedicClass;
import parallelmc.ctf.classes.PyroClass;

public class OnDamageEntity implements Listener {
    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player victim) {
            CTFPlayer pla = ParallelCTF.gameManager.getPlayer(attacker);
            CTFPlayer plv = ParallelCTF.gameManager.getPlayer(victim);
            if (pla.getTeam() == plv.getTeam()) {
                if (pla.getCtfClass() instanceof MedicClass) {
                    if (plv.isHealingOnCooldown()) {
                        ParallelCTF.sendMessageTo(attacker, victim.getName() + " is on healing cooldown!");
                        event.setCancelled(true);
                        return;
                    }
                    event.setCancelled(true);
                    plv.medicHeal();
                    ParallelCTF.sendMessageTo(victim, "You were healed by " + attacker.getName() + "! You can be healed again in 15 seconds.");
                    ParallelCTF.sendMessageTo(attacker, "You healed " + victim.getName() + "! They can be healed again in 15 seconds.");
                }
                else {
                    event.setCancelled(true);
                }
            }
            else {
                if (pla.getCtfClass() instanceof PyroClass) {
                    if (attacker.getInventory().getItemInMainHand().getType() == Material.DIAMOND_AXE) {
                        if (victim.getFireTicks() > 0) {
                            ParallelCTF.sendMessage((plv.getTeam() == CTFTeam.BLUE ? "§9" : "§c") + victim.getName() + " was axed by " + (pla.getTeam() == CTFTeam.BLUE ? "§9" : "§c") + attacker.getName());
                            event.setCancelled(true);
                            plv.kill();
                        }
                    }
                }
                if (pla.getCtfClass() instanceof AssassinClass assassin) {
                    if (assassin.isAssassinating()) {
                        attacker.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                        ParallelCTF.sendMessageTo(attacker, "Assassination kill! Strength II for 9 seconds.");
                        attacker.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 180, 1, true));
                    }
                }
                if (plv.getCtfClass() instanceof AssassinClass assassin) {
                    if (assassin.isAssassinating()) {
                        // assassins can be instakilled while assassinating
                        ParallelCTF.sendMessage((plv.getTeam() == CTFTeam.BLUE ? "§9" : "§c") + victim.getName() + " was assassinated by " + (pla.getTeam() == CTFTeam.BLUE ? "§9" : "§c") + attacker.getName());
                        event.setCancelled(true);
                        plv.kill();
                    }
                }
                if (victim.getHealth() - event.getDamage() <= 0D) {
                    ParallelCTF.sendMessage((plv.getTeam() == CTFTeam.BLUE ? "§9" : "§c") + victim.getName() + " was slain by " + (pla.getTeam() == CTFTeam.BLUE ? "§9" : "§c") + attacker.getName());
                    event.setCancelled(true);
                    plv.kill();
                }
            }
        }
    }
}
