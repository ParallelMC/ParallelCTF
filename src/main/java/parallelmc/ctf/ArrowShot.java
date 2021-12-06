package parallelmc.ctf;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public record ArrowShot(Player shooter, Location shotLocation) {}
