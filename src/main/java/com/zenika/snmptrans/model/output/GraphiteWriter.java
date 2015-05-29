package com.zenika.snmptrans.model.output;

import com.zenika.snmptrans.model.OutputWriter;
import com.zenika.snmptrans.model.Result;
import com.zenika.snmptrans.model.Server;
import com.zenika.snmptrans.model.ValidationException;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class GraphiteWriter implements OutputWriter {
    @Override
    public void setSettings(Map<String, Object> settings) throws ValidationException {

    }

    @Override
    public void doWrite(Server server, Collection<Result> results) throws IOException {

    }
}
