package mine.plugins.lunar.expbalance.player;

import lombok.Getter;
import lombok.NonNull;
import mine.plugins.lunar.plugin_framework.player.OnlinePlayerData;
import mine.plugins.lunar.plugin_framework.player.OnlinePlayerDataListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class XpPlayerData extends OnlinePlayerData {

    public XpPlayerData(JavaPlugin plugin, @NonNull Player player) {
        super(plugin, player);
    }

    //region Handler
    @Getter private static XpPlayerDataHandler xpPlayerDataHandler;

    public static void setPlayerHandler(JavaPlugin plugin) {
        xpPlayerDataHandler = new XpPlayerDataHandler(plugin);
        plugin.getServer().getPluginManager().registerEvents(new OnlinePlayerDataListener(xpPlayerDataHandler), plugin);
    }
    //endregion

}
