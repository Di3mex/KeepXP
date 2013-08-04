/*
 * This file is part of
 * KeepXP Server Plugin for Minecraft
 *
 * Copyright (C) 2013 Diemex
 *
 * KeepXP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KeepXP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero Public License
 * along with KeepXP.  If not, see <http://www.gnu.org/licenses/>.
 */

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
