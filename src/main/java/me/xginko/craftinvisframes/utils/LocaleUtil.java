package me.xginko.craftinvisframes.utils;

import java.util.Locale;
import java.util.regex.Pattern;

public final class LocaleUtil {

    public static final Pattern TRANSLATION_YML_PATTERN = Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)", Pattern.CASE_INSENSITIVE);

    public static Locale localeForLanguageTag(String languageTag) {
        Locale match = Locale.forLanguageTag(languageTag.toLowerCase(Locale.ROOT).replace('_', '-'));
        return match == Locale.ROOT ? Locale.US : match;
    }

    public static String languageTagForLocale(Locale locale) {
        return locale.toString().toLowerCase(Locale.ROOT).replace('-', '_');
    }
}
