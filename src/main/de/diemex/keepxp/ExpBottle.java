package de.diemex.keepxp;


import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Diemex
 */
public class ExpBottle implements Listener
{
    private final Plugin plugin;
    private final Map<Material, Integer> recipes;


    public ExpBottle(Plugin plugin)
    {
        this.plugin = plugin;
        //Register our custom recipes
        recipes = getRecipes();
        for (Map.Entry<Material, Integer> ingredients : recipes.entrySet())
        {
            ShapelessRecipe steak = new ShapelessRecipe(new ItemStack(Material.EXP_BOTTLE));
            steak.addIngredient(Material.GLASS_BOTTLE);
            steak.addIngredient(ingredients.getValue(), ingredients.getKey());
            plugin.getServer().addRecipe(steak);
        }
        //Empty exp bottles -> keepxp bottles
        ShapelessRecipe empty = new ShapelessRecipe(new ItemStack(Material.EXP_BOTTLE));
        empty.addIngredient(Material.EXP_BOTTLE);
        plugin.getServer().addRecipe(empty);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    /**
     * Get all shapeless recipes as defined in the config
     *
     * @return recipes or empty map
     */
    private Map<Material, Integer> getRecipes()
    {
        plugin.reloadConfig();
        plugin.saveConfig();
        FileConfiguration recipeFile = plugin.getConfig();
        Map<Material, Integer> recipes = new HashMap<Material, Integer>();

        if (recipeFile.contains("keepexp.recipes"))
        {
            ConfigurationSection sec = recipeFile.getConfigurationSection("keepexp.recipes");
            for (String node : sec.getKeys(false))
            {
                Material mat = Material.getMaterial(node);
                if (mat != null)
                {
                    int count = recipeFile.getInt("keepexp.recipes." + node, -1);
                    if (count > 0)
                    {
                        recipes.put(mat, count);
                    }
                }
            }
        } else
        {
            recipeFile.set("keepexp.recipes." + Material.GOLD_BLOCK, 3);
            recipeFile.set("keepexp.recipes." + Material.LAPIS_BLOCK, 6);
            recipes.put(Material.GOLD_BLOCK, 3);
            recipes.put(Material.LAPIS_BLOCK, 6);
            plugin.saveConfig();
        }
        return recipes;
    }


    @EventHandler
    private void onPreExpBottleCraft(PrepareItemCraftEvent event)
    {
        if (event.getRecipe() instanceof ShapelessRecipe && event.getRecipe().getResult().getType() == Material.EXP_BOTTLE && event.getViewers().size() > 0 && event.getViewers().get(0) instanceof Player)
        {
            final Player player = (Player) event.getViewers().get(0);
            //Player requires the permission
            if (!player.hasPermission("keepxp.craft"))
            {
                event.getInventory().setResult(null);
                return;
            }

            //Empty Exp Bottle from dungeons etc.
            ShapelessRecipe recipe = (ShapelessRecipe) event.getRecipe();

            if (isEmptyKeepXpBottle(recipe.getIngredientList().get(0))) ;
            else
            {
                //Merge single ItemStacks
                Map<Material, Integer> ingredients = new HashMap<Material, Integer>();
                for (ItemStack stack : recipe.getIngredientList())
                    if (ingredients.containsKey(stack.getType()))
                        ingredients.put(stack.getType(), ingredients.get(stack.getType()) + stack.getAmount()); //Amount is usually always 1

                //Check if the recipe matches and break loop once found, otherwise return and not execute code further down
                search:
                for (Map.Entry<Material, Integer> stack : ingredients.entrySet())
                {
                    if (stack.getKey() == Material.GLASS_BOTTLE)
                        continue;
                    else
                        for (Map.Entry<Material, Integer> item : recipes.entrySet())
                            if (stack.getValue().equals(item.getValue()) && stack.getKey() == item.getKey())
                                break search;
                    return;
                }
            }

            event.getInventory().setResult(makeBottle(player.getTotalExperience(), player.getLevel(), player.getName()));
        }
    }


    @EventHandler
    private void onExpBottleCraft(CraftItemEvent event)
    {
        int exp = getBottleExp(event.getInventory().getResult());
        if (exp > 0 && event.getWhoClicked() instanceof Player)
        {
            if (event.isShiftClick()) //NO shift clicking multiple bottles out of the inventory
            {
                event.setCancelled(true);
            } else
            {
                final Player player = (Player) event.getWhoClicked();

                player.setTotalExperience(0);
                player.setLevel(0);
                player.setExp(0F);
            }
        }
    }


    Map<String, Integer> playerBottles = new HashMap<String, Integer>(1);


    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event)
    {
        int bottleExp = getBottleExp(event.getItem());
        if (event.getMaterial() == Material.EXP_BOTTLE && bottleExp >= 0)
        {
            playerBottles.put(event.getPlayer().getName(), bottleExp);
        }
    }


