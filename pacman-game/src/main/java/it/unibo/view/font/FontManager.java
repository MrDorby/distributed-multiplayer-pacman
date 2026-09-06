package it.unibo.view.font;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public final class FontManager {

    private final static String ROOT = "it/unibo/fonts/";

    private FontManager() {
    }

    public static Font addingFont(float fontSize, String fontName) {
        try {
            final InputStream fontStyle = ClassLoader.getSystemResourceAsStream(ROOT + fontName);
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontStyle)
                    .deriveFont(fontSize)
                    .deriveFont(Font.BOLD);
            fontStyle.close();
            return font;
        } catch (FontFormatException | IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
