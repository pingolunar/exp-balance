package mine.plugins.lunar.expbalance.listener;

import mine.plugins.lunar.expbalance.custom_block_data.CustomBlockData;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class PlacedBlocksRegister implements Listener {

    private final JavaPlugin plugin;
    private final NamespacedKey naturalKey;

    public PlacedBlocksRegister(JavaPlugin plugin) {
        this.plugin = plugin;
        naturalKey = new NamespacedKey(plugin, "natural");
    }

    @EventHandler(ignoreCancelled = true)
    private void setBlockAsPlaced(BlockPlaceEvent e) {
        var customBlockData = new CustomBlockData(e.getBlock(), plugin);
        customBlockData.set(naturalKey, PersistentDataType.BOOLEAN, true);
    }

    public boolean isBlockNatural(Block block) {
        var customBlockData = new CustomBlockData(block, plugin);
        var wasBlockPlaced = customBlockData.get(naturalKey, PersistentDataType.BOOLEAN);
        return wasBlockPlaced == null || !wasBlockPlaced;
    }
}
