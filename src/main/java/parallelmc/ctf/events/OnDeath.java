package parallelmc.ctf.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class OnDeath implements Listener {
    // TODO: teleport to respective team's spawn
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.getPlayer().spigot().respawn();
    }
}
