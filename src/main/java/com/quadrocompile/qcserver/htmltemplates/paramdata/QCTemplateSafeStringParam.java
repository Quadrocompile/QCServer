package com.quadrocompile.qcserver.htmltemplates.paramdata;

import org.owasp.encoder.Encode;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class QCTemplateSafeStringParam implements QCTemplateParam{

    private final String data;
    private final long dataLength;

    public QCTemplateSafeStringParam(String data){
        this(data, StandardCharsets.UTF_8);
    }
    public QCTemplateSafeStringParam(String data, Charset charset){
        data = Encode.forHtml(data);
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
