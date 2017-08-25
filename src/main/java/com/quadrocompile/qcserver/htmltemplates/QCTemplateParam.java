package com.quadrocompile.qcserver.htmltemplates;

import java.io.IOException;
import java.io.OutputStreamWriter;

public interface QCTemplateParam {

    long write(OutputStreamWriter writer) throws IOException;

}
