package mine.plugins.lunar.expbalance.listener;

import mine.plugins.lunar.expbalance.block_info.BlockInfoTagType;
import mine.plugins.lunar.expbalance.config.ConfigManager;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class FortuneBlockApplier implements Listener {

    @EventHandler(ignoreCancelled = true)
    private void giveExpOnBlockBreak(BlockBreakEvent e) {

        var block = e.getBlock();
        var drops = block.getDrops();

        var player = e.getPlayer();
        if (!BlockInfoTagType.NATURAL.getBlockInfoTag().isBlockValid(player, block))
            return;

        if (drops.size() > 1)
            return;
//TODO apply to all block, override drops
        var blockType = block.getType();
        var onlyDrop = drops.stream().findFirst();

        if (onlyDrop.isEmpty() || onlyDrop.get().getType() != blockType)
            return;

        var mainHandItem = player.getInventory().getItemInMainHand();

        if (!block.isPreferredTool(mainHandItem))
            return;

        var blockLoc = block.getLocation();

        var blockWorld = blockLoc.getWorld();
        if (blockWorld == null) return;

        var toolEnchants = mainHandItem.getEnchantments();

        var fortuneEnchantLevel = toolEnchants.get(Enchantment.LOOT_BONUS_BLOCKS);
        if (fortuneEnchantLevel == null) fortuneEnchantLevel = 0;

        var lootingEnchantLevel = toolEnchants.get(Enchantment.LOOT_BONUS_MOBS);
        if (lootingEnchantLevel == null) lootingEnchantLevel = 0;

        var bestLuckEnchantLevel = Math.max(fortuneEnchantLevel, lootingEnchantLevel);

        var dropRate = ConfigManager.getXpConfig().luckEnchantMultiplier * bestLuckEnchantLevel;
        var additionalDrops = (int) Math.floor(dropRate / 100D);

        if (additionalDrops != 0)
            blockWorld.dropItemNaturally(blockLoc, new ItemStack(blockType, additionalDrops));

        var lastDropChance = dropRate % 100D;
        if (ThreadLocalRandom.current().nextDouble() < lastDropChance)
            blockWorld.dropItemNaturally(blockLoc, new ItemStack(blockType, 1));
    }
}
