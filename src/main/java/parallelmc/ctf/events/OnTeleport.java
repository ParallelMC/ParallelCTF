package parallelmc.ctf.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import parallelmc.ctf.CTFPlayer;
import parallelmc.ctf.KillReason;
import parallelmc.ctf.ParallelCTF;

public class OnTeleport implements Listener {
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Player player = event.getPlayer();
            event.setCancelled(true);
            if (player.getHealth() - 4D <= 0) {
                CTFPlayer pl = ParallelCTF.gameManager.getPlayer(player);
                ParallelCTF.sendMessage(pl.getColorFormatting() + player.getName() + " §adied from an Ender Pearl.");
                ParallelCTF.gameManager.getPlayer(player).kill(KillReason.SELF);
            } else {
                // 2 hearts of damage instead of 2.5
                player.damage(4D);
                player.teleport(event.getTo(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
        }
    }
}
