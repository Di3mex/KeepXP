package de.diemex.keepxp;


import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Diemex
 */
public class KeepXPPlugin extends JavaPlugin implements Listener
{
    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("keepxp").setExecutor(new Commands());
        new ChestPop(this, getRecipes());
        new ExpBottle(this);
        new ScrollOfKeeping(this);
    }


    /**
     * Get all shapeless recipes as defined in the config
     *
     * @return recipes or empty map
     */
    private ChestItem[] getRecipes()
    {
        reloadConfig();
        saveConfig();
        FileConfiguration recipeFile = getConfig();
        List<ChestItem> chestItems = new ArrayList<ChestItem>();

        if (recipeFile.contains("keepexp.chestitems"))
        {
            ConfigurationSection sec = recipeFile.getConfigurationSection("keepexp.chestitems");
            for (String node : sec.getKeys(false))
            {
                int count = Regex.parseNumber(node);
                Material mat = Material.getMaterial(Regex.stripEnum(node));
                int probability = recipeFile.getInt("keepexp.chestitems." + node, 0);
                if (count > 0)
                {
                    if (mat != null)
                    {
                        chestItems.add(new ChestItem(new ItemStack(mat, count), probability));
                    } else
                    {
                        node = Regex.stripEnum(node);
                        if (node.equalsIgnoreCase("soc"))
                        {
                            int prcntKept = 0;
                            switch (count)
                            {
                                case 1:
                                    prcntKept = 50;
                                    break;
                                case 2:
                                    prcntKept = 70;
                                    break;
                                case 3:
                                    prcntKept = 90;
                                    break;
                                case 4:
                                    prcntKept = 100;
                                    break;
                            }
                            chestItems.add(new ChestItem(ScrollOfKeeping.makeScroll(count, prcntKept), probability));
                        }
                        else if (node.equalsIgnoreCase("emptykeepxp"))
                        {
                            chestItems.add(new ChestItem(ExpBottle.makeEmptyBottle(), probability));
                        }
                        else if (node.equalsIgnoreCase("filledkeepxp"))
                        {
                            chestItems.add(new ChestItem(ExpBottle.makeBottle(ExpBottle.lvlToExp(count), count, "a stranger"), probability));
                        }
                    }
                }
            }
        } else
        {
            recipeFile.set("keepexp.chestitems.." + Material.ICE + "1", 3);
            recipeFile.set("keepexp.chestitems.." + Material.ICE + "2", 2);
            recipeFile.set("keepexp.chestitems.." + Material.ICE + "3", 2);
            recipeFile.set("keepexp.chestitems.." + Material.ICE + "4", 1);
            recipeFile.set("keepexp.chestitems.." + Material.ICE + "5", 1);

            recipeFile.set("keepexp.chestitems." + "soc1", 3);
            recipeFile.set("keepexp.chestitems." + "soc2", 2);
            recipeFile.set("keepexp.chestitems." + "soc3", 1);

            recipeFile.set("keepexp.chestitems." + "emptykeepxp1", 2);
            recipeFile.set("keepexp.chestitems." + "filledkeepxp5", 2);
            recipeFile.set("keepexp.chestitems." + "filledkeepxp8", 1);

            saveConfig();
            return getRecipes();
        }
        return chestItems.toArray(new ChestItem[chestItems.size()]);
    }
}
