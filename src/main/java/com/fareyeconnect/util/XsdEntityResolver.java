/*
 * *
 *  * ****************************************************************************
 *  *
 *  * Copyright (c) 2022, FarEye and/or its affiliates. All rights
 *  * reserved.
 *  * ___________________________________________________________________________________
 *  *
 *  *
 *  * NOTICE: All information contained herein is, and remains the property of
 *  * FaEye and its suppliers,if any. The intellectual and technical concepts
 *  * contained herein are proprietary to FarEye. and its suppliers and
 *  * may be covered by us and Foreign Patents, patents in process, and are
 *  * protected by trade secret or copyright law. Dissemination of this information
 *  * or reproduction of this material is strictly forbidden unless prior written
 *  * permission is obtained from FarEye
 *
 */

package com.fareyeconnect.util;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import javax.persistence.EntityNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.fareyeconnect.tool.dto.XmlSchema;

/**
 * @author Hemanth Reddy
 * @since 30/12/22
 */

public class XsdEntityResolver implements EntityResolver {

    private List<XmlSchema> schemaList;

    public XsdEntityResolver(List<XmlSchema> xmlSchemas) {
        schemaList = xmlSchemas;
    }

    public InputSource resolveEntity(String publicId, String systemId) {
        String filename = new File(systemId).getName();
        XmlSchema schema = null;
        for (XmlSchema _schema : schemaList) {
            String name = _schema.getName();
            if (name != null && name.equals(filename)) {
                schema = _schema;
            }
        }
        if (schema == null) throw new EntityNotFoundException();
        InputSource streamSource = new InputSource(new ByteArrayInputStream(schema.getSchema().getBytes(StandardCharsets.UTF_8)));
        streamSource.setSystemId("file:/tool");
        return streamSource;
    }
}
