package me.naoti.panelapp

import me.naoti.panelapp.utils.hasUppercase
import me.naoti.panelapp.utils.mapBoolean
import me.naoti.panelapp.utils.pickWords
import org.junit.Assert.*
import org.junit.Test

class UtilityUnitTest {
    @Test
    fun testMapBoolean() {
        assertTrue(mapBoolean("1"))
        assertTrue(mapBoolean("enable"))
        assertTrue(mapBoolean("true"))
        assertTrue(mapBoolean("yes"))
        assertTrue(mapBoolean("y"))
        assertTrue(mapBoolean("on"))
        assertTrue(mapBoolean(1))
        assertTrue(mapBoolean(true))
        assertFalse(mapBoolean(null))
        assertFalse(mapBoolean(listOf<String>()))
        assertFalse(mapBoolean(false))
        assertFalse(mapBoolean("Unknown thing"))
    }

    @Test
    fun testHasUppercase() {
        assertTrue("This is a test".hasUppercase())
        assertFalse("this is a test".hasUppercase())
        assertFalse("This is a test".hasUppercase(2))
        assertTrue("This Is a test".hasUppercase(2))
        assertTrue("This Is A test".hasUppercase(2))
    }

    @Test
    fun testWordBankGenerator() {
        assertEquals(pickWords().count(), 3)
        assertEquals(pickWords(5).count(), 5)
    }
}