package com.quadrocompile.qcserver.htmltemplates;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

public interface QCTemplateData {

    long write(OutputStreamWriter writer, Map<String, QCTemplateParam> params) throws IOException;

}
