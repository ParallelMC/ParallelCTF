package parallelmc.ctf.classes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import parallelmc.ctf.CTFTeam;
import parallelmc.ctf.ParallelCTF;

import java.util.ArrayList;


public class NinjaClass extends CTFClass {
    public NinjaClass() {
        super("Ninja");
    }

    private boolean isInvisible = false;
    private boolean eggCooldown = false;

    public void registerKit() {
        this.armor = new ItemStack[] {
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR)
        };
        ItemStack pearls = new ItemStack(Material.ENDER_PEARL, 10);
        ItemStack eggs = new ItemStack(Material.EGG, 10);
        ItemStack dust = new ItemStack(Material.REDSTONE, 64);
        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setUnbreakable(true);
        meta.addEnchant(Enchantment.DAMAGE_ALL, 6, true);
        meta.displayName(Component.text("§fNinja Sword"));
        ArrayList<Component> lore = new ArrayList<>();
        lore.add(Component.text("Sneak to slowly regain health!", NamedTextColor.GRAY));
        meta.lore(lore);
        sword.setItemMeta(meta);
        ItemMeta emeta = eggs.getItemMeta();
        emeta.displayName(Component.text("§fSmoke Bomb"));
        eggs.setItemMeta(emeta);
        ItemMeta dmeta = dust.getItemMeta();
        dmeta.displayName(Component.text("§fInvisibility Dust"));
        dust.setItemMeta(dmeta);
        this.hotbar = new ItemStack[] {
                sword,
                pearls,
                eggs,
                dust
        };
        this.effects = new PotionEffect[] {
                new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true)
        };
        initRunnable();
        this.runnableTicks = 10L;
    }

    public void initRunnable() {
        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (isInvisible) {
                    ItemStack redstone = player.getInventory().getItemInMainHand();
                    // sneaking = 2 redstone per second
                    if (player.isSneaking()) {
                        redstone.subtract();
                    }
                    // sprinting = 12 redstone per second
                    else if (player.isSprinting()) {
                        redstone.subtract(6);
                    }
                    // walking = 4 redstone per second
                    else {
                        redstone.subtract(2);
                    }
                }
                else if (player.isSneaking()) {
                    if (player.getInventory().getItemInMainHand().getType() == Material.GOLDEN_SWORD) {
                        double health = player.getHealth();
                        // heals a half a heart every second
                        if (health < 20D) {
                            player.setHealth(health + 0.25D);
                        }
                    }
                }
            }
        };
    }

    public boolean isEggOnCooldown() { return eggCooldown; }

    public void setEggCooldown() {
        this.eggCooldown = true;
        Bukkit.getScheduler().runTaskLater(ParallelCTF.gameManager.getPlugin(), () -> {
            this.eggCooldown = false;
        }, 20L);
    }

    public boolean isInvisible() { return isInvisible; }

    public void goInvisibleTo(Player playerToHide, CTFTeam team) {
        this.isInvisible = true;
        Plugin pl = ParallelCTF.gameManager.getPlugin();
        ParallelCTF.gameManager.players.forEach((p, c) -> {
            if (p == playerToHide) return;
            if (c.getTeam() == team)
                p.hidePlayer(pl, playerToHide);
        });
        ParallelCTF.sendMessageTo(playerToHide, "You are now invisible!");
    }

    public void goVisibleTo(Player playerToUnhide, CTFTeam team) {
        this.isInvisible = false;
        Plugin pl = ParallelCTF.gameManager.getPlugin();
        ParallelCTF.gameManager.players.forEach((p, c) -> {
            if (p == playerToUnhide) return;
            if (c.getTeam() == team)
                p.showPlayer(pl, playerToUnhide);
        });
        ParallelCTF.sendMessageTo(playerToUnhide, "You are no longer invisible!");
    }
}
