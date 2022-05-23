package parallelmc.ctf.classes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

// easier to make a special class for spectators
public class SpectatorClass extends CTFClass {
    public SpectatorClass(Player player) {
        super(player);
        this.name = "Spectator";
        this.armor = new ItemStack[] {
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR)
        };
        this.effects = new PotionEffect[] {
                new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, true, false),
                new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0, true, false),
                new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, false)
        };
        this.hotbar = new ItemStack[] {};
        this.runnable = null;
    }
}
