package parallelmc.ctf.events;

import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;
import parallelmc.ctf.CTFPlayer;
import parallelmc.ctf.ParallelCTF;
import parallelmc.ctf.classes.ChemistClass;
import parallelmc.ctf.classes.NinjaClass;

public class OnProjectileThrown implements Listener {
    @EventHandler
    public void onProjectileThrown(ProjectileLaunchEvent event) {
        ProjectileSource thrower = event.getEntity().getShooter();
        if (thrower instanceof Player player) {
            CTFPlayer pl = ParallelCTF.gameManager.getPlayer(player);
            if (event.getEntity() instanceof Egg) {
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
            if (event.getEntity() instanceof EnderPearl) {
                if (pl.getCtfClass() instanceof NinjaClass ninja) {
                    ninja.thrownPearls.add(event.getEntity());
                }
            }
            if (event.getEntity() instanceof ThrownPotion potion) {
                if (pl.getCtfClass() instanceof ChemistClass chemist) {
                    int cost = 0;
                    for (PotionEffect e : potion.getEffects()) {
                        cost += chemist.getThrowCost(e.getType());
                    }
                    if (chemist.getEnergy() < cost) {
                        event.setCancelled(true);
                        ParallelCTF.sendMessageTo(player, "You need more energy!");
                        return;
                    }
                    chemist.addCooldown(cost);
                }
            }
        }
    }
}
