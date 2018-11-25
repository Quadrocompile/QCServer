package com.quadrocompile.qcserver.htmltemplates.paramdata;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class QCEmptyParamMap<K extends String, V extends QCTemplateParam> implements Map<String,QCTemplateParam> {

    public static final QCEmptyParamMap EMPTY_PARAM_MAP = new QCEmptyParamMap<>();

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public QCTemplateParam get(Object key) {
        return null;
    }

    @Override
    public QCTemplateParam getOrDefault(Object key, QCTemplateParam defaultValue) {
        return defaultValue;
    }

    @Override
    public QCTemplateParam put(String key, QCTemplateParam value) {
        return null;
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
        return null;
    }

    @Override
    public Collection<QCTemplateParam> values() {
        return null;
    }

    @Override
    public Set<Entry<String, QCTemplateParam>> entrySet() {
        return null;
    }
}
