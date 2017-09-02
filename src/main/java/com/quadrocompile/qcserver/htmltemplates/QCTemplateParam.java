package com.quadrocompile.qcserver.htmltemplates;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public interface QCTemplateParam {

    long write(Writer writer) throws IOException;

}
