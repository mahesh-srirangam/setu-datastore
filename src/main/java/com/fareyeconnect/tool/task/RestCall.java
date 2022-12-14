package com.fareyeconnect.tool.task;

import io.quarkus.runtime.Startup;
import lombok.Data;
import org.graalvm.polyglot.Context;

@Data
@Startup
public class RestCall extends Task {

    @Override
    public void execute(Context context) {

    }
}
