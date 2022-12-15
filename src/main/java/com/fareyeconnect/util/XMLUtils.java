package com.fareyeconnect.util;

import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;
import de.odysseus.staxon.xml.util.PrettyXMLEventWriter;
import io.quarkus.logging.Log;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class XMLUtils {

    public static String jsonToXml(String json) throws XMLStreamException {
        XMLEventReader reader = null;
        XMLEventWriter writer = null;
        try (InputStream inputStream = new ByteArrayInputStream(json.getBytes());
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            JsonXMLConfig config = new JsonXMLConfigBuilder().multiplePI(false).build();
            reader = new JsonXMLInputFactory(config).createXMLEventReader(inputStream);
            writer = XMLOutputFactory.newInstance().createXMLEventWriter(output);
            writer = new PrettyXMLEventWriter(writer);
            writer.add(reader);
            return output.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.error("Error occurred while converting JSON to XML {}" + e.getMessage());
            throw new RuntimeException("Exception occurred while writing JSON to XML " + e.getMessage());
        } finally {
            if (reader != null)
                reader.close();
            if (writer != null)
                writer.close();
        }
    }
}
