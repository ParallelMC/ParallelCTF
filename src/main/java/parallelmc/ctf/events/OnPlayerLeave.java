package parallelmc.ctf.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import parallelmc.ctf.ParallelCTF;

public class OnPlayerLeave implements Listener {
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        ParallelCTF.gameManager.removePlayer(event.getPlayer());
    }
}
