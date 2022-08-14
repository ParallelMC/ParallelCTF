package parallelmc.ctf.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.ctf.CTFPlayer;
import parallelmc.ctf.ParallelCTF;

public class CallMedic implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player player) {
            CTFPlayer pl = ParallelCTF.gameManager.getPlayer(player);
            ParallelCTF.sendMessageToTeam(pl,pl.getColorFormatting() + player.getName() + " §8> §6/md §aMedic!");
        }
        return true;
    }
}
