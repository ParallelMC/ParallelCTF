package parallelmc.ctf.classes;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import parallelmc.ctf.ParallelCTF;

import java.util.ArrayList;
import java.util.Map;


public class DwarfClass extends CTFClass {
    public DwarfClass() {
        super("Dwarf");
    }

    private int level = 0;

    public int getLevel() {
        return this.level;
    }

    public void registerKit() {
        this.armor = new ItemStack[] {
                new ItemStack(Material.CHAINMAIL_BOOTS),
                new ItemStack(Material.DIAMOND_LEGGINGS),
                new ItemStack(Material.DIAMOND_CHESTPLATE),
                new ItemStack(Material.CHAINMAIL_HELMET)
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
        meta.displayName(Component.text("§fDwarf Sword"));
        lore.clear();
        lore.add(Component.text("Sneak and hold to gain enchantments!"));
        sword.setItemMeta(meta);
        this.hotbar = new ItemStack[] {
                sword,
                food
        };
        this.effects = new PotionEffect[] {
                new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1, true)
        };
        initRunnable();
        this.runnableTicks = 10L;
    }

    public void initRunnable() {
        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                // dwarves cant sprint
                player.setFoodLevel(6);
                if (player.isSneaking()) {
                    if (player.getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD && level < 10) {
                        player.giveExp(1);
                    }
                }
                else {
                    // uncrouched exp drains 3x as fast
                    player.giveExp(-3);
                }
                int previous = level;
                level = player.getLevel();
                // only re-enchant if there is a level difference
                if (previous != level) {
                    ItemStack sword = player.getInventory().getItemInMainHand();
                    // check again if they're actually holding the sword
                    if (sword.getType() == Material.DIAMOND_SWORD) {
                        sword.getEnchantments().forEach((e, l) -> {
                            sword.removeEnchantment(e);
                        });
                        Map<Enchantment, Integer> enchantments = Map.ofEntries(
                                Map.entry(Enchantment.DAMAGE_ALL, level),
                                Map.entry(Enchantment.KNOCKBACK, (int) Math.floor(level / 3f)),
                                Map.entry(Enchantment.FIRE_ASPECT, (int) Math.floor(level / 5f))
                        );
                        // adding them unsafely makes things quicker and easier
                        // small side effect is lvl 0 shows as "enchantment.level.0"
                        // but I think its good to see the possible enchantments at lvl 0
                        sword.addUnsafeEnchantments(enchantments);
                    }
                }
            }
        };
    }
}
