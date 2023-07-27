package mine.plugins.lunar.expbalance.block_info.tags;

import mine.plugins.lunar.expbalance.block_info.BlockInfoTag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ToolTag implements BlockInfoTag {

    @Override
    public boolean isBlockValid(Player player, Block block) {
        var mainHandItem = player.getInventory().getItemInMainHand();
        return block.getBlockData().isPreferredTool(mainHandItem);
    }
}
