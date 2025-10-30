package com.setayesh.planit.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.setayesh.planit.ui.UIHelper.Language;
import com.setayesh.planit.ui.UIHelper.DashboardMode;

/**
 * Data Transfer Object (DTO) for user settings.
 * Handles persistent user configuration like language and dashboard mode.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppSettings {
    private Language language = Language.EN;
    private DashboardMode dashboardMode = DashboardMode.COUNTS;

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public DashboardMode getDashboardMode() {
        return dashboardMode;
    }

    public void setDashboardMode(DashboardMode dashboardMode) {
        this.dashboardMode = dashboardMode;
    }
}
