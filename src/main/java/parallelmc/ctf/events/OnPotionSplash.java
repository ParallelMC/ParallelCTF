package parallelmc.ctf.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import parallelmc.ctf.CTFPlayer;
import parallelmc.ctf.ParallelCTF;
import parallelmc.ctf.classes.ChemistClass;
import parallelmc.ctf.classes.DwarfClass;

import java.util.Collection;

public class OnPotionSplash implements Listener {
    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        if (event.getEntity().getShooter() instanceof Player player) {
            CTFPlayer cl = ParallelCTF.gameManager.getPlayer(player);
            Collection<PotionEffect> effects = event.getPotion().getEffects();
            if (cl.getCtfClass() instanceof ChemistClass) {
                boolean harmful = effects.stream().anyMatch(x -> x.getType() == PotionEffectType.HARM) || effects.stream().anyMatch(x -> x.getType() == PotionEffectType.POISON);
                for (LivingEntity e : event.getAffectedEntities()) {
                    if (e instanceof Player p) {
                        CTFPlayer c = ParallelCTF.gameManager.getPlayer(p);
                        if (harmful && c.getTeam() == cl.getTeam() || !harmful && c.getTeam() != cl.getTeam())
                            event.setIntensity(p, 0D);
                        // lvl 10 dwarves are immune to negative effects
                        if (harmful && c.getCtfClass() instanceof DwarfClass dwarf && dwarf.getLevel() == 10) {
                            event.setIntensity(p, 0D);
                        }
                    }
                }
            }
        }
    }
}
