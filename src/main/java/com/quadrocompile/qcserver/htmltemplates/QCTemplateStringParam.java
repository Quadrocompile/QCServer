package com.quadrocompile.qcserver.htmltemplates;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class QCTemplateStringParam implements QCTemplateParam {

    private final String data;
    public QCTemplateStringParam(String data){
        this.data = data;
    }

    public boolean isNull() { return data == null; }

    @Override
    public long write(Writer writer) throws IOException{
        writer.write(data);
        return data.getBytes(StandardCharsets.UTF_8).length;
    }
}
