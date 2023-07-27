package mine.plugins.lunar.expbalance.block_info;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface BlockInfoTag {
    boolean isBlockValid(Player player, Block block);
}
