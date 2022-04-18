package de.xeri.league.util.logger.jcolor;

/**
 * Provides a fluent API to generate
 * <a href="https://en.wikipedia.org/wiki/ANSI_escape_code">ANSI escape sequences</a>
 * by specifying {@link Attribute}s of your format.
 */
public class Ansi {

    private static final char ESC = 27;
    private static final String NEWLINE = System.getProperty("line.separator");

    /**
     * Every Ansi escape code begins with this PREFIX.
     */
    public static final String PREFIX = ESC + "[";
    /**
     * Two options must be separated by this SEPARATOR.
     */
    public static final String SEPARATOR = ";";
    /**
     * Every Ansi escape code must end with this POSTFIX.
     */
    public static final String POSTFIX = "m";
    /**
     * Shorthand for the Ansi code that resets to the terminal's default format.
     */
    public static final String RESET = PREFIX + Attribute.CLEAR() + POSTFIX;

    /**
     * @param attributes ANSI attributes to format a text.
     * @return The ANSI code that describes all those attributes together.
     */
    public static String generateCode(Attribute... attributes) {
        final StringBuilder builder = new StringBuilder();

        builder.append(PREFIX);
        for (Object option : attributes) {
            final String code = option.toString();
            if (code.equals(""))
                continue;
            builder.append(code);
            builder.append(SEPARATOR);
        }
        builder.append(POSTFIX);

        return builder.toString().replace(SEPARATOR + POSTFIX, POSTFIX);
    }

  /**
     * @param text     String to format.
     * @param ansiCode Ansi code to format each message's lines.
     * @return The formatted string, ready to be printed.
     */
    public static String colorize(String text, String ansiCode) {
        final StringBuilder output = new StringBuilder();

        output.append(ansiCode);
        final String enclosedFormatting = text.replace(NEWLINE, RESET + NEWLINE + ansiCode);
        output.append(enclosedFormatting);
        output.append(RESET);
        return output.toString();
    }

    /**
     * @param text       String to format.
     * @param attributes ANSI attributes to format a text.
     * @return The formatted string, ready to be printed.
     */
    public static String colorize(String text, Attribute... attributes) {
        final String ansiCode = generateCode(attributes);
        return colorize(text, ansiCode);
    }

}
