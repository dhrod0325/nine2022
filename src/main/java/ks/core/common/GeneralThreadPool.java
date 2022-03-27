package ks.core.common;

import java.util.concurrent.Executor;

public class GeneralThreadPool implements Executor {
    private Executor executor;

    public GeneralThreadPool(Executor executor) {
        this.executor = executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void execute(Runnable command) {
        executor.execute(command);
    }
}
