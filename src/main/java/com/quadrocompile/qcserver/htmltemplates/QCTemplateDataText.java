package com.quadrocompile.qcserver.htmltemplates;

import com.quadrocompile.qcserver.util.QCUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class QCTemplateDataText implements QCTemplateData {
    private static final Logger log = Logger.getLogger(QCUtil.class);

    private final String data;
    private final long dataBytes;
    public QCTemplateDataText(String data){
        long bytes = 0;
        try {
            bytes = data.getBytes(StandardCharsets.UTF_8).length;
        }
        catch (Exception ex){
            log.error(ex);
        }

        this.data = data;
        this.dataBytes = bytes;
    }
    public long write(Writer writer, Map<String, QCTemplateParam> params) throws IOException {
        writer.write(data);
        return dataBytes;
    }
}
