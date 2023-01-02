package com.fareyeconnect.tool.parser;

import com.fareyeconnect.tool.dto.XmlSchema;
import com.fareyeconnect.util.XMLUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class XMLParser implements Parser {

    @Inject
    ObjectMapper objectMapper;

    @Override
    public Object parse(String schema, String requestBody) throws XMLStreamException, JAXBException {
        List<XmlSchema> xmlSchemaList = objectMapper.readValue(schema, new TypeReference<List<XmlSchema>>() {
        })
        return XMLUtils.jsonToXml(requestBody, xmlSchemaList);
    }
}
