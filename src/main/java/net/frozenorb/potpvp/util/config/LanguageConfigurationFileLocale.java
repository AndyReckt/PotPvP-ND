package net.frozenorb.potpvp.util.config;

public enum LanguageConfigurationFileLocale {
    ENGLISH("en"),
    EXPLICIT("ex"),
    FRENCH("fr"),
    SPANISH("es"),
    PORTUGUESE("pt");

    private final String abbreviation;

    private LanguageConfigurationFileLocale(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public static LanguageConfigurationFileLocale getByAbbreviation(String abbreviation) {
        for ( LanguageConfigurationFileLocale locale : values() ) {
            if (locale.getAbbreviation().equalsIgnoreCase(abbreviation)) {
                return locale;
            }
        }

        return ENGLISH;
    }

    public static LanguageConfigurationFileLocale getByName(String name) {
        for ( LanguageConfigurationFileLocale locale : values() ) {
            if (locale.name().equalsIgnoreCase(name)) {
                return locale;
            }
        }

        return ENGLISH;
    }

    public String getAbbreviation() {
        return this.abbreviation;
    }
}
