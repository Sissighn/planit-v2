package com.setayesh.planit;

import com.setayesh.planit.ui.Theme;
import com.setayesh.planit.ui.Colors;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Theme class.
 */
class ThemeTest {

    @Test
    void defaultTheme_shouldReturnCorrectColorCodes() {
        Theme theme = Theme.defaultTheme();

        assertNotNull(theme.line(), "Line color should not be null");
        assertNotNull(theme.placeholder(), "Placeholder color should not be null");
        assertNotNull(theme.check(), "Check color should not be null");

        assertEquals(Colors.PASTEL_BROWN, theme.line(), "Default line color mismatch");
        assertEquals(Colors.PASTEL_PINK, theme.placeholder(), "Default placeholder color mismatch");
        assertEquals(Colors.PASTEL_GREEN, theme.check(), "Default check color mismatch");
    }

    @Test
    void getters_shouldReturnSameValuesEachTime() {
        Theme theme = Theme.defaultTheme();

        assertSame(theme.line(), theme.line(), "Line color should be stable");
        assertSame(theme.placeholder(), theme.placeholder(), "Placeholder should be stable");
        assertSame(theme.check(), theme.check(), "Check color should be stable");
    }

    @Test
    void defaultTheme_shouldAlwaysReturnNewInstanceWithSameColors() {
        Theme t1 = Theme.defaultTheme();
        Theme t2 = Theme.defaultTheme();

        assertNotSame(t1, t2, "Each call should create a new Theme instance");
        assertEquals(t1.line(), t2.line());
        assertEquals(t1.placeholder(), t2.placeholder());
        assertEquals(t1.check(), t2.check());
    }
}
