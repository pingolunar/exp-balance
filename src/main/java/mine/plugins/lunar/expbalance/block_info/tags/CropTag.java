package mine.plugins.lunar.expbalance.block_info.tags;

import mine.plugins.lunar.expbalance.block_info.BlockInfoTag;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;

public class CropTag implements BlockInfoTag {

    @Override
    public boolean isBlockValid(Player player, Block block) {
        var blockData = block.getBlockData();

        if (!(blockData instanceof Ageable ageable))
            return false;

        return ageable.getAge() == ageable.getMaximumAge();
    }
}
