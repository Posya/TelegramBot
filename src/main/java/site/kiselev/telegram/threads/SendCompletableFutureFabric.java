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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * SendCompletableFutureFabric class
 */
@Component
public class SendCompletableFutureFabric {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private TelegramBot bot;

    private BlockingQueue<SendMessage> sendQueue;

    private Supplier<Boolean> getSupplier = () -> {
        try {
            //noinspection InfiniteLoopStatement
            while(true) {
                final SendMessage message = sendQueue.take();
                logger.trace("SendMessage: {}", message);
                SendResponse sendResponse = bot.execute(message);
                logger.trace("SendResponse: {}", sendResponse);
                if (sendResponse.message() == null) {
                    logger.error("Can't send message: {}", message);
                }
            }
        } catch (InterruptedException e) {
            logger.info("Interrupted: {}", e);
            logger.debug(Arrays.toString(e.getStackTrace()));
        } catch (Throwable e) {
            logger.error("Error: {}", e);
            logger.debug(Arrays.toString(e.getStackTrace()));
            throw new Error(e);
        }
        return true;
    };

    @Autowired
    public SendCompletableFutureFabric(TelegramBot bot, BlockingQueue<SendMessage> sendQueue) {
        logger.trace("Creating SendCallableImpl");
        this.sendQueue = sendQueue;
        this.bot = bot;
    }

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

    public CompletableFuture<Boolean> getFuture() {
        return CompletableFuture.supplyAsync(getSupplier);
    }

    public CompletableFuture<Boolean> getFuture(Executor executor) {
        return CompletableFuture.supplyAsync(getSupplier, executor);
    }
}
