package mine.plugins.lunar.expbalance.config;

import lombok.Getter;
import mine.plugins.lunar.expbalance.cmds.BlockInfoLink;
import mine.plugins.lunar.plugin_framework.cmds.BaseCmd;
import mine.plugins.lunar.plugin_framework.data.DataHandler;
import mine.plugins.lunar.plugin_framework.data.DataInfo;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Paths;
import java.util.List;

public class ConfigManager {

    private static final DataInfo<GeneralConfig> xpDataInfo =
            new DataInfo<>(Paths.get("config"), "settings", GeneralConfig.class);

    private final JavaPlugin plugin;
    @Getter private static GeneralConfig generalConfig;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;

        var dataHandler = new DataHandler(plugin);
        generalConfig = dataHandler.load(xpDataInfo, new GeneralConfig());
        new BaseCmd(new BlockInfoLink(generalConfig), List.of("bi"));
    }

    public void save() {
        var dataHandler = new DataHandler(plugin);
        dataHandler.save(xpDataInfo, generalConfig);

    }
}
