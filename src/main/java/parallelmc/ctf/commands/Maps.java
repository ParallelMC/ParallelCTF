package parallelmc.ctf.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.ctf.ParallelCTF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Maps implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Alphabetically sort list of maps
        List<String> sortedMapsList = new ArrayList<>(ParallelCTF.gameManager.maps.keySet());
        Collections.sort(sortedMapsList);
        String sortedMaps = String.join(", ", sortedMapsList);
        if (commandSender instanceof Player player) {
            ParallelCTF.sendMessageTo(player, "Map List: " + sortedMaps);
        } else if (commandSender instanceof ConsoleCommandSender) {
            ParallelCTF.sendConsoleMessage("Map List: " + sortedMaps);
        }
        return true;
    }
}
