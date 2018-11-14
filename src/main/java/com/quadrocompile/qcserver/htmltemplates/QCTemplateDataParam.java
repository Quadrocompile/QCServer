package com.quadrocompile.qcserver.htmltemplates;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

public class QCTemplateDataParam implements QCTemplateData {
    private final String paramName;

    public QCTemplateDataParam(String paramName){
        this.paramName = paramName.toUpperCase();
    }

    public long write(Writer writer, Map<String, QCTemplateParam> params) throws IOException {
        QCTemplateParam param = params.get(paramName);

        boolean isNull = false;
        if(param==null) isNull = true;
        if(param instanceof QCTemplateStringParam){
            if( ((QCTemplateStringParam)param).isNull()){
                isNull = true;
            }
        }

        if(isNull){
            String data = "${404:" + paramName + "}";
            writer.write(data);
            return data.getBytes(StandardCharsets.UTF_8).length;
        }
        else{
            return param.write(writer);
        }
    }

    public long write(Writer writer, Map<String, QCTemplateParam> params, Locale locale) throws IOException {
        QCTemplateParam param = params.get(paramName);

        boolean isNull = false;
        if(param==null) isNull = true;
        if(param instanceof QCTemplateStringParam){
            if( ((QCTemplateStringParam)param).isNull()){
                isNull = true;
            }
        }

        if(isNull){
            String data = "${404:" + paramName + "}";
            writer.write(data);
            return data.getBytes(StandardCharsets.UTF_8).length;
        }
        else{
            return param.write(writer, locale);
        }
    }
}
