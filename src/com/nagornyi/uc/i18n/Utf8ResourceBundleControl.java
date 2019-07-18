package com.nagornyi.uc.i18n;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utf8ResourceBundleControl extends ResourceBundle.Control {

    private static Logger log = Logger.getLogger(Utf8ResourceBundleControl.class.getName());

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale,
                                    String format, ClassLoader loader,
                                    boolean reload)
            throws IllegalAccessException, InstantiationException, IOException {
        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, "properties");
        URL resourceURL = loader.getResource(resourceName);
        if (resourceURL != null) {
            try {
                return new PropertyResourceBundle(new InputStreamReader(resourceURL.openStream(), StandardCharsets.UTF_8));
            } catch (Exception z) {
                log.log(Level.FINE, "exception thrown during bundle initialization", z);
            }
        }

        return super.newBundle(baseName, locale, format, loader, reload);
    }
}
