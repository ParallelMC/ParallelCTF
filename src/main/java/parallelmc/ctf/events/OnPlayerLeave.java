package parallelmc.ctf.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import parallelmc.ctf.CTFPlayer;
import parallelmc.ctf.ParallelCTF;

public class OnPlayerLeave implements Listener {
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        CTFPlayer pl = ParallelCTF.gameManager.getPlayer(event.getPlayer());
        if (ParallelCTF.gameManager.getRedFlagCarrier() == pl) {
            ParallelCTF.gameManager.ctfMap.resetRedFlag();
        }
        if (ParallelCTF.gameManager.getBlueFlagCarrier() == pl) {
            ParallelCTF.gameManager.ctfMap.resetBlueFlag();
        }
        ParallelCTF.gameManager.removePlayer(event.getPlayer());
    }
}
