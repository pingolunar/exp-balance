package mine.plugins.lunar.expbalance.block_info.tags;

import mine.plugins.lunar.expbalance.block_info.BlockInfoTag;
import mine.plugins.lunar.expbalance.listener.PlacedBlocksRegister;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class NaturalTag implements BlockInfoTag {

    private static PlacedBlocksRegister placedBlocksRegister;

    public static void setNaturalKey(JavaPlugin plugin) {
        placedBlocksRegister = new PlacedBlocksRegister(plugin);
        plugin.getServer().getPluginManager().registerEvents(placedBlocksRegister, plugin);
    }

    @Override
    public boolean isBlockValid(Player player, Block block) {
        return placedBlocksRegister.isBlockNatural(block);
    }
}
