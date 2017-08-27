package com.quadrocompile.qcserver.htmltemplates;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class QCTemplateDataParam implements QCTemplateData {
    private final String paramName;
    public QCTemplateDataParam(String paramName){
        this.paramName = paramName.toUpperCase();
    }
    public long write(OutputStreamWriter outputStream, Map<String, QCTemplateParam> params) throws IOException {
        QCTemplateParam param = params.get(paramName);
        if(param == null){
            String data = "${404:" + paramName + "}";
            outputStream.write(data);
            return data.getBytes(StandardCharsets.UTF_8).length;
        }
        else{
            return param.write(outputStream);
        }
    }
}