package site.kiselev.telegram.threads;

import com.pengrad.telegrambot.request.SendMessage;

import java.util.function.Consumer;

/**
 * Created by posya on 10/5/16.
 */
public interface SendThread extends Runnable {
    Consumer<SendMessage> getSendFunction();

    @Override
    void run();

    void exit();
}
