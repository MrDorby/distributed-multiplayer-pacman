package it.unibo.view.font;

public enum FontName {
    S2P("S2P.ttf");

    private final String fontName;

    FontName(String fontName) {
        this.fontName = fontName;
    }

    public String getFontName() {
        return this.fontName;
    }
}
