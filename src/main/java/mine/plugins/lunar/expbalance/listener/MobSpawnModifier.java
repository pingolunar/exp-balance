package mine.plugins.lunar.expbalance.listener;

import org.bukkit.World;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class MobSpawnModifier implements Listener {

    private static final int seaLevel = 62;

    @EventHandler(ignoreCancelled = true)
    private void cancelHostileMobSpawnBelowSeaLevel(EntitySpawnEvent e) {
        var entity = e.getEntity();

        if (!(entity instanceof Monster monster)
            || monster.getWorld().getEnvironment() != World.Environment.NORMAL
            || monster.getLocation().getY() < seaLevel)
            return;

        e.setCancelled(true);
    }

    //TODO: mobs get stronger equipment & attributes the deeper they spawn, also drop more xp
}
