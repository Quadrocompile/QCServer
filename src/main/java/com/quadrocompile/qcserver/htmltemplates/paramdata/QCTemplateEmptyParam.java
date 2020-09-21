package com.quadrocompile.qcserver.htmltemplates.paramdata;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

public class QCTemplateEmptyParam implements QCTemplateParam {

    private static final QCTemplateEmptyParam instance = new QCTemplateEmptyParam();

    public static QCTemplateEmptyParam get(){ return instance; }

    @Override
    public long write(Writer writer) throws IOException {
        return 0;
    }

    @Override
    public long write(Writer writer, Locale locale) throws IOException {
        return 0;
    }

    @Override
    public boolean isNull() {
        return false;
    }
}
