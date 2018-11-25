package com.quadrocompile.qcserver.htmltemplates.paramdata;

import java.util.*;

public class QCSingleParamMap<K extends String, V extends QCTemplateParam> implements Map<String,QCTemplateParam> {

    private String key;
    private QCTemplateParam value;

    public QCSingleParamMap (String key, QCTemplateParam value){
        this.key = key;
        this.value = value;
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
        if(key instanceof String){
            return key.equals(this.key);
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public QCTemplateParam get(Object key) {
        if(key instanceof String){
            if(key.equals(this.key)){
                return value;
            }
        }
        return null;
    }

    @Override
    public QCTemplateParam getOrDefault(Object key, QCTemplateParam defaultValue) {
        if(key instanceof String){
            if(key.equals(this.key)){
                return value;
            }
        }
        return defaultValue;
    }

    @Override
    public QCTemplateParam put(String key, QCTemplateParam value) {
        QCTemplateParam oldValue = this.value;
        this.key = key;
        this.value = value;
        return oldValue;
    }

    @Override
    public QCTemplateParam remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends QCTemplateParam> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<String> keySet() {
        Set<String> singletonSet = new HashSet<>(1, 1.0f);
        singletonSet.add(key);
        return singletonSet;
    }

    @Override
    public Collection<QCTemplateParam> values() {
        return Collections.singletonList(value);
    }

    @Override
    public Set<Entry<String, QCTemplateParam>> entrySet() {
        Set<Entry<String, QCTemplateParam>> singletonSet = new HashSet<>(1, 1.0f);
        singletonSet.add(new AbstractMap.SimpleEntry<>(key, value));
        return singletonSet;
    }
}

