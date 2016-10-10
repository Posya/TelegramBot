package site.kiselev.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.kiselev.telegram.threads.ReceiveCallable;
import site.kiselev.telegram.threads.SendCallable;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Telegram class
 * Запускает все необходимые подпроцессы, проверяет их и перестартовывает при необходимости.
 */
@Component("Telegram")
class TelegramImpl implements Telegram {

    private final Logger logger = LoggerFactory.getLogger(TelegramImpl.class);

    @Autowired
    private SendCallable sendCallable;
    @Autowired
    private ReceiveCallable receiveCallable;

    private boolean isExit = false;

    @Override
    public void runTelegramBot() {
        logger.trace("Creating ExecutorServices");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        logger.trace("Starting futures");
        Future<Boolean> sendFuture      = executor.submit(sendCallable);
        Future<Boolean> receiveFuture   = executor.submit(receiveCallable);
        logger.trace("Entering loop");
        try {
            while (!isExit) {
                if (sendFuture.isDone()) {
                    logger.debug("Restarting sendFuture");
                    sendFuture = executor.submit(sendCallable);
                }
                if (receiveFuture.isDone()) {
                    logger.debug("Restarting receiveFuture");
                    receiveFuture = executor.submit(receiveCallable);
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted: ", e);
            logger.debug(Arrays.toString(e.getStackTrace()));
        }
        logger.trace("Exiting loop");
    }

    public void exit() {
        logger.trace("Exit is called");
        isExit = true;
    }
}
