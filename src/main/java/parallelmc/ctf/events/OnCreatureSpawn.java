package parallelmc.ctf.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class OnCreatureSpawn implements Listener {
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
        if (reason == CreatureSpawnEvent.SpawnReason.EGG || reason == CreatureSpawnEvent.SpawnReason.ENDER_PEARL) {
            event.setCancelled(true);
        }
    }
}
