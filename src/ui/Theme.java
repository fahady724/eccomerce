package ui;

import java.awt.Color;
import java.awt.Font;


public final class Theme {
    private Theme() {}

    public static final Color BACKGROUND = Color.decode("#F6F7FB");
    public static final Color SURFACE = Color.WHITE;
    public static final Color SURFACE_SOFT = Color.decode("#EEF2FF");
    public static final Color PRIMARY = Color.decode("#1D4ED8");
    public static final Color PRIMARY_DARK = Color.decode("#1E3A8A");
    public static final Color SECONDARY = Color.decode("#334155");
    public static final Color DANGER = Color.decode("#B91C1C");
    public static final Color TEXT = Color.decode("#0F172A");
    public static final Color TEXT_MUTED = Color.decode("#475569");
    public static final Color BORDER = Color.decode("#CBD5E1");
    public static final Color ERROR = Color.decode("#BA1A1A");
    public static final Color SUCCESS = Color.decode("#15803D");

    public static final Font TITLE = new Font("SansSerif", Font.BOLD, 28);
    public static final Font HEADING = new Font("SansSerif", Font.BOLD, 20);
    public static final Font BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font BODY_BOLD = new Font("SansSerif", Font.BOLD, 14);
    public static final Font SMALL = new Font("SansSerif", Font.PLAIN, 12);
}
