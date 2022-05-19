package parallelmc.ctf.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.ctf.ParallelCTF;

import java.util.logging.Level;

public class Debug implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender.isOp()) {
            ParallelCTF.sendMessageTo((Player)commandSender, String.format("----DEBUG INFO:----\nGAMESTATE: %s\nMAPNAME: %s\nREDFLAGTAKEN: %b\nBLUEFLAGTAKEN: %b\nREDFLAGCARRIER: %s\nBLUEFLAGCARRIER: %s\n",
                    ParallelCTF.gameManager.gameState,
                    ParallelCTF.gameManager.ctfMap.name,
                    ParallelCTF.gameManager.isRedFlagTaken(),
                    ParallelCTF.gameManager.isBlueFlagTaken(),
                    ParallelCTF.gameManager.getRedFlagCarrier() == null ? "none" : ParallelCTF.gameManager.getRedFlagCarrier().getMcPlayer().getName(),
                    ParallelCTF.gameManager.getBlueFlagCarrier() == null ? "none" : ParallelCTF.gameManager.getBlueFlagCarrier().getMcPlayer().getName()
                    ));
        }
        else if (commandSender instanceof ConsoleCommandSender) {
            ParallelCTF.log(Level.WARNING, String.format("----DEBUG INFO:----\nGAMESTATE: %s\nMAPNAME: %s\nREDFLAGTAKEN: %b\nBLUEFLAGTAKEN: %b\nREDFLAGCARRIER: %s\nBLUEFLAGCARRIER: %s\n",
                    ParallelCTF.gameManager.gameState,
                    ParallelCTF.gameManager.ctfMap.name,
                    ParallelCTF.gameManager.isRedFlagTaken(),
                    ParallelCTF.gameManager.isBlueFlagTaken(),
                    ParallelCTF.gameManager.getRedFlagCarrier() == null ? "none" : ParallelCTF.gameManager.getRedFlagCarrier().getMcPlayer().getName(),
                    ParallelCTF.gameManager.getBlueFlagCarrier() == null ? "none" : ParallelCTF.gameManager.getBlueFlagCarrier().getMcPlayer().getName()
            ));
        }
        return true;
    }
}
