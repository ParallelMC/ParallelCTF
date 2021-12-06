package parallelmc.ctf.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import parallelmc.ctf.ArrowShot;
import parallelmc.ctf.ParallelCTF;

public class OnBowShoot implements Listener {
    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player player) {
            // bow must be fully pulled back for it to be eligible for a snipe or explosion
            if (event.getForce() == 1.0f) {
                ParallelCTF.gameManager.addNewShot(event.getProjectile(), new ArrowShot(player, player.getLocation()));
            }
        }
    }
}
