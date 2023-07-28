package mine.plugins.lunar.expbalance.config;

import java.io.Serializable;

public class GeneralConfig implements Serializable {

    public int shearXp = 10;

    public Integer[] enchantXpCost = { 200, 1200, 2000 };

    public int anvilXpCost = 50;
    public int anvilBaseLevelCost = 2;

    public int enchantLevelUpCost = 4;
    public int itemRenameCost = 1;
}
