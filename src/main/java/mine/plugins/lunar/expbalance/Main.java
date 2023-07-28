package mine.plugins.lunar.expbalance;

import mine.plugins.lunar.expbalance.block_info.BlockInfo;
import mine.plugins.lunar.expbalance.block_info.tags.NaturalTag;
import mine.plugins.lunar.expbalance.config.ConfigManager;
import mine.plugins.lunar.expbalance.listener.*;
import mine.plugins.lunar.expbalance.player.XpPlayerData;
import mine.plugins.lunar.expbalance.recipe.BundleCustomRecipe;
import mine.plugins.lunar.plugin_framework.data.Debugger;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class Main extends JavaPlugin {

    private static ConfigManager configManager;

    @Override
    public void onEnable() {
        Debugger.isDebugActive = true;
        Debugger.isFileDebugActive = false;

        BlockInfo.createConfig(this);
        NaturalTag.setNaturalKey(this);

        XpPlayerData.setPlayerHandler(this);

        var pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new ExpGiver(this), this);
        pluginManager.registerEvents(new ExpModifier(this), this);
        pluginManager.registerEvents(new MobSpawnModifier(), this);
        pluginManager.registerEvents(new FortuneBlockApplier(), this);

        configManager = new ConfigManager(this);

        BundleCustomRecipe.enable(this);
    }

    @Override
    public void onDisable() {
        configManager.save();
    }
}
