package parallelmc.ctf.events;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import parallelmc.ctf.CTFPlayer;
import parallelmc.ctf.ParallelCTF;
import parallelmc.ctf.classes.DwarfClass;

import java.util.Map;

public class OnChangeHeldItem implements Listener {
    @EventHandler
    public void onChangeHeldItem(PlayerItemHeldEvent event) {
        CTFPlayer player = ParallelCTF.gameManager.getPlayer(event.getPlayer());
        if (player.getCtfClass() instanceof DwarfClass dwarf) {
            ItemStack held = event.getPlayer().getInventory().getItem(event.getNewSlot());
            if (held != null) {
                // redo enchantments when switching to the sword to prevent a lvl 0 dwarf having a lvl 10 sword, for instance
                // looping through the dwarf's inventory each level when not held would be tedious imo
                if (held.getType() == Material.NETHERITE_SWORD) {
                    int level = dwarf.getLevel();
                    held.getEnchantments().forEach((e, l) -> {
                        held.removeEnchantment(e);
                    });
                    Map<Enchantment, Integer> enchantments = Map.ofEntries(
                            Map.entry(Enchantment.DAMAGE_ALL, level),
                            Map.entry(Enchantment.KNOCKBACK, (int) Math.floor(level / 3f)),
                            Map.entry(Enchantment.FIRE_ASPECT, (int) Math.floor(level / 5f))
                    );
                    // adding them unsafely makes things quicker and easier
                    // small side effect is lvl 0 shows as "enchantment.level.0"
                    // but I think its good to see the possible enchantments at lvl 0
                    held.addUnsafeEnchantments(enchantments);
                }
            }
        }
    }
}
