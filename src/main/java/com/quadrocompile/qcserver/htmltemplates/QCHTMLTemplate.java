package com.quadrocompile.qcserver.htmltemplates;

import com.quadrocompile.qcserver.htmltemplates.paramdata.QCTemplateParam;
import com.quadrocompile.qcserver.htmltemplates.staticdata.QCTemplateData;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class QCHTMLTemplate {

    private final List<QCTemplateData> data;

    public QCHTMLTemplate(List<QCTemplateData> data) {
        this.data = data;
    }

    public long writeToStream(Writer writer, Map<String, QCTemplateParam> params) throws IOException {
        long byteCount = 0;

        for (QCTemplateData dateEntry : data) {
            byteCount += dateEntry.write(writer, params);
        }

        return byteCount;
    }

    public long writeToStream(Writer writer, Map<String, QCTemplateParam> params, Locale locale) throws IOException {
        long byteCount = 0;

        for (QCTemplateData dateEntry : data) {
            byteCount += dateEntry.write(writer, params, locale);
        }

        return byteCount;
    }

}
