package parallelmc.ctf.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.ctf.ParallelCTF;

public class ClassInfo implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player player) {
            if (args.length < 1)
                return false;
            ParallelCTF.sendMessageTo(player, classInfo(args[0].toLowerCase()));
        }
        return true;
    }

    // since classes are stored as classes themselves and not instances
    // this is the best way of doing things right now
    private String classInfo(String cl) {
        return switch (cl) {
            case "archer" -> formatInfo("Archer", "Full Chainmail", "Punch II Bow, Stone Sword", "None", "1-shots players from 30+ blocks away");
            case "assassin" -> formatInfo("Assassin", "Gold Boots", "Iron Sword", "Can use Speed Boost for Speed III", "Can assassinate players, gains Strength II after assassinating");
            case "chemist" -> formatInfo("Chemist", "Leather Boots + Helmet, Prot II Gold Chest + Leggings", "Iron Sword", "None", "Can use splash potions, with an energy cooldown.");
            case "dwarf" -> formatInfo("Dwarf", "Chain Helmet + Boots, Diamond Chestplate + Leggings", "Netherite Sword", "Slowness I", "Can sneak to gain various enchantments on their sword");
            case "medic" -> formatInfo("Medic", "Full Gold", "Sharpess I Gold Sword", "Water Breathing, Fire Resistance", "Can punch teammates to heal them and refresh their items, cobweb snowballs");
            case "ninja" -> formatInfo("Ninja", "None", "Sharpess VI Gold Sword", "Speed II", "Can go invisible with redstone, smoke bombs, ender pearls");
            case "pyro" -> formatInfo("Pyro", "Leather + Chainmail Chestplate", "Explosive Bow, Diamond Axe", "None", "1-shots players on fire with the axe, flint and steel");
            case "soldier" -> formatInfo("Soldier", "Full Iron", "Iron Sword", "None", "Can climb walls with their sword, no fall damage");
            case "tank" -> formatInfo("Tank", "Diamond Armor", "Diamond Sword", "None", "None");
            default -> "Unknown class " + cl;
        };
    }

    private String formatInfo(String name, String armor, String weapons, String effects, String abilities) {
        return String.format("Class Info:\nName: %s\nArmor: %s\nWeapons: %s\nEffects: %s\nAbilities: %s",
                name,
                armor,
                weapons,
                effects,
                abilities);
    }
}
