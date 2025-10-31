package com.setayesh.planit.ui;

public class Theme {
    private final String line;
    private final String placeholder;
    private final String check;

    private Theme(String line, String placeholder, String check) {
        this.line = line;
        this.placeholder = placeholder;
        this.check = check;
    }

    public static Theme defaultTheme() {
        return new Theme(Colors.PASTEL_BROWN, Colors.PASTEL_PINK, Colors.PASTEL_GREEN);
    }

    public String line() {
        return line;
    }

    public String placeholder() {
        return placeholder;
    }

    public String check() {
        return check;
    }
}
