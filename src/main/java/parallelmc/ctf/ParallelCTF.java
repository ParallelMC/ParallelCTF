package parallelmc.ctf;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.Force;
import parallelmc.ctf.classes.*;
import parallelmc.ctf.commands.*;
import parallelmc.ctf.events.*;

import java.util.HashMap;
import java.util.logging.Level;

public class ParallelCTF extends JavaPlugin {
    public static Level LOG_LEVEL = Level.INFO;
    public static final HashMap<String, CTFClass> classes = new HashMap<>();
    public static GameManager gameManager;

    @Override
    public void onLoad() {
        classes.put("Tank", new TankClass());
        classes.put("Soldier", new SoldierClass());
        classes.put("Medic", new MedicClass());
        classes.put("Archer", new ArcherClass());
        classes.put("Ninja", new NinjaClass());
        classes.put("Pyro", new PyroClass());
        // separate register function for items
        for (CTFClass c : classes.values()) {
            c.registerKit();
        }
    }

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();

        manager.registerEvents(new OnPlayerJoin(), this);
        manager.registerEvents(new OnRightClick(), this);
        manager.registerEvents(new OnPlayerLeave(), this);
        manager.registerEvents(new OnDropItem(), this);
        manager.registerEvents(new OnDamage(), this);
        manager.registerEvents(new OnDamageEntity(), this);
        manager.registerEvents(new OnBowShoot(), this);
        manager.registerEvents(new OnProjectileHit(), this);
        manager.registerEvents(new OnTeleport(), this);
        manager.registerEvents(new OnCreatureSpawn(), this);
        manager.registerEvents(new OnProjectileThrown(), this);
        // manager.registerEvents(new OnDeath(), this);
        this.getCommand("team").setExecutor(new ChangeTeam());
        this.getCommand("forceteam").setExecutor(new ForceTeam());
        this.getCommand("tank").setExecutor(new Tank());
        this.getCommand("soldier").setExecutor(new Soldier());
        this.getCommand("medic").setExecutor(new Medic());
        this.getCommand("archer").setExecutor(new Archer());
        this.getCommand("ninja").setExecutor(new Ninja());
        this.getCommand("pyro").setExecutor(new Pyro());


        gameManager = new GameManager(this);
        gameManager.startGameLoop();
    }

    @Override
    public void onDisable() {

    }

    public static void log(String message) {
        Bukkit.getLogger().log(LOG_LEVEL, "[ParallelCTF] " + message);
    }

    public static void log(Level level, String message) {
        Bukkit.getLogger().log(level, "[ParallelCTF] " + message);
    }

    public static void sendMessageTo(Player player, String message) {
        Component msg = Component.text("§3[§f§lCTF§3] §a" + message);
        player.sendMessage(msg);
    }


}
