package parallelmc.ctf.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import parallelmc.ctf.CTFPlayer;
import parallelmc.ctf.ParallelCTF;
import parallelmc.ctf.classes.NinjaClass;
import parallelmc.ctf.classes.SoldierClass;

public class OnRightClick implements Listener {
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        CTFPlayer pl = ParallelCTF.gameManager.players.get(player);
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (heldItem.getType() == Material.COOKED_BEEF) {
                double health = player.getHealth();
                if (health < 20D) {
                    player.setHealth(Math.min(health + 8D, 20D));
                    heldItem.subtract();
                }
            }
        }
        else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // lazy
            if (heldItem.getType() == Material.COOKED_BEEF) {
                double health = player.getHealth();
                if (health < 20D) {
                    player.setHealth(Math.min(health + 8D, 20D));
                    heldItem.subtract();
                }
            }
            if (pl.getCtfClass() instanceof SoldierClass soldier &&
                heldItem.getType() == Material.IRON_SWORD &&
                !soldier.isWallClimbOnCooldown()) {
                player.setVelocity(new Vector(0, 1, 0));
                soldier.setWallClimbOnCooldown();
            }
        }
    }
}
