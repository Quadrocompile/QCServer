package com.quadrocompile.qcserver.htmltemplates.paramdata;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class QCTemplateIntegerParam implements QCTemplateParam {

    private static final Logger log = Logger.getLogger(QCTemplateStringParam.class);

    private final String data;
    private final long dataLength;

    public QCTemplateIntegerParam(Integer data){
        this(data, StandardCharsets.UTF_8);
    }
    public QCTemplateIntegerParam(Integer data, Charset charset){
        this.data = String.valueOf(data);
        this.dataLength = this.data.getBytes(charset).length;
    }

    public QCTemplateIntegerParam(Long data){
        this(data, StandardCharsets.UTF_8);
    }
    public QCTemplateIntegerParam(Long data, Charset charset){
        this.data = String.valueOf(data);
        this.dataLength = this.data.getBytes(charset).length;
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
