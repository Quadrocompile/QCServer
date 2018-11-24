package com.quadrocompile.qcserver.htmltemplates;

import com.quadrocompile.qcserver.htmltemplates.staticdata.QCTemplateDataTextLocalized;
import com.quadrocompile.qcserver.htmltemplates.staticdata.QCTemplateData;
import com.quadrocompile.qcserver.htmltemplates.staticdata.QCTemplateDataParam;
import com.quadrocompile.qcserver.htmltemplates.staticdata.QCTemplateDataText;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QCTemplateEngine {
    private static final Logger log = Logger.getLogger(QCTemplateEngine.class);

    private static final Pattern PATTERN_FIND_INSERTS = Pattern.compile( "\\Q[@\\E(include|param|locstring)\\Q:\\E(.+?)\\Q]\\E" );
    private static final Pattern PATTERN_EXTRACT_VARARGS = Pattern.compile( "\\Q(\\E([^(]*?)\\Q)\\E$" );
    private static final Pattern PATTERN_EXTRACT_VARARG = Pattern.compile( "[^(),]{1,999}" );

    private static QCLocalizedStringFactory localizedStringFactory = null;
    private static Locale defaultLocale = Locale.ENGLISH;

    private static String importResourceFile(String fileName, ClassLoader classLoader){
        try{
            InputStream is = classLoader.getResourceAsStream(fileName);
            if(is != null){
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                br.close();
                return sb.toString();
            }
            else{
                throw new FileNotFoundException("InputStream for " + fileName + " is null!");
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
            else if(insertType.equals("locstring")){
                Matcher varArgMatcher = PATTERN_EXTRACT_VARARGS.matcher(insertValue);
                if(varArgMatcher.find()){
                    String identifier = insertValue.substring(0, varArgMatcher.start());
                    Matcher argMatcher = PATTERN_EXTRACT_VARARG.matcher(varArgMatcher.group(0));
                    List<String> vargList = new ArrayList<>();
                    while(argMatcher.find()){
                        vargList.add(argMatcher.group(0));
                    }
                    templateData.add(new QCTemplateDataTextLocalized(identifier, vargList));
                }
                else{
                    templateData.add(new QCTemplateDataTextLocalized(insertValue));
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

    public static void setLocalizedStringFactory(QCLocalizedStringFactory factory){
        localizedStringFactory = factory;
    }

    public static void setDefaultLocale(Locale locale){
        defaultLocale = locale;
    }

    public static String getLocalizedStringDefaultLocale(String identifier){
        return getLocalizedString(identifier, defaultLocale);
    }

    public static String getLocalizedStringDefaultLocale(String identifier, List<String> args){
        return getLocalizedString(identifier, defaultLocale, args);
    }

    public static String getLocalizedString(String identifier, Locale locale){
        if(localizedStringFactory != null){
            return localizedStringFactory.getLocalizedString(identifier, locale);
        }
        else{
            return "${404:" + identifier + "(" + locale.toString() + ")}";
        }
    }

    public static String getLocalizedString(String identifier, Locale locale, List<String> args){
        if(localizedStringFactory != null){
            return localizedStringFactory.getLocalizedString(identifier, locale, args);
        }
        else{
            return "${404:" + identifier + "(" + locale.toString() + ")}";
        }
    }
}
