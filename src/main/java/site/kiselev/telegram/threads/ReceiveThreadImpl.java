package site.kiselev.telegram.threads;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.kiselev.telegram.UserSession;
import site.kiselev.telegram.UserSessionFactory;

/**
 * ReceiveThread
 */
public class ReceiveThreadImpl implements ReceiveThread {
    private final Logger logger = LoggerFactory.getLogger(ReceiveThreadImpl.class);

    private final TelegramBot bot;
    private final UserSessionFactory userSessionFactory;

    private boolean isExit = false;

    public ReceiveThreadImpl(TelegramBot bot, UserSessionFactory userSessionFactory) {
        this.bot = bot;
        this.userSessionFactory = userSessionFactory;
    }

    @Override
    public void run() {
        int lastUpdateId = 0;
        while (!isExit) {
            GetUpdatesResponse updatesResponse = bot.execute(
                    new GetUpdates().offset(lastUpdateId));
            for (Update update : updatesResponse.updates()) {
                final UserSession session = userSessionFactory.getUserSession(update.message().from().id());
                session.newMessage(update);
            }
        }
    }

    @Override
    public void exit() {
        isExit = true;
    }
}
