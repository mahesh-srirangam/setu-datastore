package com.fareyeconnect.util;

import com.fareyeconnect.tool.dto.XmlSchema;
import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;
import de.odysseus.staxon.xml.util.PrettyXMLEventWriter;
import io.quarkus.logging.Log;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;

import javax.persistence.EntityNotFoundException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

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


    public static String jsonToXml(String json, List<XmlSchema> schemaList) throws JAXBException, XMLStreamException {
        DynamicJAXBContext jaxbContext = getJAXBContext(schemaList);
        XmlSchema mainSchema = schemaList.stream().filter(XmlSchema::getIsMainSchema).findAny().orElse(null);
        if (mainSchema != null) {
            DynamicEntity entity = unmarshal(mainSchema, json, jaxbContext);
            return marshal(jaxbContext, entity, false, mainSchema);
        } else return "Root schema not detected";
    }

    private static DynamicJAXBContext getJAXBContext(List<XmlSchema> schemaList) throws JAXBException {
        XmlSchema mainSchema = schemaList.stream().filter(XmlSchema::getIsMainSchema).findAny().orElseThrow(EntityNotFoundException::new);
        StreamSource streamSource = new StreamSource(new ByteArrayInputStream(mainSchema.getSchema().getBytes(StandardCharsets.UTF_8)));
        XsdEntityResolver xsdEntityResolver = new XsdEntityResolver(schemaList);
        return DynamicJAXBContextFactory.createContextFromXSD(streamSource, xsdEntityResolver, null, null);
    }

    private static DynamicEntity unmarshal(XmlSchema mainSchema, String data, DynamicJAXBContext jaxbContext) throws JAXBException, XMLStreamException {
        JAXBUnmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Class<? extends DynamicEntity> type = jaxbContext.getDynamicType(mainSchema.getRootElement()).getJavaClass();
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
        // Since there is no root node in your JSON document you should set this flag
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
        Source dataSource = new StreamSource(new ByteArrayInputStream(data.trim().getBytes(StandardCharsets.UTF_8)));
        return ((JAXBElement<DynamicEntity>) jaxbUnmarshaller.unmarshal(dataSource, type)).getValue();
    }

    private static String marshal(DynamicJAXBContext jaxbContext, DynamicEntity entity, boolean toJson, XmlSchema mainSchema) throws JAXBException {
        JAXBMarshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        if (toJson) {
            marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        } else if (mainSchema.getHeader() != null && !"".equalsIgnoreCase(mainSchema.getHeader())) {
            marshaller.setProperty("com.sun.xml.bind.xmlHeaders", mainSchema.getHeader());
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        marshaller.marshal(entity, outputStream);
        return outputStream.toString(StandardCharsets.UTF_8);
    }

}
