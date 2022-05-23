package parallelmc.ctf.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.ctf.CTFTeam;
import parallelmc.ctf.ParallelCTF;

public class ForceTeam implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player player && player.isOp()) {
            if (args.length < 2) {
                return false;
            }
            Player p = player.getServer().getPlayer(args[0]);
            if (p == null) {
                ParallelCTF.sendMessageTo(player, "Could not find player" + args[0]);
            }
            if ("red".equalsIgnoreCase(args[1])) {
                ParallelCTF.gameManager.changeTeam(p, CTFTeam.RED);
            }
            else if ("blue".equalsIgnoreCase(args[1])) {
                ParallelCTF.gameManager.changeTeam(p, CTFTeam.BLUE);
            }
            else {
                ParallelCTF.sendMessageTo(player, "Unknown team " + args[0]);
                return true;
            }
        }
        return true;
    };
}
