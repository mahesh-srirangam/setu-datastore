/**
 * ****************************************************************************
 * <p>
 * Copyright (c) 2022, FarEye and/or its affiliates. All rights
 * reserved.
 * ___________________________________________________________________________________
 * <p>
 * <p>
 * NOTICE: All information contained herein is, and remains the property of
 * FarEye and its suppliers,if any. The intellectual and technical concepts
 * contained herein are proprietary to FarEye. and its suppliers and
 * may be covered by us and Foreign Patents, patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from FarEye.
 */
package com.fareyeconnect.tool.task;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.graalvm.polyglot.Context;

/**
 * @author Hemanth Reddy
 * @since 30/12/22
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Start extends Task {

    @Override
    public void execute(Context context) {
        System.out.println("Hello");
    }
}
