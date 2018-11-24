package com.quadrocompile.qcserver.htmltemplates.staticdata;

import com.quadrocompile.qcserver.htmltemplates.paramdata.QCTemplateParam;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

public class QCTemplateDataText implements QCTemplateData {
    private static final Logger log = Logger.getLogger(QCTemplateDataText.class);

    private final char[] data;
    private final long dataLength;

    public QCTemplateDataText(String data){
        this(data, StandardCharsets.UTF_8);
    }
    public QCTemplateDataText(String data, Charset charset){
        char[] localData;
        long localDataLength;
        try {
            byte[] byteData = data.getBytes(charset);
            CharBuffer cb = charset.decode(ByteBuffer.wrap(byteData));
            localData = cb.array();
            localDataLength = localData.length;
        }
        catch (Exception ex){
            localData = null;
            localDataLength = 0;
            log.error(ex);
        }
        this.data = localData;
        this.dataLength = localDataLength;
    }


    public long write(Writer writer, Map<String, QCTemplateParam> params) throws IOException {
        writer.write(data);
        return dataLength;
    }
    public long write(Writer writer, Map<String, QCTemplateParam> params, Locale locale) throws IOException {
        writer.write(data);
        return dataLength;
    }
}
