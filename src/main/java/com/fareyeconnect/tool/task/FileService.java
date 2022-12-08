package com.fareyeconnect.tool.task;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.graalvm.polyglot.Context;

@EqualsAndHashCode(callSuper = true)
@Data
public class FileService extends BaseNode implements Executor {

    @Override
    public void execute(Context context) {

    }
}
