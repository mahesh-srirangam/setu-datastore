package com.fareyeconnect.tool.parser;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TextParser implements Parser{
    @Override
    public Object parse(String schema, String requestBody) {
        return requestBody;
    }
}
