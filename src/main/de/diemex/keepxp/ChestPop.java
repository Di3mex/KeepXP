package de.diemex.keepxp;


import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Diemex
 */
public class ChestPop implements Listener
{
    private final Plugin plugin;
    private final ChestItem[] items;
    int probScale = 0;
    private final boolean debug = true;


    public ChestPop(Plugin plugin, ChestItem[] items)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.items = items;
        for (ChestItem item : items)
            probScale += item.probability;
    }


    /**
     * Be "super efficient" and loop through chunk till we have found chests
     */
    @EventHandler
    private void onChunkGen(ChunkPopulateEvent event)
    {
        Chunk chunk = event.getChunk();

        //From lvl10 - 50
        for (int y = 20; y < 50; y++)
        {
            for (int x = 0; x <= 15; x++)
            {
                for (int z = 0; z <= 15; z++)
                {
                    Block block = chunk.getBlock(x, y, z);
                    Material type = block.getType();
                    if (type == Material.CHEST)
                    {
                        Chest chest = (Chest) block.getState();
                        addItems(chest.getBlockInventory());
                    }
                }
            }
        }

        for (Entity entity : chunk.getEntities())
        {
            if (entity instanceof StorageMinecart)
            {
                StorageMinecart minecart = (StorageMinecart)entity;
                addItems(minecart.getInventory());
            }
        }
    }


    private void addItems(Inventory inv)
    {
        int chance = 50;
        Random rdm = new Random();
        if (rdm.nextInt(100) < chance)
        {
            for (ChestItem item : items)
            {
                if (rdm.nextInt(probScale) < item.probability)
                {
                    if (inv.firstEmpty() >= 0)
                    {
                        //Find a random empty spot
                        List<Integer> freeSlots = new ArrayList<Integer>();
                        int i = -1;
                        for (ItemStack stack : inv.getContents())
                            if (stack == null & ++i >= 0) //lol
                                freeSlots.add(i);
                        int slot = freeSlots.get(rdm.nextInt(freeSlots.size() - 1));
                        if (debug) plugin.getLogger().info("Added " + item.toAdd + " at " + inv + " in slot " + slot);
                        inv.setItem(slot, item.toAdd);
                        if (rdm.nextBoolean()) //50% prob of more items
                            break;
                    }
                }
            }
        }
    }
}
