package com.fareyeconnect.tool.helpers;

import lombok.NoArgsConstructor;
import org.graalvm.polyglot.HostAccess;

import java.util.Date;

@NoArgsConstructor
public class UtilHelper {

    @HostAccess.Export
    public String getDate() {
        return new Date().toString();
    }


}
