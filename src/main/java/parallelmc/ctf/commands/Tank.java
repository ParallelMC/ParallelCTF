package parallelmc.ctf.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.ctf.CTFPlayer;
import parallelmc.ctf.GameState;
import parallelmc.ctf.KillReason;
import parallelmc.ctf.ParallelCTF;

public class Tank implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player player) {
            if (ParallelCTF.gameManager.gameState != GameState.PLAY) {
                ParallelCTF.sendMessageTo(player, "Cannot equip classes yet!");
                return true;
            }
            CTFPlayer pl = ParallelCTF.gameManager.getPlayer(player);
            pl.setClass("Tank");
            if (ParallelCTF.gameManager.ctfMap.isPlayerNotInSpawn(pl)) {
                pl.kill(KillReason.CLASS_CHANGE);
            }
            ParallelCTF.sendMessageTo(player, "Equipped the Tank class!");
        }
        return true;
    };
}
