package com.quadrocompile.qcserver.htmltemplates.paramdata;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class QCTemplateStringParam implements QCTemplateParam {

    private static final Logger log = Logger.getLogger(QCTemplateStringParam.class);

    /*
    private final char[] data;
    private final long dataLength;

    public QCTemplateStringParam(String data){
        this(data, StandardCharsets.UTF_8);
    }
    public QCTemplateStringParam(String data, Charset charset){
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

    public boolean isNull() { return data == null; }

    public long write(Writer writer) throws IOException {
        writer.write(data);
        return dataLength;
    }
    public long write(Writer writer, Locale locale) throws IOException {
        writer.write(data);
        return dataLength;
    }

    public String getString(){
        return new String(data);
    }
    */

    private final String data;
    private final long dataLength;

    public QCTemplateStringParam(String data){
        this(data, StandardCharsets.UTF_8);
    }
    public QCTemplateStringParam(String data, Charset charset){
        this.data = data;
        this.dataLength = data.getBytes(charset).length;
    }

    public boolean isNull() { return data == null; }

    public long write(Writer writer) throws IOException {
        writer.write(data);
        return dataLength;
    }
    public long write(Writer writer, Locale locale) throws IOException {
        writer.write(data);
        return dataLength;
    }

    public String getString(){
        return data;
    }
}
