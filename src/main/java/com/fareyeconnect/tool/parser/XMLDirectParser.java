package com.fareyeconnect.tool.parser;

import com.fareyeconnect.util.XMLUtils;
import io.quarkus.runtime.Startup;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.stream.XMLStreamException;

@ApplicationScoped
@Startup
public class XMLDirectParser implements Parser {
    @Override
    public Object parse(String schema, String requestBody) throws XMLStreamException {
        return XMLUtils.jsonToXml(requestBody);
    }
}
