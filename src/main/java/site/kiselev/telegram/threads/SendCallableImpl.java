package site.kiselev.telegram.threads;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

/**
 * SendCallable class
 * Обеспечивает отправку сообщений из исходящей очереди.
 */
@Component
public class SendCallableImpl implements SendCallable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final TelegramBot bot;

    @Autowired
    private BlockingQueue<SendMessage> sendQueue;
    private boolean isExit = false;

    @Autowired
    public SendCallableImpl(TelegramBot bot) {
        logger.trace("Creating SendCallableImpl");
        this.bot = bot;
    }

    @Override
    public Consumer<SendMessage> getSendFunction() {
        logger.trace("getSendFunction was called");
        return (message) -> {
            try {
                sendQueue.put(message);
                logger.trace("Message was added to Send Queue: {}", message);
            } catch (InterruptedException e) {
                logger.error("Can't put message to queue. Message: {}", message);
                logger.debug(Arrays.toString(e.getStackTrace()));
            }
        };
    }

    @Override
    public Boolean call() {
        try {
            while(!isExit) {
                final SendMessage message = sendQueue.take();
                logger.trace("SendMessage: {}", message);
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
        return true;
    }

    @Override
    public void exit() {
        logger.trace("Exit is called");
        isExit = true;
    }
}
