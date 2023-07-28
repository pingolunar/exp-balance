package mine.plugins.lunar.expbalance.listener;

import mine.plugins.lunar.expbalance.block_info.BlockInfoTagType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FortuneBlockApplier implements Listener {

    private void modifyLooting(Location dropLocation, Player player, List<ItemStack> drops) {
        modifyDrops(dropLocation, player, drops, Enchantment.LOOT_BONUS_MOBS);
    }

    private void modifyFortune(Block block, Player player, List<ItemStack> drops) {
        if (!areBlockDropsValid(block, player)) return;
        modifyDrops(block.getLocation(), player, drops, Enchantment.LOOT_BONUS_BLOCKS);
    }

    private boolean areBlockDropsValid(Block block, Player player) {
        if (!BlockInfoTagType.TOOL.getBlockInfoTag().isBlockValid(player, block))
            return false;

        return BlockInfoTagType.NATURAL.getBlockInfoTag().isBlockValid(player, block) ||
                BlockInfoTagType.CROP.getBlockInfoTag().isBlockValid(player, block);
    }

    private void modifyDrops(Location dropLocation, Player player, List<ItemStack> drops, Enchantment modifierEnchantment) {

        var world = dropLocation.getWorld();
        if (world == null) return;

        var mainHandItem = player.getInventory().getItemInMainHand();
        var toolEnchants = mainHandItem.getEnchantments();

        var fortuneEnchantLevel = toolEnchants.get(modifierEnchantment);
        if (fortuneEnchantLevel == null) return;

        for (var drop : drops) {
            var dropAmount = drop.getAmount(); Bukkit.getLogger().info("drop:"+drop.getType()+" | amount: "+dropAmount);
            var enhancedDropAmount = dropAmount * fortuneEnchantLevel;
            world.dropItemNaturally(dropLocation, new ItemStack(drop.getType(), enhancedDropAmount));
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void modifyFortune(BlockDropItemEvent e) {

        var block = e.getBlockState().getBlock();
        var player = e.getPlayer();

        modifyFortune(block, player, e.getItems().stream().map(Item::getItemStack).toList());
    }

    @EventHandler(ignoreCancelled = true)
    private void modifyFortune(PlayerHarvestBlockEvent e) {

        var block = e.getHarvestedBlock();
        var player = e.getPlayer();

        modifyFortune(block, player, e.getItemsHarvested());
    }

    @EventHandler(ignoreCancelled = true)
    private void modifyLooting(EntityDeathEvent e) {

        var player = e.getEntity().getKiller();
        if (player == null) return;

        var dropLocation = e.getEntity().getLocation();

        modifyLooting(dropLocation, player, e.getDrops());
    }
}
