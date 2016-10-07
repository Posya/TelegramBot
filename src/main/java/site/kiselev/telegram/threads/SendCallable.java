package site.kiselev.telegram.threads;

import com.pengrad.telegrambot.request.SendMessage;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * SendCallable interface
 */
public interface SendCallable extends Callable<Boolean> {
    Consumer<SendMessage> getSendFunction();

    @Override
    Boolean call();

    void exit();
}
