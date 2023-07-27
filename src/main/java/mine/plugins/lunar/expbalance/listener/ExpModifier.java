package mine.plugins.lunar.expbalance.listener;

import lombok.AllArgsConstructor;
import mine.plugins.lunar.expbalance.config.ConfigManager;
import mine.plugins.lunar.plugin_framework.data.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

@AllArgsConstructor
public class ExpModifier implements Listener {

    private final JavaPlugin plugin;

    //region Xp Calculations
    private int getTotalXp(int xpLevel) {

        if (xpLevel <= 16)
            return xpLevel * xpLevel + 6 * xpLevel;

        if (xpLevel <= 31)
            return (int) (2.5 * xpLevel * xpLevel - 40.5 * xpLevel + 360);

        return (int) (4.5 * xpLevel * xpLevel - 162.5 * xpLevel + 2220);
    }

    private boolean giveCorrectXp(Player player, int xpLevelCost) {
        var previousXpLevel = player.getLevel();
        var currentXpLevel = previousXpLevel - xpLevelCost;

        var customXpSpent = xpLevelCost * ConfigManager.getXpConfig().enchantXp;
        var actualXpSpent = getTotalXp(previousXpLevel) - getTotalXp(currentXpLevel);

        var xpGiven = actualXpSpent - customXpSpent;

        if (Debugger.isDebugActive)
            plugin.getLogger().log(Level.INFO, "Xp given to '"+player.getDisplayName()+"': "+xpGiven);

        var missingXp = player.getTotalExperience() + xpGiven;
        if (missingXp < 0) {
            player.sendMessage(ChatColor.RED+"You need more "+ChatColor.WHITE+(-missingXp)+" xp"+
                ChatColor.RED+" to perform this action");
            return false;
        }

        Bukkit.getScheduler().runTaskLater(plugin,
            () -> player.giveExp(xpGiven), 1);
        return true;
    }
    //endregion

    @EventHandler(ignoreCancelled = true)
    private void modifyEnchantXp(EnchantItemEvent e) {
        var player = e.getEnchanter();
        var xpLevelCost = e.whichButton()+1;

        e.setCancelled(!giveCorrectXp(player, xpLevelCost));
    }

    @EventHandler(ignoreCancelled = true)
    private void modifyAnvilXp(InventoryClickEvent e) {

        var human = e.getWhoClicked();
        if (!(human instanceof Player player))
            return;

        var clickInv = e.getClickedInventory();
        if (!(clickInv instanceof AnvilInventory anvilInv))
            return;

        var clickedItem = e.getCurrentItem();
        if (clickedItem == null) return;

        var invAction = e.getAction();
        if (invAction != InventoryAction.PICKUP_ALL &&
            invAction != InventoryAction.PICKUP_HALF &&
            invAction != InventoryAction.PICKUP_ONE &&
            invAction != InventoryAction.PICKUP_SOME &&
            invAction != InventoryAction.MOVE_TO_OTHER_INVENTORY &&
            invAction != InventoryAction.HOTBAR_SWAP)
                return;

        for (var content : anvilInv.getContents()) /* Only includes the first two slots*/
            if (clickedItem.equals(content))
                return;

        //region AnvilUseEvent
        Bukkit.getLogger().info("rca: "+anvilInv.getRepairCostAmount()+" | mrc: "+anvilInv.getMaximumRepairCost());

        var xpLevelCost =  anvilInv.getRepairCost();
        e.setCancelled(!giveCorrectXp(player, xpLevelCost));
        //endregion
    }

    @EventHandler(ignoreCancelled = true)
    private void modifyAnvilXp(PrepareAnvilEvent e) {

        var result = e.getResult();
        if (result == null || result.getType() == Material.AIR)
            return;

        prepareAnvilRenameEvent(e);
    }

    private void prepareAnvilRenameEvent(PrepareAnvilEvent e) {
        var anvilInv = e.getInventory();

        var anvilContents = anvilInv.getContents();

        var firstItem = anvilContents[0];
        var secondItem = anvilContents[1];

        var firstItemMeta = firstItem.getItemMeta();
        if (firstItemMeta == null) return;

        var firstItemName = firstItemMeta.getDisplayName();
        if (firstItemName.equals(anvilInv.getRenameText()))
            return;

        //region PrepareAnvilRenameEvent
        //endregion
    }
}
