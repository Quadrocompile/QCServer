package com.quadrocompile.qcserver.htmltemplates;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QCTemplateEngine {
    private static final Logger log = Logger.getLogger(QCTemplateEngine.class);

    private static final Pattern PATTERN_FIND_PARAM = Pattern.compile(Pattern.quote("[@param:") + "(.+?)" + Pattern.quote("]")); // [@Param:UserName]
    private static final Pattern PATTERN_FIND_INCLUDE = Pattern.compile(Pattern.quote("[@include:") + "(.+?)" + Pattern.quote("]")); // [@include:UserName]

    private static final Pattern PATTERN_FIND_INSERTS = Pattern.compile( "\\Q[@\\E(include|param)\\Q:\\E(.+?)\\Q]\\E" );

    private static String importResourceFile(String fileName, ClassLoader classLoader){
        try{
            URL url = classLoader.getResource(fileName);
            if(url != null){
                File file = new File(url.getFile());
                if(file.exists()) {
                    StringBuilder sb = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                        sb.append("\n");
                    }
                    br.close();
                    return sb.toString();
                }
                else{
                    throw new FileNotFoundException("File " + fileName + " does not exist!");
                }
            }
            else{
                throw new FileNotFoundException("URL for " + fileName + " is null!");
            }
        }
        catch (Exception ex){
            log.error("Cannot import ressource file " + fileName + "!", ex);
        }

        return null;
    }

    private static Map<String, QCHTMLTemplate> templateMap = new ConcurrentHashMap<>();

    public static QCHTMLTemplate getTemplate(String templateName){
        return templateMap.get(templateName);
    }

    public static boolean createTemplateFromFile(String fileName, ClassLoader classLoader){

        List<QCTemplateData> templateData = new ArrayList<>();

        try {
            if (createTemplateFromFile(fileName, classLoader, 0, templateData)) {
                QCHTMLTemplate template = new QCHTMLTemplate(templateData);
                return (templateMap.putIfAbsent(fileName, template) == null);
            } else {
                return false;
            }
        }
        catch (Exception ex){
            log.error("Failed to create template from file " + fileName, ex);
            return false;
        }

    }

    private static boolean createTemplateFromFile(String fileName, ClassLoader classLoader, int recursionDepth, List<QCTemplateData> templateData) throws Exception{
        ++recursionDepth;

        if(recursionDepth > 50){
            throw new Exception("Recursion depth 50. Abort template import, please check for import loops.");
        }

        String fileContent = importResourceFile(fileName, classLoader);
        if(fileContent == null){
            return false;
        }

        Matcher matcher = PATTERN_FIND_INSERTS.matcher(fileContent);

        int lastEnd = 0;
        String substring;
        while (matcher.find()) {
            String insertType = matcher.group(1);
            String insertValue = matcher.group(2);

            // Bisherigen Text holen und in das Template schreiben, wenn der String nicht leer ist
            substring = fileContent.substring(lastEnd, matcher.start());
            if (!substring.isEmpty()) {
                templateData.add(new QCTemplateDataText(substring));
            }


            if(insertType.equals("param")){
                // Paramater eintragen
                templateData.add(new QCTemplateDataParam(insertValue));
            }
            else if(insertType.equals("include")){
                if(!createTemplateFromFile(insertValue, classLoader, recursionDepth+1, templateData)){
                    return false;
                }
            }


            // Pointer auf neuen Substring aufrücken
            lastEnd = matcher.end();
        }

        // Verbleibenden Text anhängen, falls es ihn gibt
        substring = fileContent.substring(lastEnd);
        if (!substring.isEmpty()) {
            templateData.add(new QCTemplateDataText(substring));
        }

        return true;
    }

}
