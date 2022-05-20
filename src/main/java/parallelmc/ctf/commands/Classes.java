package parallelmc.ctf.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.ctf.ParallelCTF;

public class Classes implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player player) {
            ParallelCTF.sendMessageTo(player, "Current classes include: §f/archer /assassin /dwarf /medic /ninja /pyro /soldier /tank");
        }
        return true;
    }
}
