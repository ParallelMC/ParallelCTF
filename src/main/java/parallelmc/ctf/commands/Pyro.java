package parallelmc.ctf.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.ctf.CTFPlayer;
import parallelmc.ctf.GameState;
import parallelmc.ctf.ParallelCTF;

public class Pyro implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player player) {
            if (ParallelCTF.gameManager.gameState != GameState.PLAY) {
                ParallelCTF.sendMessageTo(player, "Cannot equip classes yet!");
                return true;
            }
            CTFPlayer pl = ParallelCTF.gameManager.getPlayer(player);
            pl.setClass("Pyro");
            if (!ParallelCTF.gameManager.ctfMap.isPlayerInSpawn(pl)) {
                pl.kill();
            }
            ParallelCTF.sendMessageTo(player, "Equipped the Pyro class!");
        }
        return true;
    };
}