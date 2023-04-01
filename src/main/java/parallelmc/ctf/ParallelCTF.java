package parallelmc.ctf;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import parallelmc.ctf.classes.*;
import parallelmc.ctf.commands.*;
import parallelmc.ctf.events.*;

import java.util.HashMap;
import java.util.logging.Level;

public class ParallelCTF extends JavaPlugin {
    public static Level LOG_LEVEL = Level.INFO;
    public static final HashMap<String, Class<? extends CTFClass>> classes = new HashMap<>();
    public static GameManager gameManager;
    public static final BossBar alphaBossBar = BossBar.bossBar(Component.text("ParallelCTF v1.7 Alpha Gameplay", NamedTextColor.YELLOW), 1, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
    private static ProtocolManager protocolManager;

    @Override
    public void onLoad() {
        classes.put("Archer", ArcherClass.class);
        classes.put("Assassin", AssassinClass.class);
        classes.put("Chemist", ChemistClass.class);
        classes.put("Dwarf", DwarfClass.class);
        classes.put("Medic", MedicClass.class);
        classes.put("Ninja", NinjaClass.class);
        classes.put("Pyro", PyroClass.class);
        classes.put("Soldier", SoldierClass.class);
        classes.put("Tank", TankClass.class);
        classes.put("Spectator", SpectatorClass.class);
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();

        manager.registerEvents(new OnPlayerJoin(), this);
        manager.registerEvents(new OnRightClick(), this);
        manager.registerEvents(new OnPlayerLeave(), this);
        manager.registerEvents(new OnDropItem(), this);
        manager.registerEvents(new OnBlockBreak(), this);
        manager.registerEvents(new OnPlaceBlock(), this);
        manager.registerEvents(new OnFireSpread(), this);
        manager.registerEvents(new OnBlockBurn(), this);
        manager.registerEvents(new OnDamage(), this);
        manager.registerEvents(new OnDamageEntity(), this);
        manager.registerEvents(new OnBowShoot(), this);
        manager.registerEvents(new OnProjectileHit(), this);
        manager.registerEvents(new OnTeleport(), this);
        manager.registerEvents(new OnCreatureSpawn(), this);
        manager.registerEvents(new OnProjectileThrown(), this);
        manager.registerEvents(new OnChangeHeldItem(), this);
        manager.registerEvents(new OnInventoryClick(), this);
        manager.registerEvents(new OnChat(), this);
        manager.registerEvents(new OnPotionSplash(), this);
        this.getCommand("startgame").setExecutor(new StartGame());
        this.getCommand("endgame").setExecutor(new EndGame());
        this.getCommand("loadmap").setExecutor(new LoadMap());
        this.getCommand("votestart").setExecutor(new VoteStart());
        this.getCommand("votemap").setExecutor(new VoteMap());
        this.getCommand("shuffleteams").setExecutor(new ShuffleTeams());
        this.getCommand("debug").setExecutor(new Debug());
        this.getCommand("info").setExecutor(new ClassInfo());
        this.getCommand("team").setExecutor(new ChangeTeam());
        this.getCommand("forceteam").setExecutor(new ForceTeam());
        this.getCommand("classes").setExecutor(new Classes());
        this.getCommand("tank").setExecutor(new Tank());
        this.getCommand("soldier").setExecutor(new Soldier());
        this.getCommand("medic").setExecutor(new Medic());
        this.getCommand("archer").setExecutor(new Archer());
        this.getCommand("ninja").setExecutor(new Ninja());
        this.getCommand("pyro").setExecutor(new Pyro());
        this.getCommand("assassin").setExecutor(new Assassin());
        this.getCommand("dwarf").setExecutor(new Dwarf());
        this.getCommand("chemist").setExecutor(new Chemist());
        this.getCommand("md").setExecutor(new CallMedic());
        this.getCommand("d").setExecutor(new Defend());
        this.getCommand("c").setExecutor(new Careful());
        this.getCommand("b").setExecutor(new Buffs());

        // load config
        World world = this.getServer().getWorld("world-ctf");
        if (world == null) {
            ParallelCTF.log(Level.SEVERE, "Could not find world world-ctf! Exiting startup...");
            return;
        }
        FileConfiguration config = this.getConfig();
        String defaultMap = config.getString("default_map");
        if (defaultMap == null) {
            ParallelCTF.log(Level.SEVERE, "Could not find default_map in config!");
        } else {
            gameManager = new GameManager(this,
                    new Location(world, config.getDouble("pregame.x"), config.getDouble("pregame.y"), config.getDouble("pregame.z")),
                    config.getInt("win_captures"), defaultMap);
            gameManager.loadMap(defaultMap);
        }


        // setup important gamerules
        world.setGameRule(GameRule.DO_FIRE_TICK, true);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DO_TILE_DROPS, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);

    }

    @Override
    public void onDisable() { }

    public static void log(String message) {
        Bukkit.getLogger().log(LOG_LEVEL, "[ParallelCTF] " + message);
    }

    public static void log(Level level, String message) {
        Bukkit.getLogger().log(level, "[ParallelCTF] " + message);
    }

    public static void sendMessageToTeam(CTFPlayer player, String message) {
        for (CTFPlayer p : gameManager.players.values()) {
            if (p.getTeam() == player.getTeam()) {
                sendMessageTo(p.getMcPlayer(), message);
            }
        }
    }

    public static void sendMessageTo(Player player, String message) {
        Component msg = Component.text("§3[§f§lCTF§3] §a" + message);
        player.sendMessage(msg);
    }

    public static void sendMessage(String message) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            sendMessageTo(p, message);
        }
    }

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }
}
