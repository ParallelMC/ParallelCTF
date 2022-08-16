package parallelmc.ctf;

import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import parallelmc.ctf.classes.CTFClass;
import parallelmc.ctf.classes.MedicClass;
import parallelmc.ctf.classes.NinjaClass;

import java.util.logging.Level;

public class CTFPlayer {
    private final Player player;
    private CTFClass ctfClass;
    private final FastBoard board;
    private CTFTeam team;
    private boolean healingCooldown;
    private BukkitTask runnable;

    public CTFPlayer(Player player, CTFTeam team) {
        this.player = player;
        this.team = team;
        this.ctfClass = null;
        this.board = new FastBoard(this.player);
        this.board.updateTitle("§lParallel§3§lCTF");
        this.healingCooldown = false;
        this.runnable = null;
    }

    public void setTeam(CTFTeam team) {
        this.team = team;
    }

    /***
     * Changes this player's class
     * @param name The name of the class to switch to
     */
    public void setClass(String name) {
        Class<? extends CTFClass> cl = ParallelCTF.classes.get(name);
        if (cl == null) {
            ParallelCTF.log(Level.SEVERE, "Unknown CTF class name " + name);
            return;
        }
        try {
            this.ctfClass = cl.getConstructor(Player.class).newInstance(player);
        } catch (NoSuchMethodException ex) {
            ParallelCTF.log(Level.SEVERE, "Missing constructor for class" + name);
        } catch (Exception e) {
            ParallelCTF.log(Level.SEVERE, "Failed to setClass for class " + name + ":\n" + e.getMessage());
        }

        if (this.runnable != null) {
            runnable.cancel();
            runnable = null;
        }
        if (ctfClass.runnable != null) {
            ctfClass.initRunnable();
            runnable = ctfClass.runnable.runTaskTimer(ParallelCTF.gameManager.getPlugin(), 1L, ctfClass.runnableTicks);
        }
        ctfClass.giveClass();
    }

    public void updateLobbyBoard(int curVotes, int neededVotes, String nextMap, int mapVotes) {
        this.board.updateLines(
                "",
                "§dVotes needed to start",
                "§6" + curVotes + "/" + neededVotes,
                "§7/votestart",
                "",
                "§dNext Map",
                "§6" + nextMap + " (" + mapVotes + " votes)",
                "§7/votemap"
        );
    }

    /***
     * Updates the player's scoreboard
     */
    public void updateBoard(int mins, int secs, int capsToWin, int redCaps, int blueCaps, String... flagLines) {
        this.board.updateLines(
                "",
                "Time Left  | " + mins + ":" + (secs < 10 ? "0" + secs : secs),
                "Your Team | " + (team == CTFTeam.RED ? "§cRed" : "§9Blue"),
                "",
                "§c§lRed",
                "§cCaptures | " + redCaps + "/" + capsToWin,
                flagLines[0],
                flagLines[1],
                "§9§lBlue",
                "§9Captures | " + blueCaps + "/" + capsToWin,
                flagLines[2],
                flagLines[3]
        );
    }

    /***
     * Ran when a medic heals another player
     * The player is put on healing cooldown for a few seconds
     */
    public void medicHeal() {
        ctfClass.giveClass();
        if (ParallelCTF.gameManager.getBlueFlagCarrier() == this) {
            ItemStack banner = new ItemStack(Material.BLUE_BANNER);
            player.getInventory().setItem(EquipmentSlot.HEAD, banner);
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, true, false));
        }
        if (ParallelCTF.gameManager.getRedFlagCarrier() == this) {
            ItemStack banner = new ItemStack(Material.RED_BANNER);
            player.getInventory().setItem(EquipmentSlot.HEAD, banner);
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, true, false));
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 4, true));
        healingCooldown = true;
        Bukkit.getScheduler().runTaskLater(ParallelCTF.gameManager.getPlugin(), () -> {
            healingCooldown = false;
        }, 300L);
    }

    public boolean isHealingOnCooldown() {
        return healingCooldown;
    }

    public void deleteBoard() {
        this.board.delete();
    }

    /***
        https://i.kym-cdn.com/photos/images/original/001/688/902/e4b.jpg
        "Kills" a player without them actually being killed, and spawns a villager in their place to represent death
    */
    public void kill(KillReason reason) {
        Location vLoc = player.getLocation();
        if (this.team == CTFTeam.BLUE) {
            player.teleport(ParallelCTF.gameManager.ctfMap.blueSpawnPos);
        }
        else {
            player.teleport(ParallelCTF.gameManager.ctfMap.redSpawnPos);
        }
        // apparently this needs to be run a tick (or two) later
        Bukkit.getScheduler().runTaskLater(ParallelCTF.gameManager.getPlugin(), () -> {
            player.setFireTicks(0);
        }, 2L);
        player.setFallDistance(0);
        player.setHealth(20D);
        ctfClass.giveClass();
        healingCooldown = false;
        // remove all thrown ender pearls when a ninja dies
        // prevents them from teleporting out of spawn after dying
        if (this.ctfClass instanceof NinjaClass ninja) {
            ninja.thrownPearls.forEach(Entity::remove);
        }
        // medic cobwebs are removed on death
        if (this.ctfClass instanceof MedicClass medic) {
            medic.placedWebs.forEach((l) -> l.getBlock().setType(Material.AIR));
            medic.placedWebs.clear();
        }
        if (ParallelCTF.gameManager.getBlueFlagCarrier() == this) {
            if (reason == KillReason.SPAWN_CAMP || reason == KillReason.CLASS_CHANGE || reason == KillReason.TEAM_CHANGE) {
                ParallelCTF.gameManager.ctfMap.resetBlueFlag();
                ParallelCTF.sendMessage("§9Blue's Flag §ahas been reset!");
                return;
            }
            ParallelCTF.gameManager.ctfMap.dropBlueFlag(vLoc);
        }
        if (ParallelCTF.gameManager.getRedFlagCarrier() == this) {
            if (reason == KillReason.SPAWN_CAMP || reason == KillReason.CLASS_CHANGE || reason == KillReason.TEAM_CHANGE) {
                ParallelCTF.gameManager.ctfMap.resetRedFlag();
                ParallelCTF.sendMessage("§cRed's Flag §ahas been reset!");
                return;
            }
            ParallelCTF.gameManager.ctfMap.dropRedFlag(vLoc);
        }
        Villager villager = (Villager)player.getWorld().spawnEntity(vLoc, EntityType.VILLAGER);
        villager.setAI(false);
        villager.setGravity(false);
        villager.damage(20D);
        villager.getWorld().playSound(vLoc, "entity.villager.death", 1, 1);
    }

    public Player getMcPlayer() { return this.player; }

    public CTFTeam getTeam() { return this.team; }

    public CTFClass getCtfClass() { return this.ctfClass; }

    public String getColorFormatting() {
        return switch (this.team) {
            case RED -> "§c";
            case BLUE -> "§9";
            case SPECTATOR -> "§7";
        };
    }

}
