package parallelmc.ctf.classes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;


public class TankClass extends CTFClass {
    public TankClass() {
        super("Tank");
    }

    public void registerKit() {
        this.armor = new ItemStack[] {
                new ItemStack(Material.DIAMOND_BOOTS),
                new ItemStack(Material.DIAMOND_LEGGINGS),
                new ItemStack(Material.DIAMOND_CHESTPLATE),
                new ItemStack(Material.DIAMOND_HELMET)
        };
        for (ItemStack i : armor) {
            ItemMeta ameta = i.getItemMeta();
            ameta.setUnbreakable(true);
            i.setItemMeta(ameta);
        }
        ItemStack food = new ItemStack(Material.COOKED_BEEF, 3);
        ArrayList<Component> lore = new ArrayList<>();
        lore.add(Component.text("Right-click to restore health!"));
        food.lore(lore);
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setUnbreakable(true);
        meta.displayName(Component.text("§fTank Sword"));
        sword.setItemMeta(meta);
        this.hotbar = new ItemStack[] {
                sword,
                food
        };
        this.runnable = null;
    }
}
