package com.quadrocompile.qcserver.htmltemplates;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public interface QCTemplateData {

    long write(Writer writer, Map<String, QCTemplateParam> params) throws IOException;

}
