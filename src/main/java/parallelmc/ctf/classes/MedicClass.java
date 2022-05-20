package parallelmc.ctf.classes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;


public class MedicClass extends CTFClass {
    public MedicClass(Player player) {
        super(player);
        this.name = "Medic";
        this.armor = new ItemStack[] {
                new ItemStack(Material.GOLDEN_BOOTS),
                new ItemStack(Material.GOLDEN_LEGGINGS),
                new ItemStack(Material.GOLDEN_CHESTPLATE),
                new ItemStack(Material.GOLDEN_HELMET)
        };
        for (ItemStack i : armor) {
            ItemMeta ameta = i.getItemMeta();
            ameta.setUnbreakable(true);
            i.setItemMeta(ameta);
        }
        ItemStack food = new ItemStack(Material.COOKED_BEEF, 6);
        ArrayList<Component> lore = new ArrayList<>();
        lore.add(Component.text("Right-click to restore health!", NamedTextColor.GRAY));
        food.lore(lore);
        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setUnbreakable(true);
        meta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
        meta.displayName(Component.text("§fMedic Sword"));
        lore.clear();
        lore.add(Component.text("Hit allies to heal them!", NamedTextColor.GRAY));
        sword.setItemMeta(meta);
        ItemStack snowball = new ItemStack(Material.SNOWBALL,3);
        ItemMeta smeta = snowball.getItemMeta();
        smeta.displayName(Component.text("§fWeb Ball"));
        snowball.setItemMeta(smeta);
        this.hotbar = new ItemStack[] {
                sword,
                food,
                snowball
        };
        this.effects = new PotionEffect[] {
                new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, true),
                new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0, true)
        };
        // TODO: possibly passive healing
        this.runnable = null;
    }
}
