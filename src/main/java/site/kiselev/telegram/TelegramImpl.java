package site.kiselev.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.kiselev.telegram.threads.ReceiveCompletableFutureFabric;
import site.kiselev.telegram.threads.SendCompletableFutureFabric;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Telegram class
 * Запускает все необходимые подпроцессы, проверяет их и перестартовывает при необходимости.
 */
@Component("Telegram")
class TelegramImpl implements Telegram {

    private final Logger logger = LoggerFactory.getLogger(TelegramImpl.class);

    private SendCompletableFutureFabric sendCompletableFutureFabric;
    private ReceiveCompletableFutureFabric receiveCompletableFutureFabric;

    private boolean isExit = false;

    @Autowired
    public TelegramImpl(
            SendCompletableFutureFabric sendCompletableFutureFabric,
            ReceiveCompletableFutureFabric receiveCompletableFutureFabric) {
        this.sendCompletableFutureFabric = sendCompletableFutureFabric;
        this.receiveCompletableFutureFabric = receiveCompletableFutureFabric;
    }

    @Override
    public void runTelegramBot() {
//        logger.trace("Creating ExecutorServices");
//        ExecutorService executor = Executors.newFixedThreadPool(2);
        logger.trace("Starting futures");
        CompletableFuture<Boolean> sendFuture = sendCompletableFutureFabric.getFuture();
        CompletableFuture<Boolean> receiveFuture = receiveCompletableFutureFabric.getFuture();

        logger.trace("Entering loop");
        try {
            while (!isExit) {
                if (sendFuture.isDone()) {
                    logger.debug("Restarting sendFuture");
                    sendFuture = sendCompletableFutureFabric.getFuture();
                }
                if (receiveFuture.isDone()) {
                    logger.debug("Restarting receiveFuture");
                    receiveFuture = receiveCompletableFutureFabric.getFuture();
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            logger.info("Interrupted: ", e);
            logger.debug(Arrays.toString(e.getStackTrace()));
        }
        logger.trace("Exiting loop");
    }

    public void exit() {
        logger.trace("Exit is called");
        isExit = true;
    }
}
