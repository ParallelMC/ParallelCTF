package parallelmc.ctf;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import parallelmc.ctf.classes.NinjaClass;

public class CTFMap {
    public String name;
    public Location redFlagPos;
    public Location blueFlagPos;
    public Location redSpawnPos;
    public Location blueSpawnPos;
    // used for spawn-camping prevention
    private final BoundingBox redBB;
    private final BoundingBox blueBB;
    public final World world;


    public CTFMap(World world, String name, Location redFlagPos, Location blueFlagPos, Location redSpawnPos, Location blueSpawnPos, Location redSpawnCorner1, Location redSpawnCorner2, Location blueSpawnCorner1, Location blueSpawnCorner2) {
        this.world = world;
        this.name = name;
        this.redFlagPos = redFlagPos;
        this.blueFlagPos = blueFlagPos;
        this.redSpawnPos = redSpawnPos;
        this.blueSpawnPos = blueSpawnPos;
        this.redBB = new BoundingBox(redSpawnCorner1.getX(), redSpawnCorner1.getY(), redSpawnCorner1.getZ(), redSpawnCorner2.getX(), redSpawnCorner2.getY(), redSpawnCorner2.getZ());
        this.blueBB = new BoundingBox(blueSpawnCorner1.getX(), blueSpawnCorner1.getY(), blueSpawnCorner1.getZ(), blueSpawnCorner2.getX(), blueSpawnCorner2.getY(), blueSpawnCorner2.getZ());
    }

    /***
     * Checks for players and projectiles attempting to camp each team's spawn
     */
    public void checkSpawnCamping() {
        for (Entity e : world.getNearbyEntities(redBB)) {
            if (e instanceof Player p) {
                CTFPlayer cP = ParallelCTF.gameManager.getPlayer(p);
                if (cP.getTeam() == CTFTeam.BLUE || cP == ParallelCTF.gameManager.getBlueFlagCarrier()) {
                    cP.kill();
                    ParallelCTF.sendMessageTo(p, "Do not try to spawn camp!");
                }
            }
            else if (e instanceof Projectile p){
                p.remove();
            }
        }

        for (Entity e : world.getNearbyEntities(blueBB)) {
            if (e instanceof Player p) {
                CTFPlayer cP = ParallelCTF.gameManager.getPlayer(p);
                if (cP.getTeam() == CTFTeam.RED || cP == ParallelCTF.gameManager.getRedFlagCarrier()) {
                    cP.kill();
                    ParallelCTF.sendMessageTo(p, "Do not try to spawn camp!");
                }
            }
            else if (e instanceof Projectile p){
                p.remove();
            }
        }
    }

    /***
     * Checks if a player is in their own spawn
     * @param p The player to check
     * @return If the player is in their spawn
     */
    public boolean isPlayerNotInSpawn(CTFPlayer p) {
        if (p.getTeam() == CTFTeam.BLUE) {
            return !world.getNearbyEntities(blueBB).contains(p.getMcPlayer());
        }
        else {
            return !world.getNearbyEntities(redBB).contains(p.getMcPlayer());
        }
    }

    /***
     * Resets the blue flag
     */
    public void resetBlueFlag() {
        world.getBlockAt(blueFlagPos).getRelative(BlockFace.UP).setType(Material.BLUE_BANNER);
        ParallelCTF.gameManager.setBlueFlagTaken(false);
        ParallelCTF.gameManager.setBlueFlagCarrier(null);
    }

    /***
     * Resets the red flag
     */
    public void resetRedFlag() {
        world.getBlockAt(redFlagPos).getRelative(BlockFace.UP).setType(Material.RED_BANNER);
        ParallelCTF.gameManager.setRedFlagTaken(false);
        ParallelCTF.gameManager.setRedFlagCarrier(null);
    }

