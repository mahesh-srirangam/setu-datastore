package com.fareyeconnect.tool.task;


import io.quarkus.runtime.Startup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.graalvm.polyglot.Context;

@EqualsAndHashCode(callSuper = true)
@Data
public class SystemTask extends Task {

    private String task;

    @Override
    public void execute(Context context) {
        context.eval("js", task);
    }
}
