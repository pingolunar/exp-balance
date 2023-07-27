package mine.plugins.lunar.expbalance.block_info;

import mine.plugins.lunar.plugin_framework.data.DataHandler;
import mine.plugins.lunar.plugin_framework.data.Debugger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

public record BlockInfo(int xp, HashSet<BlockInfoTag> tags) {

    /**
     * @return If the block is validated by all tags
     */
    public boolean areTagsValid(Player player, Block block) {
        for (var tag : tags)
            if (!tag.isBlockValid(player, block))
                return false;
        return true;
    }

    //region Static Data
    private static final HashMap<Material, BlockInfo> blocksInfo = new HashMap<>();

    public static @Nullable BlockInfo get(Material type) {
        return blocksInfo.get(type);
    }
    //endregion

    //region IO
    private static final Path dataPath = Paths.get("blocks_info");

    public static void load(JavaPlugin plugin) {
        var dataHandler = new DataHandler(plugin);
        var blocksInfoFilesNames = dataHandler.listFilesName(dataPath);

        blocksInfoFilesNames.forEach(fileName -> {
            var cleanFileName = dataHandler.removeExtension(fileName);

            var blockInfoStr = dataHandler.loadTxt(dataPath, cleanFileName, 2);
            if (blockInfoStr.length != 2) return;

            try {
                var type = Material.valueOf(cleanFileName.toUpperCase());
                var xp = Integer.parseInt(blockInfoStr[0]);

                var tags = new HashSet<BlockInfoTag>();

                var fileTags = blockInfoStr[1].split(" ");
                for (var fileTag : fileTags)
                    if (!fileTag.equals(""))
                        tags.add(BlockInfoTagType.valueOf(fileTag.toUpperCase()).getBlockInfoTag());

                var blockInfo = new BlockInfo(xp, tags);
                blocksInfo.put(type, blockInfo);

                if (Debugger.isFileDebugActive)
                    plugin.getLogger().log(Level.INFO, "Loaded "+fileName);

            } catch (IllegalArgumentException ignored) {
                plugin.getLogger().log(Level.WARNING, "Failed to load "+fileName);
            }


        });
    }

    private static final Path configPath = Paths.get("config_info");

    public static void createConfig(JavaPlugin plugin) {
        var dataHandler = new DataHandler(plugin);

        dataHandler.saveTxt(configPath, "all_blocks", Arrays.stream(Material.values())
                .map(type -> type.name().toLowerCase()).toArray(String[]::new));

        dataHandler.saveTxt(configPath, "all_tags", Arrays.stream(BlockInfoTagType.values())
                .map(type -> type.name().toLowerCase()).toArray(String[]::new));
    }
    //endregion
}
