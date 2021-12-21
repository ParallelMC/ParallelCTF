package parallelmc.ctf.classes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import parallelmc.ctf.ParallelCTF;

import java.util.ArrayList;


public class AssassinClass extends CTFClass {
    // TODO: give assassin redstone when they get a non assassination kill
    public AssassinClass() {
        super("Assassin");
    }

    private boolean isAssassinating = false;

    public void registerKit() {
        this.armor = new ItemStack[] {
                new ItemStack(Material.GOLDEN_BOOTS),
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR)
        };
        ItemMeta ameta = armor[0].getItemMeta();
        ameta.setUnbreakable(true);
        armor[0].setItemMeta(ameta);
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ArrayList<Component> lore = new ArrayList<>();
        lore.add(Component.text("Assassinate a player to gain Strength II!", NamedTextColor.GRAY));
        ItemMeta meta = sword.getItemMeta();
        meta.setUnbreakable(true);
        meta.lore(lore);
        meta.displayName(Component.text("§fAssassin Sword"));
        sword.setItemMeta(meta);
        ItemStack redstone = new ItemStack(Material.REDSTONE);
        ItemMeta rmeta = redstone.getItemMeta();
        rmeta.displayName(Component.text("§7Assassinate"));
        redstone.setItemMeta(rmeta);
        ItemStack sugar = new ItemStack(Material.SUGAR, 2);
        ItemMeta smeta = sugar.getItemMeta();
        smeta.displayName(Component.text("§7Speed Boost"));
        sugar.setItemMeta(smeta);
        this.hotbar = new ItemStack[] {
                sword,
                redstone,
                sugar
        };
        this.runnable = null;
    }

    public void assassinate() {
        this.isAssassinating = true;
        this.player.getInventory().setHeldItemSlot(0);
        Bukkit.getScheduler().runTaskLater(ParallelCTF.gameManager.getPlugin(), () -> {
            this.isAssassinating = false;
            ParallelCTF.sendMessageTo(player, "Your assassination timer has ran out.");
        }, 40L);
        Bukkit.getScheduler().runTaskLater(ParallelCTF.gameManager.getPlugin(), () -> {
            if (!this.player.getInventory().contains(Material.REDSTONE)) {
                ItemStack redstone = new ItemStack(Material.REDSTONE);
                ItemMeta rmeta = redstone.getItemMeta();
                rmeta.displayName(Component.text("§7Assassinate"));
                redstone.setItemMeta(rmeta);
                this.player.getInventory().addItem(redstone);
            }
        }, 300L);
    }

    public boolean isAssassinating() { return this.isAssassinating; }
}
