package com.fareyeconnect.tool.task;


import io.quarkus.runtime.Startup;
import lombok.Data;
import org.graalvm.polyglot.Context;

@Data
@Startup
public class SystemTask extends Task {

    private String task;

    @Override
    public void execute(Context context) {
        context.eval("js", task);
    }
}
