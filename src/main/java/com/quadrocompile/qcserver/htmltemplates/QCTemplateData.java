package com.quadrocompile.qcserver.htmltemplates;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

public interface QCTemplateData {

    long write(Writer writer, Map<String, QCTemplateParam> params) throws IOException;
    long write(Writer writer, Map<String, QCTemplateParam> params, Locale locale) throws IOException;

}
