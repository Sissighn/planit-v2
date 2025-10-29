package com.setayesh.planit.util;

import java.util.regex.Pattern;

/**
 * ANSI-aware string utilities:
 * - strip(): remove ANSI color codes
 * - visibleLength(): length without ANSI codes
 * - clipVisible(): clip by visible width without breaking escape sequences
 * - padRight(): pad by visible width
 */
public final class AnsiUtils {

    private AnsiUtils() {
    }

    // Matches ANSI escape sequences like "\u001B[38;5;120m"
    private static final Pattern ANSI_PATTERN = Pattern.compile("\\u001B\\[[;\\d]*m");

    /** Remove all ANSI escape codes. */
    public static String strip(String s) {
        if (s == null)
            return "";
        return ANSI_PATTERN.matcher(s).replaceAll("");
    }

    /** Visible character length (ignores ANSI codes). */
    public static int visibleLength(String s) {
        return strip(s).length();
    }

    /**
     * Clip string to a given visible width (ANSI-safe).
     * Keeps escape sequences intact and never cuts them in half.
     */
    public static String clipVisible(String s, int maxVisible) {
        if (s == null)
            return "";
        if (maxVisible <= 0)
            return "";
        StringBuilder out = new StringBuilder();
        int visibleCount = 0;
        for (int i = 0; i < s.length() && visibleCount < maxVisible; i++) {
            char c = s.charAt(i);
            if (c == 0x1B) { // ESC
                int j = i + 1;
                if (j < s.length() && s.charAt(j) == '[') {
                    j++;
                    while (j < s.length() && s.charAt(j) != 'm')
                        j++;
                    if (j < s.length())
                        j++; // include 'm'
                }
                out.append(s, i, j);
                i = j - 1;
            } else {
                out.append(c);
                visibleCount++;
            }
        }
        return out.toString();
    }

    /** Pad with spaces on the right to reach target visible width. */
    public static String padRight(String s, int width) {
        if (s == null)
            s = "";
        int pad = width - visibleLength(s);
        return s + " ".repeat(Math.max(0, pad));
    }
}
