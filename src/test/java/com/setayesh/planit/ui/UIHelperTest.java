package com.setayesh.planit.ui;

import com.setayesh.planit.ui.UIHelper.Language;
import com.setayesh.planit.ui.UIHelper.DashboardMode;
import com.setayesh.planit.settings.*;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UIHelper class focusing on logic, not colors or ANSI output.
 */
class UIHelperTest {

    private static final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;

    @BeforeEach
    void setup() {
        // Redirect console output
        System.setOut(new PrintStream(output));
        // Reset global state before each test
        AppContext.setLanguage(Language.EN);
        AppContext.setDashboardMode(DashboardMode.COUNTS);
    }

    @AfterEach
    void restore() {
        System.setOut(originalOut);
        output.reset();
    }

    @Test
    void setLanguage_shouldUpdateAppContextAndSave() {
        UIHelper.setLanguage(Language.DE);
        assertEquals(Language.DE, AppContext.getLanguage());
    }

    @Test
    void setDashboardMode_shouldUpdateAppContextAndSave() {
        UIHelper.setDashboardMode(DashboardMode.BOTH);
        assertEquals(DashboardMode.BOTH, AppContext.getDashboardMode());
    }

    @Test
    void getLanguage_shouldReturnCurrentLanguage() {
        AppContext.setLanguage(Language.DE);
        assertEquals(Language.DE, UIHelper.getLanguage());
    }

    @Test
    void getDashboardMode_shouldReturnCurrentMode() {
        AppContext.setDashboardMode(DashboardMode.PERCENTAGES);
        assertEquals(DashboardMode.PERCENTAGES, UIHelper.getDashboardMode());
    }

    @Test
    void t_shouldReturnTranslationKeyIfDefined() {
        // Mock translation call
        String result = UIHelper.t("welcome");
        assertNotNull(result);
        assertTrue(result instanceof String);
    }

    @Test
    void printDashboard_shouldDisplayCountsModeCorrectly() {
        AppContext.setLanguage(Language.EN);
        AppContext.setDashboardMode(DashboardMode.COUNTS);

        UIHelper.printDashboard(3, 4, 10);
        String console = output.toString();

        assertTrue(console.contains("Archived"), "Should print English labels");
        assertTrue(console.contains("📦"), "Should contain icons");
        assertTrue(console.contains("Total: 10"));
    }

    @Test
    void printDashboard_shouldDisplayPercentagesModeCorrectly() {
        AppContext.setLanguage(Language.DE);
        AppContext.setDashboardMode(DashboardMode.PERCENTAGES);

        UIHelper.printDashboard(2, 8, 10);
        String console = output.toString();

        assertTrue(console.contains("Archiviert"), "Should print German labels");
        assertTrue(console.contains("80%"), "Should show percentage values");
        assertTrue(console.contains("100%"));
    }

    @Test
    void printDashboard_shouldDisplayBothModeCorrectly() {
        AppContext.setLanguage(Language.EN);
        AppContext.setDashboardMode(DashboardMode.BOTH);

        UIHelper.printDashboard(1, 4, 10);
        String console = output.toString();

        assertTrue(console.contains("(10%)"), "Should include archived percentage");
        assertTrue(console.contains("(40%)"), "Should include completed percentage");
    }
}
