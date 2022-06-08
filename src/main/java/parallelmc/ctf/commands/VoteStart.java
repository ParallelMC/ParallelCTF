package parallelmc.ctf.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.ctf.GameState;
import parallelmc.ctf.ParallelCTF;

public class VoteStart implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player player) {
            if (ParallelCTF.gameManager.gameState == GameState.PREGAME) {
                if (ParallelCTF.gameManager.voteStart.contains(player.getUniqueId())) {
                    ParallelCTF.sendMessageTo(player, "You have already voted to start!");
                    return true;
                }
                if (ParallelCTF.gameManager.players.size() < 4) {
                    ParallelCTF.sendMessageTo(player, "There must be at least 4 players to start the game!");
                    return true;
                }
                ParallelCTF.gameManager.voteStart.add(player.getUniqueId());
                ParallelCTF.sendMessage(player.getName() + " has voted the start the game. (" +
                        ParallelCTF.gameManager.voteStart.size() + "/" + (ParallelCTF.gameManager.players.size() - 1) + " votes needed)");
                if (ParallelCTF.gameManager.voteStart.size() >= ParallelCTF.gameManager.players.size() - 1) {
                    ParallelCTF.gameManager.voteStart.clear();
                    ParallelCTF.gameManager.start();
                }
            }
            else {
                ParallelCTF.sendMessageTo(player, "The game has already started!");
            }
        }
        return true;
    }
}
