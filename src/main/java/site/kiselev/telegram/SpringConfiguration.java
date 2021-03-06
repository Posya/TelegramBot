package site.kiselev.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import site.kiselev.telegram.threads.SendCompletableFutureFabric;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import static site.kiselev.telegram.ApplicationConfig.TELEGRAM_BOT_API_TOKEN;

/**
 * SpringConfiguration class
 * Содержит конфигурацию бинов Spring
 */
@Configuration
public class SpringConfiguration {
    @Bean
    public TelegramBot bot() {
        return TelegramBotAdapter.build(TELEGRAM_BOT_API_TOKEN);
    }

    @Bean
    @Autowired
    public Consumer<SendMessage> sendFunction(SendCompletableFutureFabric sendCompletableFutureFabric) {
        return sendCompletableFutureFabric.getSendFunction();
    }

    @Bean
    BlockingQueue<SendMessage> sendQueue() {
        return new LinkedBlockingQueue<>();
    }
}
