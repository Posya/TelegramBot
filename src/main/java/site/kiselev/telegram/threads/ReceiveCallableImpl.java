package site.kiselev.telegram.threads;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.kiselev.telegram.UserSession;
import site.kiselev.telegram.UserSessionFactory;

import java.util.Arrays;

/**
 * ReceiveCallable
 */
@Component
public class ReceiveCallableImpl implements ReceiveCallable {
    private final Logger logger = LoggerFactory.getLogger(ReceiveCallableImpl.class);

    private final TelegramBot bot;
    private final UserSessionFactory userSessionFactory;

    private boolean isExit = false;

    @Autowired
    public ReceiveCallableImpl(TelegramBot bot, UserSessionFactory userSessionFactory) {
        logger.trace("Creating ReceiveCallableImpl");
        this.bot = bot;
        this.userSessionFactory = userSessionFactory;
    }

    @Override
    public Boolean call() {
        int lastUpdateId = 0;
        try {
            while (!isExit) {
                logger.trace("Receiving updates. LastUpdateId is {}", lastUpdateId);
                GetUpdates getUpdates = new GetUpdates().offset(lastUpdateId).limit(100).timeout(0);
                GetUpdatesResponse updatesResponse = bot.execute(getUpdates);
                for (Update update : updatesResponse.updates()) {
                    final UserSession session = userSessionFactory.getUserSession(update.message().from().id());
                    session.newMessage(update);
                    lastUpdateId = update.updateId() + 1;
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted: {}", e);
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