    /***
     * Checks a small radius at each flag to see if the flag has been stolen
     */
    public void checkFlagTaken() {
        if (!ParallelCTF.gameManager.isBlueFlagTaken()) {
            for (Entity e : world.getNearbyEntities(blueFlagPos, 1, 2, 1)) {
                if (e instanceof Player p) {
                    CTFPlayer player = ParallelCTF.gameManager.getPlayer(p);
                    if (player.getTeam() == CTFTeam.RED) {
                        if (player.getCtfClass() instanceof NinjaClass ninja && ninja.isInvisible())
                            continue;
                        world.getBlockAt(blueFlagPos).getRelative(BlockFace.UP).setType(Material.AIR);
                        ItemStack banner = new ItemStack(Material.BLUE_BANNER);
                        p.getInventory().setItem(EquipmentSlot.HEAD, banner);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, true, false));
                        ParallelCTF.gameManager.setBlueFlagCarrier(player);
                        ParallelCTF.gameManager.setBlueFlagTaken(true);
                        world.spawnEntity(blueFlagPos.clone().add(0, 50, 0), EntityType.LIGHTNING);
                        ParallelCTF.sendMessage("§c" + p.getName() + " §ahas taken §9Blue's Flag!");
                        break;
                    }
                }
            }
        }
        if (!ParallelCTF.gameManager.isRedFlagTaken()) {
            for (Entity e : world.getNearbyEntities(redFlagPos, 1, 2, 1)) {
                if (e instanceof Player p) {
                    CTFPlayer player = ParallelCTF.gameManager.getPlayer(p);
                    if (player.getTeam() == CTFTeam.BLUE) {
                        if (player.getCtfClass() instanceof NinjaClass ninja && ninja.isInvisible())
                            continue;
                        world.getBlockAt(redFlagPos).getRelative(BlockFace.UP).setType(Material.AIR);
                        ItemStack banner = new ItemStack(Material.RED_BANNER);
                        p.getInventory().setItem(EquipmentSlot.HEAD, banner);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, true, false));
                        ParallelCTF.gameManager.setRedFlagCarrier(player);
                        ParallelCTF.gameManager.setRedFlagTaken(true);
                        world.spawnEntity(redFlagPos.clone().add(0, 50, 0), EntityType.LIGHTNING);
                        ParallelCTF.sendMessage("§9" + p.getName() + " §ahas taken §cRed's Flag!");
                        break;
                    }
                }
            }
        }
    }

    /***
     * Checks a small radius at each flag to see if either flag has been captured
     * If both flags are taken then they both cannot be captured
     */
    public void checkFlagCaptured() {
        if (ParallelCTF.gameManager.isRedFlagTaken() && !ParallelCTF.gameManager.isBlueFlagTaken()) {
            Player player = ParallelCTF.gameManager.getRedFlagCarrier().getMcPlayer();
            if (player.getLocation().distanceSquared(blueFlagPos) < 2) {
                player.getInventory().setItem(EquipmentSlot.HEAD, ParallelCTF.gameManager.getRedFlagCarrier().getCtfClass().armor[3]);
                player.removePotionEffect(PotionEffectType.GLOWING);
                world.spawnEntity(blueFlagPos.clone().add(0, 50, 0), EntityType.LIGHTNING);
                ParallelCTF.sendMessage("§9" + player.getName() + " §ahas captured §cRed's Flag!");
                resetRedFlag();
                ParallelCTF.gameManager.addBlueCapture();
            }
        }
        if (ParallelCTF.gameManager.isBlueFlagTaken() && !ParallelCTF.gameManager.isRedFlagTaken()) {
            Player player = ParallelCTF.gameManager.getBlueFlagCarrier().getMcPlayer();
            if (player.getLocation().distanceSquared(redFlagPos) < 2) {
                player.getInventory().setItem(EquipmentSlot.HEAD, ParallelCTF.gameManager.getBlueFlagCarrier().getCtfClass().armor[3]);
                player.removePotionEffect(PotionEffectType.GLOWING);
                world.spawnEntity(redFlagPos.clone().add(0, 50, 0), EntityType.LIGHTNING);
                ParallelCTF.sendMessage("§c" + player.getName() + " §ahas captured §9Blue's Flag!");
                resetBlueFlag();
                ParallelCTF.gameManager.addRedCapture();
            }
        }
    }
}
