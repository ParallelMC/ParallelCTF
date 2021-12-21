package parallelmc.ctf;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import parallelmc.ctf.classes.NinjaClass;

import java.util.HashMap;

public class GameManager {
    private int redCaptures = 0;
    private int blueCaptures = 0;
    private int secondsLeft = 1800;
    private int redPlayers = 0;
    private int bluePlayers = 0;
    private final Plugin plugin;
    public HashMap<Player, CTFPlayer> players = new HashMap<>();
    public HashMap<Entity, ArrowShot> shotArrows = new HashMap<>();

    public GameManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(Player player) {
        if (redPlayers > bluePlayers) {
            players.put(player, new CTFPlayer(player, CTFTeam.BLUE));
            player.customName(Component.text(player.getName(), NamedTextColor.BLUE));
            player.playerListName(Component.text(player.getName(), NamedTextColor.BLUE));
            ParallelCTF.sendMessageTo(player, "Joined §9Blue team!");
            bluePlayers++;
        }
        else { // secretly favor red to make things easier
            players.put(player, new CTFPlayer(player, CTFTeam.RED));
            player.customName(Component.text(player.getName(), NamedTextColor.RED));
            player.playerListName(Component.text(player.getName(), NamedTextColor.RED));
            ParallelCTF.sendMessageTo(player, "Joined §cRed team!");
            redPlayers++;
        }
    }

    public void removePlayer(Player player) {
        CTFPlayer pl = getPlayer(player);
        if (pl.getTeam() == CTFTeam.RED) {
            redPlayers--;
        } else if (pl.getTeam() == CTFTeam.BLUE) {
            bluePlayers--;
        }
        pl.deleteBoard();
        players.remove(player);
    }

    public CTFPlayer getPlayer(Player player) {
        return players.get(player);
    }

    public void changeTeam(Player player, CTFTeam newTeam) {
        CTFPlayer pl = players.get(player);
        if (newTeam == CTFTeam.RED) {
            bluePlayers--;
            redPlayers++;
        }
        else {
            redPlayers--;
            bluePlayers++;
        }
        pl.setTeam(newTeam);
    }

    public void startGameLoop() {
        // time loop
        this.plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            int mins = secondsLeft / 60;
            int secs = secondsLeft % 60;
            players.forEach((p, c) -> {
                c.updateBoard(mins, secs, redCaptures, blueCaptures);
            });
            secondsLeft--;
        }, 0L, 20L);
        // tick loop
        this.plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            players.forEach((p, c) -> {
                if (c.getCtfClass() instanceof NinjaClass ninja) {
                    if (p.getInventory().getItemInMainHand().getType() == Material.REDSTONE) {
                        if (!ninja.isInvisible()) {
                            ninja.goInvisibleTo(p, c.getTeam() == CTFTeam.RED ? CTFTeam.BLUE : CTFTeam.RED);
                        }
                    }
                    else if (ninja.isInvisible()) {
                        ninja.goVisibleTo(p, c.getTeam() == CTFTeam.RED ? CTFTeam.BLUE : CTFTeam.RED);
                    }
                }
            });
        }, 0L, 1L);
    }

    /**
     * Get the disparity between teams
     * @return If red has more players, 1. If blue has more players, -1. If they are equal, 0
     */
    public int getTeamDisparity() {
        if (redPlayers > bluePlayers || bluePlayers == 0)
            return 1;
        if (bluePlayers > redPlayers)
            return -1;
        return 0;
    }


    public void addNewShot(Entity arrow, ArrowShot shot) {
        this.shotArrows.put(arrow, shot);
    }

    public void removeShot(Entity arrow) {
        this.shotArrows.remove(arrow);
    }

    public ArrowShot getShot(Entity arrow) {
        return this.shotArrows.get(arrow);
    }

    public Plugin getPlugin() { return this.plugin; }

    public int getRedCaptures() { return this.redCaptures; }

    public int getBlueCaptures() { return this.blueCaptures; }

    public int getSecondsLeft() { return this.secondsLeft; }
}
