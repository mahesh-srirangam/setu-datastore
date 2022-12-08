package com.fareyeconnect.tool.task;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.graalvm.polyglot.Context;

@EqualsAndHashCode(callSuper = true)
@Data
public class SystemTask extends BaseNode implements Executor {

    private String script;

    @Override
    public void execute(Context context) {

    }
}
