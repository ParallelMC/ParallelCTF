package parallelmc.ctf.classes;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class CTFClass {
    public String name;
    public ItemStack[] hotbar;
    public ItemStack[] armor;
    public PotionEffect[] effects;
    public BukkitRunnable runnable;
    public long runnableTicks;
    protected final Player player;

    public CTFClass(Player player) { this.player = player; }

    public void giveClass() {
        player.getInventory().clear();
        player.setExp(0);
        player.setLevel(0);
        player.setFoodLevel(20);
        PlayerInventory inv = player.getInventory();
        for (ItemStack i : hotbar) {
            inv.addItem(i);
        }
        inv.setArmorContents(armor);
        for (PotionEffect p : player.getActivePotionEffects()) {
            player.removePotionEffect(p.getType());
        }
        if (effects != null) {
            for (PotionEffect e : effects) {
                player.addPotionEffect(e);
            }
        }
    }

    public void initRunnable() { }
}
