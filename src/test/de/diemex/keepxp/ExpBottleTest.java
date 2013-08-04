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


import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Diemex
 */
public class ExpBottleTest
{
    @Test
    public void getLvlToExp_valuesFromWiki_valid()
    {
        assertEquals(272, ExpBottle.lvlToExp(16));
        assertEquals(315, ExpBottle.lvlToExp(18));
        assertEquals(560, ExpBottle.lvlToExp(25));
        assertEquals(825, ExpBottle.lvlToExp(30));
    }

    @Test
    public void getExpToNextLvl_valuesFromWiki_valid()
    {
        assertEquals(20, ExpBottle.getExpToNextLevel(16));
        assertEquals(26, ExpBottle.getExpToNextLevel(18));
        assertEquals(47, ExpBottle.getExpToNextLevel(25));
        assertEquals(62, ExpBottle.getExpToNextLevel(30));
    }
}
