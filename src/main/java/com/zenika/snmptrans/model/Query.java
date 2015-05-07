package com.zenika.snmptrans.model;

import java.util.Collection;

public class Query {
    private String templateName;
    private Collection<String> oids;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Collection<String> getOids() {
        return oids;
    }

    public void setOids(Collection<String> oids) {
        this.oids = oids;
    }
}
