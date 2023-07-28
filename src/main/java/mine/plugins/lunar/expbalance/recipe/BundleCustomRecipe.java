package mine.plugins.lunar.expbalance.recipe;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class BundleCustomRecipe {

    public static void enable(JavaPlugin plugin) {
        var key = new NamespacedKey(plugin, "leather_bundle");

        var recipe = new ShapedRecipe(key, new ItemStack(Material.BUNDLE));

        recipe.shape("S S", "L L", "LLL");

        recipe.setIngredient('S', Material.STRING);
        recipe.setIngredient('L', Material.LEATHER);

        Bukkit.addRecipe(recipe);
    }
}
