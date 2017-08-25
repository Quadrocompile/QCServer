package com.quadrocompile.qcserver.htmltemplates;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

public class QCHTMLTemplate {

    private final List<QCTemplateData> data;

    public QCHTMLTemplate(List<QCTemplateData> data) {
        this.data = data;
    }

    public long writeToStream(OutputStreamWriter outputStream, Map<String, QCTemplateParam> params) throws IOException {
        long byteCount = 0;

        for (QCTemplateData dateEntry : data) {
            byteCount += dateEntry.write(outputStream, params);
        }

        return byteCount;
    }

}
