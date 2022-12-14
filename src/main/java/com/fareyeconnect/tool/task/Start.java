package com.fareyeconnect.tool.task;

import io.quarkus.runtime.Startup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.graalvm.polyglot.Context;

@EqualsAndHashCode(callSuper = true)
@Data
public class Start extends Task {

    @Override
    public void execute(Context context) {
        System.out.println("Hello");
    }
}
