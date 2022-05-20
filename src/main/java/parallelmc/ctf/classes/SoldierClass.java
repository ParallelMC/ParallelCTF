package parallelmc.ctf.classes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import parallelmc.ctf.ParallelCTF;

import java.util.ArrayList;


public class SoldierClass extends CTFClass {
    public SoldierClass(Player player) {
        super(player);
        this.name = "Soldier";
        this.armor = new ItemStack[] {
                new ItemStack(Material.IRON_BOOTS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.IRON_HELMET)
        };
        for (ItemStack i : armor) {
            ItemMeta ameta = i.getItemMeta();
            ameta.setUnbreakable(true);
            i.setItemMeta(ameta);
        }
        ItemStack food = new ItemStack(Material.COOKED_BEEF, 4);
        ArrayList<Component> lore = new ArrayList<>();
        lore.add(Component.text("Right-click to restore health!", NamedTextColor.GRAY));
        food.lore(lore);
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setUnbreakable(true);
        lore.clear();
        lore.add(Component.text("Right-click to climb walls!", NamedTextColor.GRAY));
        meta.displayName(Component.text("§fWall Climbing Sword"));
        sword.setItemMeta(meta);
        this.hotbar = new ItemStack[] {
                sword,
                food
        };
        this.runnable = null;
    }

    private boolean wallClimbCooldown = false;


    public void setWallClimbOnCooldown() {
        this.wallClimbCooldown = true;
        Bukkit.getScheduler().runTaskLater(ParallelCTF.gameManager.getPlugin(), () -> {
            this.wallClimbCooldown = false;
        }, 20L);
    }

    public boolean isWallClimbOnCooldown() { return wallClimbCooldown; }
}
