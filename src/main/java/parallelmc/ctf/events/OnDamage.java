package parallelmc.ctf.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import parallelmc.ctf.CTFPlayer;
import parallelmc.ctf.CTFTeam;
import parallelmc.ctf.ParallelCTF;
import parallelmc.ctf.classes.SoldierClass;

public class OnDamage implements Listener {
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            CTFPlayer pl = ParallelCTF.gameManager.getPlayer(player);
            if (pl.getCtfClass() instanceof SoldierClass) {
                // soldiers cant take fall damage
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                }
            }
            if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK &&
                    player.getHealth() - event.getDamage() <= 0D) {
                event.setCancelled(true);
                ParallelCTF.sendMessage((pl.getTeam() == CTFTeam.BLUE ? "§9" : "§c") + player.getName() + " died to " + event.getCause());
                pl.kill();
            }
        }
    }
}
