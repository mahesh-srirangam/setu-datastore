/*
 *
 *  * *
 *  *  * ****************************************************************************
 *  *  *
 *  *  * Copyright (c) 2023, FarEye and/or its affiliates. All rights
 *  *  * reserved.
 *  *  * ___________________________________________________________________________________
 *  *  *
 *  *  *
 *  *  * NOTICE: All information contained herein is, and remains the property of
 *  *  * FaEye and its suppliers,if any. The intellectual and technical concepts
 *  *  * contained herein are proprietary to FarEye. and its suppliers and
 *  *  * may be covered by us and Foreign Patents, patents in process, and are
 *  *  * protected by trade secret or copyright law. Dissemination of this information
 *  *  * or reproduction of this material is strictly forbidden unless prior written
 *  *  * permission is obtained from FarEye
 *  *
 *
 */

package com.fareyeconnect;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class App {

    public static void main(String[] args) {
        Quarkus.run(args);
    }
}
