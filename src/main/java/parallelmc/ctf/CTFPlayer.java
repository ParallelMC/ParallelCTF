package parallelmc.ctf;

import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import parallelmc.ctf.classes.CTFClass;

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

    /***
     * Updates the player's scoreboard
     */
    public void updateBoard(int mins, int secs, int capsToWin, int redCaps, int blueCaps) {
        this.board.updateLines(
                "",
                "Time Left  | " + mins + ":" + (secs < 10 ? "0" + secs : secs),
                "Your Team | " + (team == CTFTeam.RED ? "§cRed" : "§9Blue"),
                "",
                "§c§lRed",
                "§cCaptures | " + redCaps + "/" + capsToWin,
                "§cFlag Status | " + (ParallelCTF.gameManager.isRedFlagTaken() ? "Held by " + ParallelCTF.gameManager.getRedFlagCarrier().getMcPlayer().getName() : "Home"),
                "",
                "§9§lBlue",
                "§9Captures | " + blueCaps + "/" + capsToWin,
                "§9Flag Status | " + (ParallelCTF.gameManager.isBlueFlagTaken() ? "Held by " + ParallelCTF.gameManager.getBlueFlagCarrier().getMcPlayer().getName() : "Home")
        );
    }

    /***
     * Ran when a medic heals another player
     * The player is put on healing cooldown for a few seconds
     */
    public void medicHeal() {
        ctfClass.giveClass();
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
    public void kill() {
        Location vLoc = player.getLocation();
        if (this.team == CTFTeam.BLUE) {
            player.teleport(ParallelCTF.gameManager.ctfMap.blueSpawnPos);
        }
        else {
            player.teleport(ParallelCTF.gameManager.ctfMap.redSpawnPos);
        }
        player.setFireTicks(0);
        player.setFallDistance(0);
        player.setHealth(20D);
        ctfClass.giveClass();
        healingCooldown = false;
        if (ParallelCTF.gameManager.getBlueFlagCarrier() == this) {
            ParallelCTF.gameManager.ctfMap.resetBlueFlag();
            ParallelCTF.sendMessage("§9Blue's Flag §ahas been reset!");
        }
        if (ParallelCTF.gameManager.getRedFlagCarrier() == this) {
            ParallelCTF.gameManager.ctfMap.resetRedFlag();
            ParallelCTF.sendMessage("§cRed's Flag §ahas been reset!");
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

}
