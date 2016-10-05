package site.kiselev.telegram;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * UserSession
 */
public class UserSessionImpl implements UserSession, Runnable {

    private final Logger logger = LoggerFactory.getLogger(UserSessionImpl.class);
    private final Integer userID;
    private final Consumer<SendMessage> sendFunction;

    private BlockingQueue<Update> receiveQueue;
    private ExecutorService userSessionExecutor;
    private boolean isExit = false;

    public UserSessionImpl(Integer userID, Consumer<SendMessage> sendFunction) throws Exception {
        this.userID = userID;
        this.sendFunction = sendFunction;
        receiveQueue = new LinkedBlockingQueue<>();
        userSessionExecutor = Executors.newSingleThreadExecutor();
        logger.debug("Starting userSessionExecutor");
        userSessionExecutor.submit(this);
    }

    @Override
    public void newMessage(Update update) {
        if (userSessionExecutor.isTerminated()) {
            logger.error("UserSessionExecutor was stopped. Restarting...");
            userSessionExecutor.submit(this);
        }
        try {
            receiveQueue.put(update);
        } catch (InterruptedException e) {
            logger.error("Can't put update to queue. Update: {}", update);
            logger.debug(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void stop() {
        isExit = true;
    }

    @Override
    public void run() {
        while (!isExit) {
            Update update = read();
            SendMessage message = new SendMessage(userID, "*You wrote:* " + update.message().text())
                    .parseMode(ParseMode.Markdown);
            write(message);
        }
    }

    private Update read() {
        try {
            return receiveQueue.take();
        } catch (InterruptedException e) {
            logger.error("InterruptedException. Terminating...");
            logger.debug(Arrays.toString(e.getStackTrace()));
            stop();
            userSessionExecutor.shutdownNow();
            throw new RuntimeException(e);
        }
    }

    private void write(SendMessage message) {
        sendFunction.accept(message);
    }
}
