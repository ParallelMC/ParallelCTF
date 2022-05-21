package parallelmc.ctf;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;
import parallelmc.ctf.classes.DwarfClass;
import parallelmc.ctf.classes.NinjaClass;

import java.io.File;
import java.util.HashMap;
import java.util.List;
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

    /***
     * Adds a player to the game. The teams are auto balanced
     * @param player The player to add
     */
    public void addPlayer(Player player) {
        if (gameState == GameState.PREGAME) {
            player.teleport(ParallelCTF.gameManager.preGameLoc);
        }
        if (redPlayers > bluePlayers) {
            players.put(player, new CTFPlayer(player, CTFTeam.BLUE));
            player.playerListName(Component.text(player.getName(), NamedTextColor.BLUE));
            player.displayName(Component.text(player.getName(), NamedTextColor.BLUE));
            ParallelCTF.sendMessageTo(player, "Joined §9Blue team!");
            bluePlayers++;
            if (gameState == GameState.PLAY) {
                player.teleport(ctfMap.blueSpawnPos);
            }
        }
        else { // secretly favor red to make things easier
            players.put(player, new CTFPlayer(player, CTFTeam.RED));
            player.playerListName(Component.text(player.getName(), NamedTextColor.RED));
            player.displayName(Component.text(player.getName(), NamedTextColor.RED));
            ParallelCTF.sendMessageTo(player, "Joined §cRed team!");
            redPlayers++;
            if (gameState == GameState.PLAY) {
                player.teleport(ctfMap.redSpawnPos);
            }
        }
    }

    /***
     * Removes a player from the game. The teams are currently NOT auto balanced when a player leaves
     * @param player The player to remove
     */
    public void removePlayer(Player player) {
        CTFPlayer pl = getPlayer(player);
        if (pl.getTeam() == CTFTeam.RED) {
            redPlayers--;
        } else if (pl.getTeam() == CTFTeam.BLUE) {
            bluePlayers--;
        }
        pl.deleteBoard();
        players.remove(player);
        if (bluePlayers > redPlayers + 1) {
            ParallelCTF.sendMessage("A slot is available on the §cRed Team! §aType /team red to join!");
        }
        if (redPlayers > bluePlayers + 1) {
            ParallelCTF.sendMessage("A slot is available on the §9Blue Team! §aType /team blue to join!");
        }
    }

    /***
     * Retrieve a CTFPlayer instance for a player
     * @param player The player to get
     * @return a CTFPlayer
     */
    public CTFPlayer getPlayer(Player player) {
        return players.get(player);
    }

    /***
     * Switches a player's team
     * @param player The player to switch
     * @param newTeam The team they will join
     */
    public void changeTeam(Player player, CTFTeam newTeam) {
        CTFPlayer pl = players.get(player);
        if (newTeam == CTFTeam.RED) {
            bluePlayers--;
            redPlayers++;
            player.playerListName(Component.text(player.getName(), NamedTextColor.RED));
            player.displayName(Component.text(player.getName(), NamedTextColor.RED));
        }
        else {
            redPlayers--;
            bluePlayers++;
            player.playerListName(Component.text(player.getName(), NamedTextColor.BLUE));
            player.displayName(Component.text(player.getName(), NamedTextColor.BLUE));
        }
        pl.setTeam(newTeam);
    }

    /***
     * Loads a map from a file config
     * Will allow hot loading of different maps between rounds in the future
     */
    public void loadMap() {
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

    /***
     * Starts the game
     * Each player will be given the Tank class by default
     */
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

    /***
     * Ends the game and resets everything
     * @param winner The team who won, or null if the game was a tie
     */
    public void endGame(@Nullable CTFTeam winner) {
        ParallelCTF.sendMessage((winner == null ? "The game ended in a tie!" : ((winner == CTFTeam.BLUE ? "§9Blue Team" : "§cRed Team")) + " §ais the winner!"));
        ctfMap.resetRedFlag();
        ctfMap.resetBlueFlag();
        redCaptures = 0;
        blueCaptures = 0;
        secondsLeft = 1800;
        players.forEach((p, cp) -> {
            p.getInventory().clear();
            p.getActivePotionEffects().clear();
            p.setHealth(20D);
            p.setFoodLevel(37);
            p.setExp(0F);
            p.setLevel(0);
            p.teleport(preGameLoc);
            this.plugin.getServer().getScheduler().cancelTasks(plugin);
        });
        gameState = GameState.PREGAME;
    }

    /***
     * The main game loop, which handles most game logic
     */
    private void startGameLoop() {
        // time loop
        this.plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            int mins = secondsLeft / 60;
            int secs = secondsLeft % 60;
            boolean redNl = redFlagTaken && (redFlagCarrier.getMcPlayer().getName().length() > 10);
            boolean blueNl = blueFlagTaken && (blueFlagCarrier.getMcPlayer().getName().length() > 10);
            List<String> flagLines = List.of("§cFlag Status | " + (ParallelCTF.gameManager.isRedFlagTaken() ? "Held by " + (redNl ? "" : ParallelCTF.gameManager.getRedFlagCarrier().getMcPlayer().getName()) : "Home"),
                    (redNl ? ParallelCTF.gameManager.getRedFlagCarrier().getMcPlayer().getName() : ""),
                    "§9Flag Status | " + (ParallelCTF.gameManager.isBlueFlagTaken() ? "Held by " + (blueNl ? "" : ParallelCTF.gameManager.getBlueFlagCarrier().getMcPlayer().getName()) : "Home"),
                    (blueNl ? ParallelCTF.gameManager.getBlueFlagCarrier().getMcPlayer().getName() : ""));
            players.forEach((p, c) -> {
                // java wizardry but prevents having to run the above for every player
                c.updateBoard(mins, secs, capturesToWin, redCaptures, blueCaptures, flagLines.toArray(String[]::new));
            });
            secondsLeft--;
            if (secondsLeft <= 0) {
                decideWinner();
            }
        }, 0L, 20L);
        // distance check loop
        // spawn camping and flag captures are ran here every 1/4 sec
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
                if (c.getCtfClass() instanceof NinjaClass ninja && redFlagCarrier != c && blueFlagCarrier != c) {
                    if (p.getInventory().getItemInMainHand().getType() == Material.REDSTONE) {
                        if (!ninja.isInvisible())
                            ninja.goInvisibleTo(p, c.getTeam() == CTFTeam.BLUE ? CTFTeam.RED : CTFTeam.BLUE);
                    }
                    else if (ninja.isInvisible()) {
                        ninja.goVisibleTo(p, c.getTeam() == CTFTeam.BLUE ? CTFTeam.RED : CTFTeam.BLUE);
                    }
                }
                // dwarfs intentionally cannot sprint, so dont update their food bar
                if (!(c.getCtfClass() instanceof DwarfClass)) {
                    p.setFoodLevel(37);
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

    /***
     * Decides the winner in the case of a force end or the clock hits zero
     */
    public void decideWinner() {
        if (blueCaptures > redCaptures) {
            endGame(CTFTeam.BLUE);
        }
        else if (redCaptures > blueCaptures) {
            endGame(CTFTeam.RED);
        }
        else {
            endGame(null);
        }
    }

    public void addRedCapture() {
        this.redCaptures++;
        if (redCaptures >= capturesToWin) {
            endGame(CTFTeam.RED);
        }
    }

    public void addBlueCapture() {
        this.blueCaptures++;
        if (blueCaptures >= capturesToWin) {
            endGame(CTFTeam.BLUE);
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
