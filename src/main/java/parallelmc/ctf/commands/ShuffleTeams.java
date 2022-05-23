package parallelmc.ctf.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import parallelmc.ctf.CTFPlayer;
import parallelmc.ctf.CTFTeam;
import parallelmc.ctf.GameState;
import parallelmc.ctf.ParallelCTF;

import java.util.Collections;
import java.util.List;

public class ShuffleTeams implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender.isOp() || commandSender instanceof ConsoleCommandSender) {
            if (ParallelCTF.gameManager.gameState == GameState.PREGAME) {
                List<CTFPlayer> players = ParallelCTF.gameManager.players.values().stream().toList();
                Collections.shuffle(players);
                int i = 0;
                for (CTFPlayer p : players) {
                    if (p.getTeam() == CTFTeam.SPECTATOR) continue;
                    if (i % 2 == 0) {
                        ParallelCTF.gameManager.changeTeam(p.getMcPlayer(), CTFTeam.RED);
                    } else {
                        ParallelCTF.gameManager.changeTeam(p.getMcPlayer(), CTFTeam.BLUE);
                    }
                    i++;
                }
                ParallelCTF.sendMessage("Teams were shuffled by " + commandSender.getName());
            }
        }
        return true;
    }
}
