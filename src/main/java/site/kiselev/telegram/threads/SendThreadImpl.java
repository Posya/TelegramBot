package site.kiselev.telegram.threads;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * SendThread class
 */
public class SendThreadImpl implements SendThread {

    private final Logger logger = LoggerFactory.getLogger(SendThreadImpl.class);
    private final TelegramBot bot;

    private BlockingQueue<SendMessage> sendQueue;
    private boolean isExit = false;

    public SendThreadImpl(TelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public Consumer<SendMessage> getSendFunction() {
        return (message) -> {
            try {
                sendQueue.put(message);
            } catch (InterruptedException e) {
                logger.error("Can't put message to queue. Message: {}", message);
                logger.debug(Arrays.toString(e.getStackTrace()));
            }
        };
    }

    @Override
    public void run() {
        try {
            while(!isExit) {
                final SendMessage message = sendQueue.take();
                SendResponse sendResponse = bot.execute(message);
                logger.trace("SendResponse: {}", sendResponse);
                if (sendResponse.message() == null) {
                    logger.error("Can't send message: {}", message);
                }
            }
        } catch (InterruptedException e) {
            logger.error("Can't get message from queue.");
            logger.debug(Arrays.toString(e.getStackTrace()));
        } catch (Throwable e) {
            logger.error("Exception: {}", e);
            logger.debug(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void exit() {
        isExit = true;
    }
}
