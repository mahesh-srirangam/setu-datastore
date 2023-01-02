package com.fareyeconnect.tool.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.xml.bind.JAXBException;

import javax.xml.stream.XMLStreamException;

public interface Parser {
    Object parse(String schema, String requestBody) throws JsonProcessingException, XMLStreamException, JAXBException;
}
