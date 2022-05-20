package parallelmc.ctf.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import parallelmc.ctf.GameState;
import parallelmc.ctf.ParallelCTF;

public class EndGame implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender.isOp() || commandSender instanceof ConsoleCommandSender) {
            if (ParallelCTF.gameManager.gameState == GameState.PLAY) {
                ParallelCTF.sendMessage("The game was ended by " + commandSender.getName());
                ParallelCTF.gameManager.decideWinner();
            }
        }
        return true;
    }
}