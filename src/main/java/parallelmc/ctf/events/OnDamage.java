package parallelmc.ctf.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import parallelmc.ctf.*;
import parallelmc.ctf.classes.PyroClass;
import parallelmc.ctf.classes.SoldierClass;

public class OnDamage implements Listener {
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (ParallelCTF.gameManager.gameState == GameState.PREGAME) {
                event.setCancelled(true);
                return;
            }
            CTFPlayer pl = ParallelCTF.gameManager.getPlayer(player);
            if (pl.getTeam() == CTFTeam.SPECTATOR) {
                event.setCancelled(true);
                return;
            }
            EntityDamageEvent.DamageCause cause = event.getCause();
            if (pl.getCtfClass() instanceof SoldierClass) {
                // soldiers cant take fall damage
                if (cause == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                    return;
                }
            }
            if (pl.getCtfClass() instanceof PyroClass) {
                if (cause == EntityDamageEvent.DamageCause.FIRE ||
                        cause == EntityDamageEvent.DamageCause.FIRE_TICK ||
                        cause == EntityDamageEvent.DamageCause.LAVA ||
                        cause == EntityDamageEvent.DamageCause.HOT_FLOOR) {
                    event.setDamage(event.getDamage() / 2);
                }
            }
            if (cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK &&
                    cause != EntityDamageEvent.DamageCause.PROJECTILE &&
                    player.getHealth() - event.getFinalDamage() <= 0D) {
                event.setCancelled(true);
                ParallelCTF.sendMessage(pl.getColorFormatting() + player.getName() + " §adied to " + event.getCause());
                pl.kill(KillReason.OTHER);
            }
        }
    }
}
