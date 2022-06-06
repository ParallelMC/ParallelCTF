package parallelmc.ctf.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.ctf.GameState;
import parallelmc.ctf.ParallelCTF;

public class VoteMap implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player player) {
            if (args.length < 1)
                return false;
            if (ParallelCTF.gameManager.gameState == GameState.PREGAME) {
                if (ParallelCTF.gameManager.voteMap.contains(player)) {
                    ParallelCTF.sendMessageTo(player, "You have already voted for a map!");
                    return true;
                }
                String name = args[0].toLowerCase();
                if (!ParallelCTF.gameManager.mapVotes.containsKey(name)) {
                    ParallelCTF.sendMessageTo(player, "Invalid map! Maps include:");
                    ParallelCTF.gameManager.mapVotes.keySet().forEach((m) -> {
                        ParallelCTF.sendMessageTo(player, m);
                    });
                    return true;
                }
                ParallelCTF.gameManager.voteMap.add(player);
                int prevVotes = ParallelCTF.gameManager.mapVotes.get(name);
                ParallelCTF.gameManager.mapVotes.put(name, prevVotes + 1);
                ParallelCTF.sendMessage(player.getName() + " voted for the map " + name + "! (" + (prevVotes + 1) + " votes)");
            }
            else {
                ParallelCTF.sendMessageTo(player, "The game has already started!");
            }
        }
        return true;
    }
}
