package com.quadrocompile.qcserver.htmltemplates;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

public class QCTemplateLocalizedStringParam  implements QCTemplateData {

    private static final Logger log = Logger.getLogger(QCTemplateLocalizedStringParam.class);

    private final String paramName;

    public QCTemplateLocalizedStringParam(String paramName){
        this.paramName = paramName.toUpperCase();
    }

    public long write(Writer writer, Map<String, QCTemplateParam> params) throws IOException {
        String data = QCTemplateEngine.getLocalizedStringDefaultLocale(paramName);
        writer.write(data);
        return data.getBytes(StandardCharsets.UTF_8).length;
    }

    public long write(Writer writer, Map<String, QCTemplateParam> params, Locale locale) throws IOException {
        String data = QCTemplateEngine.getLocalizedString(paramName, locale);
        writer.write(data);
        return data.getBytes(StandardCharsets.UTF_8).length;
    }

}
