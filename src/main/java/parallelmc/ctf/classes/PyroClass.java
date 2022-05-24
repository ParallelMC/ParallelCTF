package parallelmc.ctf.classes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;


public class PyroClass extends CTFClass {
    public PyroClass(Player player) {
        super(player);
        this.name = "Pyro";
        this.armor = new ItemStack[] {
                new ItemStack(Material.LEATHER_BOOTS),
                new ItemStack(Material.LEATHER_LEGGINGS),
                new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                new ItemStack(Material.LEATHER_HELMET)
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
        ItemStack axe = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta meta = axe.getItemMeta();
        meta.setUnbreakable(true);
        meta.displayName(Component.text("§fPyro Axe"));
        lore.clear();
        lore.add(Component.text("Hit on-fire players for an instakill!", NamedTextColor.GRAY));
        axe.setItemMeta(meta);
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bmeta = bow.getItemMeta();
        bmeta.displayName(Component.text("§fExplosive Bow"));
        lore.clear();
        lore.add(Component.text("Shoot the floor for a firey explosion!", NamedTextColor.GRAY));
        bmeta.lore(lore);
        bmeta.setUnbreakable(true);
        bow.setItemMeta(bmeta);
        ItemStack flint = new ItemStack(Material.FLINT_AND_STEEL);
        Damageable fmeta = (Damageable)flint.getItemMeta();
        fmeta.setDamage(40);
        flint.setItemMeta(fmeta);
        this.hotbar = new ItemStack[] {
                axe,
                food,
                bow,
                new ItemStack(Material.ARROW, 25),
                flint
        };
    }
}
