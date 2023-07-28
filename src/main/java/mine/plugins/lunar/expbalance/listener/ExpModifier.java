package mine.plugins.lunar.expbalance.listener;

import lombok.AllArgsConstructor;
import mine.plugins.lunar.expbalance.config.ConfigManager;
import mine.plugins.lunar.plugin_framework.data.Debugger;
import mine.plugins.lunar.plugin_framework.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
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

    /**
     * @return The xp amount required to level up
     */
    private int getLevelUpXp(int xpLevel) {
        if (xpLevel <= 15)
            return 2 * xpLevel + 7;

        if (xpLevel <= 30)
            return 5 * xpLevel - 38;

        return 9 * xpLevel - 158;
    }

    /**
     * @return The xp spent if the missing xp is negative, null otherwise
     */
    private Integer giveCorrectXp(Player player, int xpLevelCost, int xpCost) {
        var currentXpLevel = player.getLevel();
        var nextXpLevel = currentXpLevel - xpLevelCost;

        var playerXpProgress = player.getExp();
        var xpSpent = getLevelUpXp(currentXpLevel) * playerXpProgress +
                getLevelUpXp(nextXpLevel) * (1 - playerXpProgress);

        for (int i = nextXpLevel+1; i < currentXpLevel; i++)
            xpSpent += getLevelUpXp(i);

        var xpGiven = (int) xpSpent - xpCost;

        var missingXp = (int) (player.getTotalExperience() - (xpSpent - xpGiven));

        if (Debugger.isDebugActive) {
            plugin.getLogger().log(Level.INFO, "'" + player.getDisplayName() + "' spent: " + xpSpent+" xp");
            plugin.getLogger().log(Level.INFO, "'" + player.getDisplayName() + "' total xp: " + player.getTotalExperience());
            plugin.getLogger().log(Level.INFO, "Xp given to '" + player.getDisplayName() + "': " + xpGiven);
            plugin.getLogger().log(Level.INFO, "'" + player.getDisplayName() + "' missing xp: " + missingXp);
        }

        if (missingXp < 0) {
            player.sendMessage(ChatColor.RED+"You need more "+ChatColor.WHITE+(-missingXp)+" xp"+
                ChatColor.RED+" to perform this action");
            return (int) xpSpent;
        }

        Bukkit.getScheduler().runTask(plugin, () -> player.giveExp(xpGiven));
        return null;
    }

    private Integer giveCorrectEnchantXp(Player player, int buttonPressed) {
        return giveCorrectXp(player, buttonPressed+1,
            ConfigManager.getGeneralConfig().enchantXpCost[buttonPressed]);
    }

    private Integer giveCorrectAnvilXp(Player player, int xpLevelCost) {
        return giveCorrectXp(player, xpLevelCost, ConfigManager.getGeneralConfig().anvilXpCost * xpLevelCost);
    }
    //endregion

    @EventHandler(ignoreCancelled = true)
    private void modifyEnchantXp(EnchantItemEvent e) {
        var player = e.getEnchanter();
        e.setCancelled(giveCorrectEnchantXp(player, e.whichButton()) != null);
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
        var xpLevelCost =  anvilInv.getRepairCost();
        var xpSpent = giveCorrectAnvilXp(player, xpLevelCost);

        if (xpSpent == null)
            return;

        Bukkit.getScheduler().runTask(plugin, () -> player.giveExp(0));
        e.setCancelled(true);
        //endregion
    }

    @EventHandler(ignoreCancelled = true)
    private void modifyAnvilXp(PrepareAnvilEvent e) {
        var anvilInv = e.getInventory();
        anvilInv.setMaximumRepairCost(Integer.MAX_VALUE);

        var result = e.getResult();
        if (result == null || result.getType() == Material.AIR)
            return;

        var renameCost = prepareAnvilRenameEvent(e);
        var enchantCost = prepareAnvilEnchantEvent(e);

        anvilInv.setRepairCost(ConfigManager.getGeneralConfig().anvilBaseLevelCost+renameCost+enchantCost);
    }

    private int prepareAnvilRenameEvent(PrepareAnvilEvent e) {

        var anvilInv = e.getInventory();
        var anvilContents = anvilInv.getContents();

        var firstItem = anvilContents[0];

        var firstItemMeta = firstItem.getItemMeta();
        if (firstItemMeta == null) return 0;

        var firstItemName = firstItemMeta.getDisplayName();
        if (firstItemName.equals(anvilInv.getRenameText()))
            return 0;

        //region PrepareAnvilRenameEvent
        return ConfigManager.getGeneralConfig().itemRenameCost;
        //endregion
    }

    private int prepareAnvilEnchantEvent(PrepareAnvilEvent e) {

        var result = e.getResult();
        if (result == null) return 0;

        var anvilInv = e.getInventory();
        var anvilContents = anvilInv.getContents();

        var secondItem = anvilContents[1];
        if (secondItem == null) return 0;

        int totalRepairCost = 0;

        var firstItem = anvilContents[0];
        var firstItemEnchants = ItemBuilder.getEnchantments(firstItem);

        var secondItemEnchants = ItemBuilder.getEnchantments(secondItem);

        var newResultEnchantments = new HashMap<Enchantment, Integer>();

        for (var firstEnchant : firstItemEnchants.keySet()) {
            totalRepairCost += addResultEnchant(firstItemEnchants, secondItemEnchants, newResultEnchantments, firstEnchant);
        }

        for (var secondEnchant : secondItemEnchants.keySet()) {
            if (newResultEnchantments.containsKey(secondEnchant)) continue;
            totalRepairCost += addResultEnchant(firstItemEnchants, secondItemEnchants, newResultEnchantments, secondEnchant);
        }

        var newResult = new ItemBuilder(result).setEnchantments(newResultEnchantments).get();
        e.setResult(newResult);

        return totalRepairCost;
    }

    private int addResultEnchant(Map<Enchantment, Integer> firstItemEnchants, Map<Enchantment, Integer> secondItemEnchants,
                                Map<Enchantment, Integer> newResultEnchantments, Enchantment enchantment) {

        var firstEnchantLevel = firstItemEnchants.get(enchantment);
        var secondEnchantLevel = secondItemEnchants.get(enchantment);

        if (secondEnchantLevel == null) secondEnchantLevel = 0;
        if (firstEnchantLevel == null) firstEnchantLevel = 0;

        var bestEnchantLevel = Math.max(firstEnchantLevel, secondEnchantLevel);
        var doesEnchantLevelUp = firstEnchantLevel.equals(secondEnchantLevel);

        var addedCost = doesEnchantLevelUp ? ConfigManager.getGeneralConfig().enchantLevelUpCost * bestEnchantLevel : 0;
        newResultEnchantments.put(enchantment, bestEnchantLevel+(doesEnchantLevelUp ? 1 : 0));
        return addedCost;
    }

}
