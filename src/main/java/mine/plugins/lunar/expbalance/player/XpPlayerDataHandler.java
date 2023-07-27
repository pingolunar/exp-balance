package mine.plugins.lunar.expbalance.player;

import mine.plugins.lunar.plugin_framework.player.OnlinePlayerDataHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class XpPlayerDataHandler extends OnlinePlayerDataHandler<XpPlayerData> {

    public XpPlayerDataHandler(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    protected XpPlayerData getDefaultPlayerData(Player player) {
        return new XpPlayerData(plugin, player);
    }
}
