package parallelmc.ctf.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.ctf.GameState;
import parallelmc.ctf.ParallelCTF;

public class LoadMap implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof ConsoleCommandSender || (commandSender instanceof Player player && player.isOp())) {
            if (args.length < 1) {
                return false;
            }
            if (ParallelCTF.gameManager.gameState != GameState.PREGAME) {
                commandSender.sendMessage("The game has already started! Cannot load map");
                return true;
            }
            if (ParallelCTF.gameManager.loadMap(args[0])) {
                commandSender.sendMessage("Map loaded!");
            } else {
                commandSender.sendMessage("Failed to load map!");
            }
        }
        return true;
    };
}