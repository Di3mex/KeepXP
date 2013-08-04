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
