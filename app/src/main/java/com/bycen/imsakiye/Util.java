package com.bycen.imsakiye;

import java.util.Locale;

public final class Util {

    public static Locale locale() {
        final String language = "tr";
        final String country = "TR";
        return new Locale(language, country);
    }
}