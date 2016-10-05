package site.kiselev.telegram;

import com.pengrad.telegrambot.TelegramBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.kiselev.telegram.threads.ReceiveThread;
import site.kiselev.telegram.threads.SendThread;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Telegram class
 */
public class TelegramImpl implements Telegram {

    private final Logger logger = LoggerFactory.getLogger(TelegramImpl.class);

    private TelegramBot bot;
    private SendThread sendThread;
    private UserSessionFactory userSessionFactory = new UserSessionFactoryImpl(sendThread.getSendFunction());
    private ReceiveThread receiveThread;
    private boolean isExit = false;


    @Override
    public void run() {
        ExecutorService sendExecutor = Executors.newSingleThreadExecutor();
        ExecutorService receiveExecutor = Executors.newSingleThreadExecutor();
        try {
            while (!isExit) {
                if (sendExecutor.isTerminated()) {
                    logger.debug("Starting sendExecutor");
                    sendExecutor.submit(sendThread);
                }
                if (receiveExecutor.isTerminated()) {
                    logger.debug("Starting receiveExecutor");
                    receiveExecutor.submit(sendThread);
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted: ", e);
            logger.debug(Arrays.toString(e.getStackTrace()));
        }
    }

    public void exit() {
        isExit = true;
    }
}
