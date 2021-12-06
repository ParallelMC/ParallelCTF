package parallelmc.ctf.events;

import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import parallelmc.ctf.CTFPlayer;
import parallelmc.ctf.ParallelCTF;
import parallelmc.ctf.classes.NinjaClass;

public class OnProjectileThrown implements Listener {
    @EventHandler
    public void onProjectileThrown(ProjectileLaunchEvent event) {
        ProjectileSource thrower = event.getEntity().getShooter();
        if (thrower instanceof Player player) {
            if (event.getEntity() instanceof Egg) {
                CTFPlayer pl = ParallelCTF.gameManager.getPlayer(player);
                if (pl.getCtfClass() instanceof NinjaClass ninja) {
                    if (ninja.isEggOnCooldown()) {
                        if (ninja.isEggOnCooldown()) {
                            event.setCancelled(true);
                            ParallelCTF.sendMessageTo(player, "Smoke Bomb is on cooldown!");
                        }
                    }
                    else {
                        ninja.setEggCooldown();
                    }
                }
            }
        }
    }
}
