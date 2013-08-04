package de.diemex.keepxp;


import org.bukkit.inventory.ItemStack;

/**
 * @author Diemex
 */
public class ChestItem
{
    public ChestItem(ItemStack toAdd, int probability)
    {
        this.toAdd = toAdd;
        this.probability = probability;
    }


    public ItemStack toAdd;
    public int probability;
}
