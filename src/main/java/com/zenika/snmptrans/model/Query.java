package com.zenika.snmptrans.model;

import java.util.Collection;

public class Query {

    private String obj;
    private String resultAlias;
    private Collection<String> attr;
    private String typeName;

    public String getObj() {
        return obj;
    }

    public void setObj(String obj) {
        this.obj = obj;
    }

    public String getResultAlias() {
        return resultAlias;
    }

    public void setResultAlias(String resultAlias) {
        this.resultAlias = resultAlias;
    }

    public Collection<String> getAttr() {
        return attr;
    }

    public void setAttr(Collection<String> attr) {
        this.attr = attr;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
