package parallelmc.ctf.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import parallelmc.ctf.CTFPlayer;
import parallelmc.ctf.ParallelCTF;
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
                            victim.setHealth(0);
                        }
                    }
                }
            }
        }
    }
}