    @EventHandler
    private void onExpBottleThrow(ExpBottleEvent event)
    {
        if (event.getEntity().getShooter() instanceof Player)
        {
            final Player player = (Player) event.getEntity().getShooter();
            if (playerBottles.containsKey(player.getName()))
            {
                event.setExperience(playerBottles.get(player.getName()));
                playerBottles.remove(player.getName());
            }
        }
    }


    public static ItemStack makeBottle(int exp, int lvl, String player)
    {
        ItemStack stack = new ItemStack(Material.EXP_BOTTLE);

        ItemMeta meat = stack.getItemMeta();
        meat.setDisplayName("Bottle o' Exp");

        List<String> lore = new ArrayList<String>();
        lore.add("This bottle has captured");
        lore.add(String.format("%d lvls from %s", lvl, player));
        lore.add("Throw it on the floor to");
        lore.add("regather the knowledge!");
        lore.add("");
        lore.add(String.format("Lvl 0 -> %d", lvl));
        lore.add(String.format("%d Exp Points", exp));
        meat.setLore(lore);

        stack.setItemMeta(meat);

        return stack;
    }


    public static ItemStack makeEmptyBottle()
    {
        ItemStack stack = new ItemStack(Material.EXP_BOTTLE);

        ItemMeta meat = stack.getItemMeta();
        meat.setDisplayName("Bottle o' Exp");

        List<String> lore = new ArrayList<String>();
        lore.add("This bottle can capture");
        lore.add("your exp for latter use.");
        lore.add("");
        lore.add("Just put it in a crafting");
        lore.add("grid and all your lvls will");
        lore.add("be captured in the bottle.");
        lore.add("");
        lore.add("Empty KeepXP Bottle");
        meat.setLore(lore);

        stack.setItemMeta(meat);
        return stack;
    }


    /**
     * Check if the given Item is an ExpBottle
     *
     * @param stack stack to check
     *
     * @return -1 if not, otherwise the amount of exp this bottle contains
     */
    private int getBottleExp(ItemStack stack)
    {
        int lvl = -1;
        if (stack != null && stack.getType() == Material.EXP_BOTTLE)
        {
            if (stack.hasItemMeta())
            {
                ItemMeta meat = stack.getItemMeta();
                String lastLine = meat.getLore().size() > 0 ? meat.getLore().get(meat.getLore().size() - 1) : "";
                Pattern expLine = Pattern.compile("[0-9]* Exp Points");
                if (expLine.matcher(lastLine).find())
                {
                    lastLine = Pattern.compile("[^0-9]").matcher(lastLine).replaceAll("");
                    if (lastLine.length() > 0)
                    {
                        lvl = Integer.parseInt(lastLine);
                    }
                }
            }
        }
        return lvl;
    }


    private boolean isEmptyKeepXpBottle(ItemStack stack)
    {
        if (stack.getType() == Material.EXP_BOTTLE)
        {
            if (stack.hasItemMeta())
            {
                ItemMeta meat = stack.getItemMeta();
                List<String> lore = meat.getLore();
                if (lore.size() > 0)
                {
                    Pattern lastMatcher = Pattern.compile("Empty KeepXP Bottle");
                    if (lastMatcher.matcher(lore.get(lore.size() - 1)).find())
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * Get the amount of experience needed to reach a given lvl
     */
    public static int lvlToExp(int level)
    {
        int exp = 0;
        for (int currentLevel = 0; currentLevel < level; currentLevel++)
            exp += getExpToNextLevel(currentLevel);
        return exp;
    }


    public static int getExpToNextLevel(int currentLevel)
    {
        //Copied straight from mojang :D
        return currentLevel >= 30 ? 62 + (currentLevel - 30) * 7 :
                (currentLevel >= 15 ? 17 + (currentLevel - 15) * 3 :
                        17);
    }
}
