package com.quadrocompile.qcserver.htmltemplates;

import com.quadrocompile.qcserver.htmltemplates.paramdata.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class QCParamMap implements Map<String, QCTemplateParam> {

    private final Map<String, QCTemplateParam> params;


    public QCParamMap(){
        this.params = new HashMap<>();
    }
    public QCParamMap(Map<String, QCTemplateParam> src){
        this.params = new HashMap<>(src);
    }



    public QCTemplateParam putEmpty(String key){
        return this.params.put(key, QCTemplateEmptyParam.get());
    }
    public QCTemplateParam putObject(String key, Object value){
        return this.params.put(key, new QCTemplateStringParam(String.valueOf(value)));
    }
    public QCTemplateParam putString(String key, String value){
        return this.params.put(key, new QCTemplateStringParam(value));
    }
    public QCTemplateParam putStringSafe(String key, String value){
        return this.params.put(key, new QCTemplateSafeStringParam(value));
    }
    public QCTemplateParam putInt(String key, int value){
        return this.params.put(key, new QCTemplateIntegerParam(value));
    }
    public QCTemplateParam putJSONAndSerialize(String key, JSONObject value){
        return this.params.put(key, new QCTemplateStringParam(value.toString()));
    }
    public QCTemplateParam putJSONAndSerialize(String key, JSONArray value){
        return this.params.put(key, new QCTemplateStringParam(value.toString()));
    }



    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return params.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return params.containsValue(value);
    }

    @Override
    public QCTemplateParam get(Object key) {
        return params.get(key);
    }

    @Override
    public QCTemplateParam getOrDefault(Object key, QCTemplateParam defaultValue) {
        return params.getOrDefault(key, defaultValue);
    }

    @Override
    public QCTemplateParam put(String key, QCTemplateParam value) {
        return params.put(key, value);
    }

    @Override
    public QCTemplateParam remove(Object key) {
        return params.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends QCTemplateParam> m) {
        params.putAll(m);
    }

    @Override
    public void clear() {
        params.clear();
    }

    @Override
    public Set<String> keySet() {
        return params.keySet();
    }

    @Override
    public Collection<QCTemplateParam> values() {
        return params.values();
    }

    @Override
    public Set<Entry<String, QCTemplateParam>> entrySet() {
        return params.entrySet();
    }

}
