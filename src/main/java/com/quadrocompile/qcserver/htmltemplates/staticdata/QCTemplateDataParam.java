package com.quadrocompile.qcserver.htmltemplates.staticdata;

import com.quadrocompile.qcserver.htmltemplates.paramdata.QCTemplateParam;
import com.quadrocompile.qcserver.htmltemplates.paramdata.QCTemplateStringParam;

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

        if(param == null || param.isNull()){
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

        if(param == null || param.isNull()){
            String data = "${404:" + paramName + "}";
            writer.write(data);
            return data.getBytes(StandardCharsets.UTF_8).length;
        }
        else{
            return param.write(writer, locale);
        }
    }
}
