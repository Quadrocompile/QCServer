package com.quadrocompile.qcserver.htmltemplates;

import java.util.Locale;

public interface QCLocalizedStringFactory {

    public String getLocalizedString(String identifier, Locale locale);

}
