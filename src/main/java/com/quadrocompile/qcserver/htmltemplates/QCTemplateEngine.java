package com.quadrocompile.qcserver.htmltemplates;

import com.quadrocompile.qcserver.QCServer;
import com.quadrocompile.qcserver.htmltemplates.staticdata.QCTemplateDataTextLocalized;
import com.quadrocompile.qcserver.htmltemplates.staticdata.QCTemplateData;
import com.quadrocompile.qcserver.htmltemplates.staticdata.QCTemplateDataParam;
import com.quadrocompile.qcserver.htmltemplates.staticdata.QCTemplateDataText;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.*;
import java.util.*;
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

    private static TemplateWatchdog watchdog = null;
    private static class TemplateWatchdog implements Runnable{
        private Set<String> directorySet = new HashSet<>();
        private WatchService watchService;
        private Thread t;
        public TemplateWatchdog(){
            try {
                watchService = FileSystems.getDefault().newWatchService();
                t = new Thread(this);
                t.start();
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
        private void addDirectory(File dir) throws Exception{
            if(!directorySet.contains(dir.getAbsolutePath())) {
                log.debug("Add directory to watchdog: " + dir.getAbsolutePath());
                dir.toPath().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                directorySet.add(dir.getAbsolutePath());
            }
        }
        public void run(){
            try {
                WatchKey key;
                while ((key = watchService.take()) != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        //process
                    }
                    key.reset();
                    for(String fileName : reloadableTemplateMap.keySet()){
                        log.debug("Reload templates from file");
                        updateTemplateFromFile(fileName);
                    }
                }
            }
            catch (Exception ignored){}
        }
    }

    public static void enableTemplateReloading(){
        if(watchdog==null) watchdog= new TemplateWatchdog();
    }

    private static String importResourceFile(String fileName, ClassLoader classLoader){
        try{
            //InputStream is = classLoader.getResourceAsStream(fileName);
            URL resourceURL = classLoader.getResource(fileName);
            URLConnection resourceConnection = resourceURL.openConnection();
            resourceConnection.setUseCaches(false);
            InputStream is = resourceConnection.getInputStream();
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
    private static String importResourceFile(String fileName){
        try{
            File f = new File(fileName);
            if(watchdog!=null){
                watchdog.addDirectory(f.getParentFile());
            }
            InputStream is = new FileInputStream(f);
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
    private static Map<String, QCHTMLTemplate> reloadableTemplateMap = new ConcurrentHashMap<>();

    public static QCHTMLTemplate getTemplate(String templateName){
        QCHTMLTemplate template = templateMap.get(templateName);
        if(template == null && watchdog != null){
            template = reloadableTemplateMap.get(templateName);
        }
        return template;
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
    public static boolean createTemplateFromFile(String fileName){
        List<QCTemplateData> templateData = new ArrayList<>();

        try {
            if (createTemplateFromFile(fileName, 0, templateData)) {
                QCHTMLTemplate template = new QCHTMLTemplate(templateData);
                if(watchdog==null){
                    return (templateMap.putIfAbsent(fileName, template) == null);
                }
                else{
                    return (reloadableTemplateMap.putIfAbsent(fileName, template) == null);
                }
            } else {
                return false;
            }
        }
        catch (Exception ex){
            log.error("Failed to create template from file " + fileName, ex);
            return false;
        }
    }

    private static boolean updateTemplateFromFile(String fileName){
        List<QCTemplateData> templateData = new ArrayList<>();

        try {
            if (createTemplateFromFile(fileName, 0, templateData)) {
                QCHTMLTemplate template = new QCHTMLTemplate(templateData);
                reloadableTemplateMap.put(fileName, template);
                if(watchdog==null){
                    templateMap.putIfAbsent(fileName, template);
                }
                else{
                    reloadableTemplateMap.putIfAbsent(fileName, template);
                }
                return true;
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
    private static boolean createTemplateFromFile(String fileName, int recursionDepth, List<QCTemplateData> templateData) throws Exception{
        ++recursionDepth;

        if(recursionDepth > 50){
            throw new Exception("Recursion depth 50. Abort template import, please check for import loops.");
        }

        String fileContent = importResourceFile(fileName);
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
                if(!createTemplateFromFile(insertValue, recursionDepth+1, templateData)){
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
