package parallelmc.ctf.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class OnPlaceBlock implements Listener {
    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.FIRE && !event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }
}
