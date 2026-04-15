package UI;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {

    public enum Mode { LIGHT, DARK }

    private static Mode current = Mode.LIGHT;
    private static final List<Runnable> listeners = new ArrayList<>();

    // ── Light palette ──────────────────────────────────────────────
    public static final Color LIGHT_BG          = new Color(242, 244, 247);
    public static final Color LIGHT_CARD        = Color.WHITE;
    public static final Color LIGHT_CARD_BORDER = new Color(225, 225, 225);
    public static final Color LIGHT_TEXT        = new Color(30,  30,  30);
    public static final Color LIGHT_SUBTEXT     = new Color(120, 120, 120);
    public static final Color LIGHT_HOVER       = new Color(170, 190, 180);

    // ── Dark palette ───────────────────────────────────────────────
    public static final Color DARK_BG           = new Color(18,  18,  18);
    public static final Color DARK_CARD         = new Color(30,  30,  30);
    public static final Color DARK_CARD_BORDER  = new Color(55,  55,  55);
    public static final Color DARK_TEXT         = new Color(230, 230, 230);
    public static final Color DARK_SUBTEXT      = new Color(150, 150, 150);
    public static final Color DARK_HOVER        = new Color(70,  90,  80);

    // ── Shared accent ──────────────────────────────────────────────
    public static final Color GREEN_CARD        = new Color(50,  170, 80);
    public static final Color GREEN_TEXT        = new Color(50,  160, 80);
    public static final Color RED_BADGE         = new Color(210, 70,  60);
    public static final Color GREEN_BADGE       = new Color(50,  160, 80);
    public static final Color BLUE_BADGE        = new Color(70,  120, 200);

    // ── Active getters ─────────────────────────────────────────────
    public static boolean isDark()        { return current == Mode.DARK; }
    public static Color bg()              { return isDark() ? DARK_BG          : LIGHT_BG;          }
    public static Color card()            { return isDark() ? DARK_CARD        : LIGHT_CARD;        }
    public static Color cardBorder()      { return isDark() ? DARK_CARD_BORDER : LIGHT_CARD_BORDER; }
    public static Color text()            { return isDark() ? DARK_TEXT        : LIGHT_TEXT;        }
    public static Color subtext()         { return isDark() ? DARK_SUBTEXT     : LIGHT_SUBTEXT;     }
    public static Color hover()           { return isDark() ? DARK_HOVER       : LIGHT_HOVER;       }

    public static void setMode(Mode mode) {
        current = mode;
        for (Runnable r : new ArrayList<>(listeners)) r.run();
    }

    public static void addListener(Runnable r) {
        listeners.add(r);
    }
}