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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * ReceiveCompletableFutureFabric class
 */
@Component
public class ReceiveCompletableFutureFabric {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private TelegramBot bot;
    private UserSessionFactory userSessionFactory;

    private Supplier<Boolean> getSupplier = () -> {
        int lastUpdateId = 0;
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
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
            logger.info("Interrupted: {}", e);
            logger.debug(Arrays.toString(e.getStackTrace()));
        }
        return true;
    };

    @Autowired
    public ReceiveCompletableFutureFabric(TelegramBot bot, UserSessionFactory userSessionFactory) {
        logger.trace("Creating ReceiveCompletableFutureFabric");
        this.bot = bot;
        this.userSessionFactory = userSessionFactory;
    }

    public CompletableFuture<Boolean> getFuture() {
        return CompletableFuture.supplyAsync(getSupplier);
    }

    public CompletableFuture<Boolean> getFuture(Executor executor) {
        return CompletableFuture.supplyAsync(getSupplier, executor);
    }
}
