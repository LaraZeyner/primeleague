package de.xeri.league.util.logger.jcolor;

class BackColorAttribute extends ColorAttribute {
    /**
     * {@inheritDoc}
     */
    BackColorAttribute(int r, int g, int b) {
        super(r, g, b);
    }

    @Override
    protected String getColorAnsiPrefix() {
        final String ANSI_8BIT_COLOR_PREFIX = "48;5;";
        final String ANSI_TRUE_COLOR_PREFIX = "48;2;";

        return isTrueColor() ? ANSI_TRUE_COLOR_PREFIX : ANSI_8BIT_COLOR_PREFIX;
    }

}
