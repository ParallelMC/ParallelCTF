package parallelmc.ctf.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.ctf.CTFPlayer;
import parallelmc.ctf.ParallelCTF;

public class Ninja implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player player) {
            CTFPlayer pl = ParallelCTF.gameManager.getPlayer(player);
            pl.setClass("Ninja");
            ParallelCTF.sendMessageTo(player, "Equipped the Ninja class!");
        }
        return true;
    };
}
