package parallelmc.ctf;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.io.Files;
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
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parallelmc.ctf.classes.DwarfClass;
import parallelmc.ctf.classes.NinjaClass;
import parallelmc.ctf.classes.SpectatorClass;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
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
    private final String defaultMap;
    public GameState gameState;
    private final HashMap<String, CTFMap> maps = new HashMap<>();
    public CTFMap ctfMap;
    public HashMap<UUID, CTFPlayer> players = new HashMap<>();
    public HashSet<UUID> hasNameHandler = new HashSet<>();
    public HashMap<Entity, ArrowShot> shotArrows = new HashMap<>();
    public HashSet<UUID> voteStart = new HashSet<>();
    public HashSet<UUID> voteMap = new HashSet<>();
    public HashMap<String, Integer> mapVotes = new HashMap<>();

    public GameManager(Plugin plugin, Location preGameLoc, int winCaps, String defaultMap) {
        this.plugin = plugin;
        this.preGameLoc = preGameLoc;
        this.capturesToWin = winCaps;
        this.defaultMap = defaultMap;
        this.gameState = GameState.PREGAME;
        preloadMaps();
        maps.forEach((n, m) -> mapVotes.put(n, 0));
        startPregameLoop();
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
            players.put(player.getUniqueId(), new CTFPlayer(player, CTFTeam.BLUE));
            player.playerListName(Component.text(player.getName(), NamedTextColor.BLUE));
            player.displayName(Component.text(player.getName(), NamedTextColor.BLUE));
            ParallelCTF.sendMessageTo(player, "Joined §9Blue team!");
            bluePlayers++;
            if (gameState == GameState.PLAY) {
                player.teleport(ctfMap.blueSpawnPos);
            }
        }
        else { // secretly favor red to make things easier
            players.put(player.getUniqueId(), new CTFPlayer(player, CTFTeam.RED));
            player.playerListName(Component.text(player.getName(), NamedTextColor.RED));
            player.displayName(Component.text(player.getName(), NamedTextColor.RED));
            ParallelCTF.sendMessageTo(player, "Joined §cRed team!");
            redPlayers++;
            if (gameState == GameState.PLAY) {
                player.teleport(ctfMap.redSpawnPos);
            }
        }
        updateName(player);
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
        players.remove(player.getUniqueId());
        if (redPlayers == 0 && bluePlayers == 0 && gameState == GameState.PLAY) {
            decideWinner();
            return;
        }
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
        return players.get(player.getUniqueId());
    }

    /***
     * Switches a player's team
     * @param player The player to switch
     * @param newTeam The team they will join
     */
    public void changeTeam(Player player, CTFTeam newTeam) {
        CTFPlayer pl = players.get(player.getUniqueId());
        if (newTeam == CTFTeam.SPECTATOR) {
            if (pl.getTeam() == CTFTeam.RED)
                redPlayers--;
            else if (pl.getTeam() == CTFTeam.BLUE)
                bluePlayers--;
            player.playerListName(Component.text(player.getName(), NamedTextColor.GRAY));
            player.displayName(Component.text(player.getName(), NamedTextColor.GRAY));
            player.setAllowFlight(true);
            pl.setClass("Spectator");
        }
        else if (newTeam == CTFTeam.RED) {
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
        // if they used to be a spectator disallow flight and reveal to all players
        if (pl.getTeam() == CTFTeam.SPECTATOR) {
            player.setAllowFlight(false);
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
        if (gameState == GameState.PLAY)
            pl.kill();
        pl.setTeam(newTeam);
        updateName(player);
    }

    private void preloadMaps() {
        String[] mapNames = plugin.getDataFolder().list();
        if (mapNames == null) {
            ParallelCTF.log(Level.SEVERE, "Failed to preload maps! Does config folder exist?");
            return;
        }
        // assumes the world name to be world-ctf
        World world = plugin.getServer().getWorld("world-ctf");
        int loadedMaps = 0;
        for (String m : mapNames) {
            if (m.equals("config.yml")) continue;
            FileConfiguration config = new YamlConfiguration();
            try {
                config.load(new File(plugin.getDataFolder(), m));
            } catch (Exception e) {
                ParallelCTF.log(Level.WARNING, "Failed to load map configuration!");
                continue;
            }
            // google function to remove extension
            @SuppressWarnings("UnstableApiUsage")
            String map = Files.getNameWithoutExtension(m);
            // beautiful constructor
            maps.put(map, new CTFMap(
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
            ));
            loadedMaps++;
            ParallelCTF.log(Level.WARNING, "Loaded map config " + map);
        }
        ParallelCTF.log(Level.WARNING, "Loaded " + loadedMaps + " of " + (mapNames.length - 1) + " maps");
    }

    /***
     * Loads a map from the map hashmap
     */
    public boolean loadMap(@NotNull String mapName) {
        if (maps.containsKey(mapName)) {
            this.ctfMap = maps.get(mapName);
            return true;
        }
        else {
            ParallelCTF.log(Level.SEVERE, "Could not find map " + mapName);
            return false;
        }
    }

    /***
     * Starts the game
     * Each player will be given the Tank class by default
     */
    public void start() {
        this.plugin.getServer().getScheduler().cancelTasks(plugin);
        // default map
        String winningMap = this.defaultMap;
        int winningVotes = -1;
        for (Map.Entry<String, Integer> e : mapVotes.entrySet()) {
            if (e.getValue() > winningVotes) {
                winningMap = e.getKey();
                winningVotes = e.getValue();
            }
        }
        if (!loadMap(winningMap)) {
            ParallelCTF.sendMessage("Failed to load map " + winningMap + "! Game start aborted.");
            return;
        }
        ParallelCTF.sendMessage("Map set to " + winningMap + "!");
        players.forEach((p, cp) -> {
            if (!(cp.getCtfClass() instanceof SpectatorClass))
                cp.setClass("Tank");
            Player player = plugin.getServer().getPlayer(p);
            if (player == null) {
                ParallelCTF.log(Level.WARNING, "Couldn't find player with UUID of " + p);
                return;
            }
            if (cp.getTeam() == CTFTeam.BLUE) {
                player.teleport(ctfMap.blueSpawnPos);
            } else {
                // teleport spectators to red team's spawn too cause why not
                player.teleport(ctfMap.redSpawnPos);
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
            Player player = plugin.getServer().getPlayer(p);
            if (player == null) {
                ParallelCTF.log(Level.WARNING, "Couldn't find player with UUID of " + p);
                return;
            }
            player.getInventory().clear();
            player.getActivePotionEffects().clear();
            player.setHealth(20D);
            player.setFoodLevel(37);
            player.setExp(0F);
            player.setLevel(0);
            player.teleport(preGameLoc);
            this.plugin.getServer().getScheduler().cancelTasks(plugin);
        });
        gameState = GameState.PREGAME;
        startPregameLoop();
    }

    private void startPregameLoop() {
        this.plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            String winningMap = "";
            int winningVotes = -1;
            for (Map.Entry<String, Integer> e : mapVotes.entrySet()) {
                if (e.getValue() > winningVotes) {
                    winningMap = e.getKey();
                    winningVotes = e.getValue();
                }
            }
            // thank you java
            String boardMap = winningMap;
            int neededPlayers = players.size() - 1;
            players.forEach((p, c) -> {
                Player player = plugin.getServer().getPlayer(p);
                if (player == null) {
                    ParallelCTF.log(Level.WARNING, "Couldn't find player with UUID of " + p);
                    return;
                }
                player.setFoodLevel(37);
                c.updateLobbyBoard(voteStart.size(), Math.max(neededPlayers, 4), boardMap, mapVotes.get(boardMap));
            });
        }, 0L, 20L);
    }

    /***
     * The main game loop, which handles most game logic
     */
    private void startGameLoop() {
        // flag damage loop
        // flag carriers are hurt 1.5 hearts every 15 seconds
        this.plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (redFlagTaken) {
                Player p = redFlagCarrier.getMcPlayer();
                if (p.getHealth() - 3D <= 0D) {
                    ParallelCTF.sendMessage(redFlagCarrier.getColorFormatting() + p.getName() + " §awas killed by §cRed's Flag!");
                    redFlagCarrier.kill();
                }
                else {
                    p.damage(3D);
                    ParallelCTF.sendMessageTo(p, "You were damaged by §cRed's Flag!");
                }
            }
            if (blueFlagTaken) {
                Player p = blueFlagCarrier.getMcPlayer();
                if (p.getHealth() - 3D <= 0D) {
                    ParallelCTF.sendMessage(blueFlagCarrier.getColorFormatting() + p.getName() + " §awas killed by §9Blue's Flag!");
                    blueFlagCarrier.kill();
                }
                else {
                    p.damage(3D);
                    ParallelCTF.sendMessageTo(p, "You were damaged by §9Blue's Flag!");
                }
            }
        }, 0L, 300L);
        // time loop
        this.plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            int mins = secondsLeft / 60;
            int secs = secondsLeft % 60;
            boolean redNl = redFlagTaken && (redFlagCarrier.getMcPlayer().getName().length() > 10);
            boolean blueNl = blueFlagTaken && (blueFlagCarrier.getMcPlayer().getName().length() > 10);
            List<String> flagLines = List.of("§cFlag Status | " + (isRedFlagTaken() ? "Held by " + (redNl ? "" : getRedFlagCarrier().getMcPlayer().getName()) : "Home"),
                    (redNl ? getRedFlagCarrier().getColorFormatting() + getRedFlagCarrier().getMcPlayer().getName() : ""),
                    "§9Flag Status | " + (isBlueFlagTaken() ? "Held by " + (blueNl ? "" : getBlueFlagCarrier().getMcPlayer().getName()) : "Home"),
                    (blueNl ? getBlueFlagCarrier().getColorFormatting() + getBlueFlagCarrier().getMcPlayer().getName() : ""));
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
                Player player = plugin.getServer().getPlayer(p);
                if (player == null) {
                    ParallelCTF.log(Level.WARNING, "Couldn't find player with UUID of " + p);
                    return;
                }
                if (c.getCtfClass() instanceof NinjaClass ninja && redFlagCarrier != c && blueFlagCarrier != c) {
                    if (player.getInventory().getItemInMainHand().getType() == Material.REDSTONE) {
                        if (!ninja.isInvisible())
                            ninja.goInvisibleTo(player, c.getTeam() == CTFTeam.BLUE ? CTFTeam.RED : CTFTeam.BLUE);
                    }
                    else if (ninja.isInvisible()) {
                        ninja.goVisibleTo(player, c.getTeam() == CTFTeam.BLUE ? CTFTeam.RED : CTFTeam.BLUE);
                    }
                }
                // dwarfs intentionally cannot sprint, so dont update their food bar
                if (!(c.getCtfClass() instanceof DwarfClass)) {
                    player.setFoodLevel(37);
                }
            });
        }, 0L, 1L);
    }

    /**
     * Forces a player's name to update to the correct team color
     * Extremely jank implementation but it works for now
     * @param p The player to update
     */
    private void updateName(Player p) {
        CTFPlayer player = getPlayer(p);
        PlayerInfoData data = new PlayerInfoData(WrappedGameProfile.fromPlayer(p), 0, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(player.getColorFormatting() + p.getName()));
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        packet.getPlayerInfoDataLists().write(0, Collections.singletonList(data));
        for (Player pl : plugin.getServer().getOnlinePlayers()) {
            if (pl.equals(p)) continue;
            pl.hidePlayer(plugin, p);
            try {
                ParallelCTF.getProtocolManager().sendServerPacket(pl, packet);
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (!hasNameHandler.contains(p.getUniqueId())) {
            ParallelCTF.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {
                        PacketContainer packet = event.getPacket();
                        PlayerInfoData oldData = packet.getPlayerInfoDataLists().read(0).get(0);
                        if (packet.getPlayerInfoAction().read(0) == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
                            if (oldData.getProfile().getName().equals(p.getName())) {
                                CTFPlayer player = getPlayer(p);
                                String nameStr = player.getColorFormatting() + p.getName();
                                WrappedChatComponent name = WrappedChatComponent.fromText(nameStr);
                                PlayerInfoData newData = new PlayerInfoData(oldData.getProfile().withName(nameStr), oldData.getLatency(), oldData.getGameMode(), name);
                                packet.getPlayerInfoDataLists().write(0, Collections.singletonList(newData));
                                event.setPacket(packet);
                            }
                        }
                    }
                }
            });
            hasNameHandler.add(p.getUniqueId());
        }
        for (Player pl : plugin.getServer().getOnlinePlayers()) {
            if (pl.equals(p)) continue;
            pl.showPlayer(plugin, p);
        }
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

    public boolean isPlayerNotFlagCarrier(Player p) {
        CTFPlayer pl = this.getPlayer(p);
        return (!redFlagTaken || redFlagCarrier != pl) && (!blueFlagTaken || blueFlagCarrier != pl);
    }

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
