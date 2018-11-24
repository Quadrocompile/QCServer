package com.quadrocompile.qcserver.htmltemplates;

import java.util.List;
import java.util.Locale;

public interface QCLocalizedStringFactory {

    String getLocalizedString(String identifier, Locale locale);
    String getLocalizedString(String identifier, Locale locale, List<String> args);

}
