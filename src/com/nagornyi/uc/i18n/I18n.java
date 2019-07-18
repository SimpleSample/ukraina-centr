package com.nagornyi.uc.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {

    public static ResourceBundle getBundle(String resourceName, Locale targetLocale) {
        return ResourceBundle.getBundle(resourceName, targetLocale, new Utf8ResourceBundleControl());
    }
}
