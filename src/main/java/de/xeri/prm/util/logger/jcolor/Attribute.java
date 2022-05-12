package de.xeri.prm.util.logger.jcolor;

/**
 * Abstracts ANSI codes with intuitive names. It maps a description (e.g. RED_TEXT) with a code (e.g. 31).
 * @see <a href="https://en.wikipedia.org/wiki/ANSI_escape_code#Escape_sequences">Wikipedia, for a list of all codes available</a>
 * @see <a href="https://stackoverflow.com/questions/4842424/list-of-ansi-color-escape-sequences/33206814#33206814">StackOverflow, for a list of codes with examples</a>
 */
public abstract class Attribute {

    /**
     * @return The Attribute's ansi escape code.
     */
    @Override
    public abstract String toString();

    /**
     * @return Clears any format. Restores the terminal's default format.
     */
    public static Attribute CLEAR() {
        return new SimpleAttribute("0");
    }

    public static Attribute BOLD() {
        return new SimpleAttribute("1");
    }

    public static Attribute DIM() {
        return new SimpleAttribute("2");
    }

    public static Attribute ITALIC() {
        return new SimpleAttribute("3");
    }

    public static Attribute UNDERLINE() {
        return new SimpleAttribute("4");
    }

    public static Attribute SLOW_BLINK() {
        return new SimpleAttribute("5");
    }

    public static Attribute RAPID_BLINK() {
        return new SimpleAttribute("6");
    }

    public static Attribute REVERSE() {
        return new SimpleAttribute("7");
    }

    public static Attribute HIDDEN() {
        return new SimpleAttribute("8");
    }

    public static Attribute STRIKETHROUGH() {
        return new SimpleAttribute("9");
    }
    
    public static Attribute FRAMED() {
        return new SimpleAttribute("51");
    }
 
    public static Attribute ENCIRCLED() {
        return new SimpleAttribute("52");
    }

    public static Attribute OVERLINED() {
        return new SimpleAttribute("53");
    }

    /**
     *
     * @param r A number (0-255) that represents the red component.
     * @param g A number (0-255) that represents the green component.
     * @param b A number (0-255) that represents the blue component.
     * @return An Attribute that represents a foreground with a true color.
     */
    public static Attribute TEXT_COLOR(int r, int g, int b) {
        return new TextColorAttribute(r, g, b);
    }

    /**
     *
     * @param r A number (0-255) that represents the red component.
     * @param g A number (0-255) that represents the green component.
     * @param b A number (0-255) that represents the blue component.
     * @return An Attribute that represents a background with a true color.
     */
    public static Attribute BACK_COLOR(int r, int g, int b) {
        return new BackColorAttribute(r, g, b);
    }
}

