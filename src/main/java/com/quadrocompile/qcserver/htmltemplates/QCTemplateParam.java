package com.quadrocompile.qcserver.htmltemplates;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

public interface QCTemplateParam {

    long write(Writer writer) throws IOException;
    long write(Writer writer, Locale locale) throws IOException;

}
