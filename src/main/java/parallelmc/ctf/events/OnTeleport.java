package parallelmc.ctf.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class OnTeleport implements Listener {
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Player player = event.getPlayer();
            event.setCancelled(true);
            // 2 hearts of damage instead of 2.5
            player.damage(4);
            player.teleport(event.getTo(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }
}
