package parallelmc.ctf;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import parallelmc.ctf.classes.NinjaClass;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

public class GameManager {
    private int redCaptures = 0;
    private int blueCaptures = 0;
    private int secondsLeft = 1800;
    private int redPlayers = 0;
    private int bluePlayers = 0;
    private boolean redFlagTaken = false;
    private boolean blueFlagTaken = false;
    private CTFPlayer redFlagCarrier;
    private CTFPlayer blueFlagCarrier;
    private final Plugin plugin;
    public Location preGameLoc;
    private final int capturesToWin;
    public GameState gameState;
    public CTFMap ctfMap;
    public HashMap<Player, CTFPlayer> players = new HashMap<>();
    public HashMap<Entity, ArrowShot> shotArrows = new HashMap<>();

    public GameManager(Plugin plugin, Location preGameLoc, int winCaps) {
        this.plugin = plugin;
        this.preGameLoc = preGameLoc;
        this.capturesToWin = winCaps;
        this.gameState = GameState.PREGAME;
    }

    public void addPlayer(Player player) {
        if (gameState == GameState.PREGAME) {
            player.teleport(ParallelCTF.gameManager.preGameLoc);
        }
        if (redPlayers > bluePlayers) {
            players.put(player, new CTFPlayer(player, CTFTeam.BLUE));
            player.customName(Component.text(player.getName(), NamedTextColor.BLUE));
            player.playerListName(Component.text(player.getName(), NamedTextColor.BLUE));
            ParallelCTF.sendMessageTo(player, "Joined §9Blue team!");
            bluePlayers++;
            if (gameState == GameState.PLAY) {
                player.teleport(ctfMap.blueSpawnPos);
            }
        }
        else { // secretly favor red to make things easier
            players.put(player, new CTFPlayer(player, CTFTeam.RED));
            player.customName(Component.text(player.getName(), NamedTextColor.RED));
            player.playerListName(Component.text(player.getName(), NamedTextColor.RED));
            ParallelCTF.sendMessageTo(player, "Joined §cRed team!");
            redPlayers++;
            if (gameState == GameState.PLAY) {
                player.teleport(ctfMap.redSpawnPos);
            }
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

    // will allow hot loading of different maps between rounds in the future
    public void loapMap() {
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(new File(plugin.getDataFolder(), "map.yml"));
        } catch (Exception e) {
            ParallelCTF.log(Level.SEVERE, "Failed to load map configuration! Does the file exist?");
            return;
        }
        // assumes the world name to be world-ctf
        World world = plugin.getServer().getWorld("world-ctf");
        // beautiful constructor
        this.ctfMap = new CTFMap(
                world,
                config.getString("name"),
                new Location(world, config.getDouble("red.flag.x"), config.getDouble("red.flag.y"), config.getDouble("red.flag.z")),
                new Location(world, config.getDouble("blue.flag.x"), config.getDouble("blue.flag.y"), config.getDouble("blue.flag.z")),
                new Location(world, config.getDouble("red.spawn.x"), config.getDouble("red.spawn.y"), config.getDouble("red.spawn.z")),
                new Location(world, config.getDouble("blue.spawn.x"), config.getDouble("blue.spawn.y"), config.getDouble("blue.spawn.z")),
                new Location(world, config.getDouble("red.corner1.x"), config.getDouble("red.corner1.y"), config.getDouble("red.corner1.z")),
                new Location(world, config.getDouble("red.corner2.x"), config.getDouble("red.corner2.y"), config.getDouble("red.corner2.z")),
                new Location(world, config.getDouble("blue.corner1.x"), config.getDouble("blue.corner1.y"), config.getDouble("blue.corner1.z")),
                new Location(world, config.getDouble("blue.corner2.x"), config.getDouble("blue.corner2.y"), config.getDouble("blue.corner2.z"))
        );
        ParallelCTF.log(Level.WARNING, "Loaded CTF Map " + this.ctfMap.name);
    }

    public void start() {
        players.forEach((p, cp) -> {
            cp.setClass("Tank");
            if (cp.getTeam() == CTFTeam.BLUE) {
                p.teleport(ctfMap.blueSpawnPos);
            } else {
                p.teleport(ctfMap.redSpawnPos);
            }
        });
        startGameLoop();
        ParallelCTF.sendMessage("Game Started. GLHF!");
        this.gameState = GameState.PLAY;
    }

    private void startGameLoop() {
        // time loop
        this.plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            int mins = secondsLeft / 60;
            int secs = secondsLeft % 60;
            players.forEach((p, c) -> {
                c.updateBoard(mins, secs, capturesToWin, redCaptures, blueCaptures);
            });
            secondsLeft--;
        }, 0L, 20L);
        // distance check loop
        // spawn camping and flag captures are ran here
        // running them every tick would be too expensive
        this.plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            this.ctfMap.checkSpawnCamping();
            // do these separately to keep things neat
            this.ctfMap.checkFlagTaken();
            this.ctfMap.checkFlagCaptured();
        }, 0L, 5L);
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

    public void addRedCapture() {
        this.redCaptures++;
        setBlueFlagTaken(false);
        setBlueFlagCarrier(null);
        if (redCaptures >= capturesToWin) {
            // TODO: red win
        }
    }

    public void addBlueCapture() {
        this.blueCaptures++;
        setRedFlagTaken(false);
        setRedFlagCarrier(null);
        if (blueCaptures >= capturesToWin) {
            // TODO: red win
        }
    }

    public boolean isRedFlagTaken() {
        return redFlagTaken;
    }

    public boolean isBlueFlagTaken() {
        return blueFlagTaken;
    }

    public void setRedFlagTaken(boolean value) {
        this.redFlagTaken = value;
    }

    public void setBlueFlagTaken(boolean value) {
        this.blueFlagTaken = value;
    }

    public CTFPlayer getRedFlagCarrier() {
        return redFlagCarrier;
    }

    public void setRedFlagCarrier(@Nullable CTFPlayer redFlagCarrier) {
        this.redFlagCarrier = redFlagCarrier;
    }

    public CTFPlayer getBlueFlagCarrier() {
        return blueFlagCarrier;
    }

    public void setBlueFlagCarrier(@Nullable CTFPlayer blueFlagCarrier) {
        this.blueFlagCarrier = blueFlagCarrier;
    }
}
