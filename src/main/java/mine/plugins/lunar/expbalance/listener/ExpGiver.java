package mine.plugins.lunar.expbalance.listener;

import mine.plugins.lunar.expbalance.block_info.BlockInfo;
import mine.plugins.lunar.expbalance.config.ConfigManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ExpGiver implements Listener {

    public ExpGiver(JavaPlugin plugin) {
        BlockInfo.load(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    private void giveExpOnBlockBreak(BlockBreakEvent e) {
        var block = e.getBlock();

        var blockInfo = BlockInfo.get(block.getType());
        if (blockInfo == null || !blockInfo.areTagsValid(e.getPlayer(), block)) return;

        e.setExpToDrop(blockInfo.xp());
    }

    @EventHandler(ignoreCancelled = true)
    private void giveExpOnBlockHarvest(PlayerHarvestBlockEvent e) {
        var block = e.getHarvestedBlock();

        var blockInfo = BlockInfo.get(block.getType());
        if (blockInfo == null || !blockInfo.areTagsValid(e.getPlayer(), block)) return;

        spawnXpOrb(block.getLocation(), blockInfo.xp());
    }

    private void spawnXpOrb(Location location, int xpAmount) {
        var world = location.getWorld();
        if (world == null) return;

        var xpOrb = (ExperienceOrb) world.spawnEntity(location, EntityType.EXPERIENCE_ORB);
        xpOrb.setExperience(xpAmount);
    }

    @EventHandler(ignoreCancelled = true)
    private void giveExpOnShear(PlayerShearEntityEvent e) {
        spawnXpOrb(e.getEntity().getLocation(), ConfigManager.getGeneralConfig().shearXp);
    }
}
