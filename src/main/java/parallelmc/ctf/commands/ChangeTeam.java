package parallelmc.ctf.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.ctf.CTFTeam;
import parallelmc.ctf.ParallelCTF;

public class ChangeTeam implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player player) {
            if (args.length < 1) {
                return false;
            }
            if ("red".equalsIgnoreCase(args[0])) {
                if (ParallelCTF.gameManager.getTeamDisparity() != -1) {
                    ParallelCTF.sendMessageTo(player, "Cannot join Red, as it would make the teams uneven!");
                    return true;
                }
                ParallelCTF.gameManager.changeTeam(player, CTFTeam.RED);
                ParallelCTF.sendMessageTo(player, "Joined §cRed team!");
            }
            else if ("blue".equalsIgnoreCase(args[0])) {
                if (ParallelCTF.gameManager.getTeamDisparity() != 1) {
                    ParallelCTF.sendMessageTo(player, "Cannot join Blue, as it would make the teams uneven!");
                    return true;
                }
                ParallelCTF.gameManager.changeTeam(player, CTFTeam.BLUE);
                ParallelCTF.sendMessageTo(player, "Joined §9Blue team!");
            }
            else if ("spectator".equalsIgnoreCase(args[0])) {
                ParallelCTF.gameManager.changeTeam(player, CTFTeam.SPECTATOR);
                ParallelCTF.sendMessageTo(player, "Joined §7Spectators!");
            }
            else {
                ParallelCTF.sendMessageTo(player, "Unknown team " + args[0]);
                return true;
            }
        }
        return true;
    };
}