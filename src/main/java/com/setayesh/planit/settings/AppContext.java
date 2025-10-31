package com.setayesh.planit.settings;

import com.setayesh.planit.ui.UIHelper.Language;
import com.setayesh.planit.ui.UIHelper.DashboardMode;

/**
 * Central application context.
 * Holds global state such as the current language and dashboard mode.
 * Keeps UIHelper stateless and focused on display logic.
 */
public class AppContext {
    private static Language language = Language.EN;
    private static DashboardMode dashboardMode = DashboardMode.COUNTS;

    // -------------------- LANGUAGE -------------------- //
    public static Language getLanguage() {
        return language;
    }

    public static void setLanguage(Language lang) {
        language = lang;
    }

    // -------------------- DASHBOARD MODE -------------------- //
    public static DashboardMode getDashboardMode() {
        return dashboardMode;
    }

    public static void setDashboardMode(DashboardMode mode) {
        dashboardMode = mode;
    }
}
