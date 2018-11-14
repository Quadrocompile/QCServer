package com.quadrocompile.qcserver.htmltemplates;

import java.util.List;
import java.util.Locale;

public interface QCLocalizedStringFactory {

    public String getLocalizedString(String identifier, Locale locale);
    public String getLocalizedString(String identifier, Locale locale, List<String> args);

}
