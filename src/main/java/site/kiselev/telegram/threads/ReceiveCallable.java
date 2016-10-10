package site.kiselev.telegram.threads;

import java.util.concurrent.Callable;

/**
 * ReceiveCallable interface
 */
public interface ReceiveCallable extends Callable<Boolean> {
    @Override
    Boolean call();
    void exit();
}
