package com.quadrocompile.qcserver.htmltemplates;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class QCTemplateLocalizedStringParam  implements QCTemplateData {

    private static final Logger log = Logger.getLogger(QCTemplateLocalizedStringParam.class);

    private final String paramName;
    private final List<String> paramArgs;

    public QCTemplateLocalizedStringParam(String paramName){
        this.paramName = paramName;
        this.paramArgs = null;
    }

    public QCTemplateLocalizedStringParam(String paramName, List<String> params){
        this.paramName = paramName;
        this.paramArgs = new ArrayList<>(params.size());
        for(String p : params){
            this.paramArgs.add(p.toUpperCase());
        }
    }

    public long write(Writer writer, Map<String, QCTemplateParam> params) throws IOException {
        if(paramArgs==null) {
            String data = QCTemplateEngine.getLocalizedStringDefaultLocale(paramName);
            writer.write(data);
            return data.getBytes(StandardCharsets.UTF_8).length;
        }
        else{
            List<String> paramList = new ArrayList<>(paramArgs.size());
            for(String p : paramArgs){
                QCTemplateParam data = params.get(p);
                if(data instanceof QCTemplateStringParam){
                    paramList.add( ((QCTemplateStringParam)data).getString() );
                }
                else{
                    paramList.add("${404:" + paramName + "}");
                }
            }
            String data = QCTemplateEngine.getLocalizedStringDefaultLocale(paramName, paramList);
            writer.write(data);
            return data.getBytes(StandardCharsets.UTF_8).length;
        }
    }

    public long write(Writer writer, Map<String, QCTemplateParam> params, Locale locale) throws IOException {
        if(paramArgs==null) {
            String data = QCTemplateEngine.getLocalizedString(paramName, locale);
            writer.write(data);
            return data.getBytes(StandardCharsets.UTF_8).length;
        }
        else{
            List<String> paramList = new ArrayList<>(paramArgs.size());
            for(String p : paramArgs){
                QCTemplateParam data = params.get(p);
                if(data instanceof QCTemplateStringParam){
                    paramList.add( ((QCTemplateStringParam)data).getString() );
                }
                else{
                    paramList.add("${404:" + paramName + "}");
                }
            }
            String data = QCTemplateEngine.getLocalizedStringDefaultLocale(paramName, paramList);
            writer.write(data);
            return data.getBytes(StandardCharsets.UTF_8).length;
        }
    }

}
