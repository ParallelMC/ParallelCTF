package parallelmc.ctf.events;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import parallelmc.ctf.CTFPlayer;
import parallelmc.ctf.ParallelCTF;
import parallelmc.ctf.classes.AssassinClass;
import parallelmc.ctf.classes.SoldierClass;

public class OnRightClick implements Listener {
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        CTFPlayer pl = ParallelCTF.gameManager.players.get(player);
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        Action action = event.getAction();
        if (heldItem.getType() == Material.COOKED_BEEF && (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR)) {
            double health = player.getHealth();
            if (health < 20D) {
                player.setHealth(Math.min(health + 8D, 20D));
                heldItem.subtract();
            }
        }
        if (pl.getCtfClass() instanceof SoldierClass soldier &&
                heldItem.getType() == Material.IRON_SWORD &&
                !soldier.isWallClimbOnCooldown() &&
                action == Action.RIGHT_CLICK_BLOCK) {
            player.setVelocity(new Vector(0, 0.6, 0));
            soldier.setWallClimbOnCooldown();
        }
        if (pl.getCtfClass() instanceof AssassinClass assassin &&
                (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR)) {
            if (heldItem.getType() == Material.SUGAR) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 120, 2, true));
                heldItem.subtract();
                ParallelCTF.sendMessageTo(player, "Speed boost active!");
               Bukkit.getScheduler().runTaskLater(ParallelCTF.gameManager.getPlugin(), () -> {
                    if (!player.getInventory().containsAtLeast(new ItemStack(Material.SUGAR), 2)) {
                        ItemStack sugar = new ItemStack(Material.SUGAR);
                        ItemMeta smeta = sugar.getItemMeta();
                        smeta.displayName(Component.text("§7Speed Boost"));
                        sugar.setItemMeta(smeta);
                        player.getInventory().addItem(sugar);
                    }
               }, 300L);
            }
            else if (heldItem.getType() == Material.REDSTONE) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 127, true));
                heldItem.subtract();
                ParallelCTF.sendMessageTo(player, "Assassinate active!");
                assassin.assassinate();
            }
        }
    }
}
