package parallelmc.ctf.classes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;


public class ArcherClass extends CTFClass {
    public ArcherClass(Player player) {
        super(player);
        this.name = "Archer";
        this.armor = new ItemStack[] {
                new ItemStack(Material.CHAINMAIL_BOOTS),
                new ItemStack(Material.CHAINMAIL_LEGGINGS),
                new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                new ItemStack(Material.CHAINMAIL_HELMET)
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
        ItemStack sword = new ItemStack(Material.STONE_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setUnbreakable(true);
        meta.displayName(Component.text("§fArcher Sword"));
        sword.setItemMeta(meta);
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bmeta = bow.getItemMeta();
        bmeta.displayName(Component.text("§fArcher Bow"));
        lore.clear();
        lore.add(Component.text("Hit enemies from afar to snipe them!", NamedTextColor.GRAY));
        bmeta.lore(lore);
        bmeta.addEnchant(Enchantment.ARROW_KNOCKBACK, 1, false);
        bmeta.setUnbreakable(true);
        bow.setItemMeta(bmeta);
        this.hotbar = new ItemStack[] {
                sword,
                food,
                bow,
                new ItemStack(Material.ARROW, 64),
                new ItemStack(Material.ARROW, 64)
        };
    }
}
