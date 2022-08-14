package parallelmc.ctf.classes;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ChemistClass extends CTFClass {

    private int energy = 0;

    public ChemistClass(Player player) {
        super(player);
        this.name = "Chemist";
        ItemStack chestplate = new ItemStack(Material.GOLDEN_CHESTPLATE);
        chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        ItemStack leggings = new ItemStack(Material.GOLDEN_LEGGINGS);
        leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        this.armor = new ItemStack[] {
                new ItemStack(Material.LEATHER_BOOTS),
                leggings,
                chestplate,
                new ItemStack(Material.LEATHER_HELMET)
        };
        for (ItemStack i : armor) {
            ItemMeta ameta = i.getItemMeta();
            ameta.setUnbreakable(true);
            i.setItemMeta(ameta);
        }
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setUnbreakable(true);
        meta.displayName(Component.text("§fChemist Sword"));
        sword.setItemMeta(meta);

        ItemStack damage = new ItemStack(Material.SPLASH_POTION, 12);
        PotionMeta pmeta = (PotionMeta)damage.getItemMeta();
        pmeta.setColor(PotionEffectType.HARM.getColor());
        pmeta.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 1, 1), true);
        pmeta.displayName(Component.text("§fInstant Damage II"));
        damage.setItemMeta(pmeta);

        ItemStack poison = new ItemStack(Material.SPLASH_POTION, 8);
        pmeta = (PotionMeta)poison.getItemMeta();
        pmeta.setColor(PotionEffectType.POISON.getColor());
        pmeta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 100, 1), true);
        pmeta.displayName(Component.text("§fPoison II"));
        poison.setItemMeta(pmeta);

        ItemStack health = new ItemStack(Material.SPLASH_POTION, 5);
        pmeta = (PotionMeta)health.getItemMeta();
        pmeta.setColor(PotionEffectType.HEAL.getColor());
        pmeta.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 1, 2), true);
        pmeta.displayName(Component.text("§fInstant Health III"));
        health.setItemMeta(pmeta);

        ItemStack regen = new ItemStack(Material.SPLASH_POTION, 5);
        pmeta = (PotionMeta)regen.getItemMeta();
        pmeta.setColor(PotionEffectType.REGENERATION.getColor());
        pmeta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 320, 2), true);
        pmeta.displayName(Component.text("§fRegeneration III"));
        regen.setItemMeta(pmeta);

        ItemStack str = new ItemStack(Material.SPLASH_POTION, 3);
        pmeta = (PotionMeta)str.getItemMeta();
        pmeta.setColor(PotionEffectType.INCREASE_DAMAGE.getColor());
        pmeta.addCustomEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 3600, 0), true);
        pmeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 3600, 0), true);
        pmeta.displayName(Component.text("§fStrength + Speed"));
        str.setItemMeta(pmeta);

        ItemStack fire = new ItemStack(Material.SPLASH_POTION, 5);
        pmeta = (PotionMeta)fire.getItemMeta();
        pmeta.setColor(PotionEffectType.FIRE_RESISTANCE.getColor());
        pmeta.addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 600, 0), true);
        pmeta.displayName(Component.text("§fFire Resistance"));
        fire.setItemMeta(pmeta);

        ItemStack jump = new ItemStack(Material.SPLASH_POTION, 3);
        pmeta = (PotionMeta)jump.getItemMeta();
        pmeta.setColor(PotionEffectType.JUMP.getColor());
        pmeta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, 80, 1), true);
        pmeta.displayName(Component.text("§fJump Boost II"));
        jump.setItemMeta(pmeta);

        this.hotbar = new ItemStack[] {
          sword,
          damage,
          poison,
          health,
          regen,
          str,
          fire,
          jump
        };
        initRunnable();
        this.runnableTicks = 20L;
    }

    public int getEnergy() {
        return energy;
    }

    public void addCooldown(int amount) {
        if (energy <= 0) return;
        energy -= amount;
        player.setExp(energy / 7f);
    }

    public int getThrowCost(PotionEffectType p) {
        switch (p.getName()) {
            case "HARM", "HEAL" -> { return 3; }
            case "REGENERATION", "POISON" -> { return 2; }
            default -> { return 1; }
        }
    }

    public void initRunnable() {
        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (energy < 7) {
                    energy++;
                    player.setExp(energy / 7f);
                }
            }
        };
    }
}
