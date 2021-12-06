package parallelmc.ctf.events;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import parallelmc.ctf.ParallelCTF;

public class OnPlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
       AttributeInstance instance = event.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED);
       if (instance != null) {
           instance.setBaseValue(15.9);
       }
        ParallelCTF.gameManager.addPlayer(event.getPlayer());
    }
}
