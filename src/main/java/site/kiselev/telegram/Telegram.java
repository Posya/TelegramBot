package site.kiselev.telegram;

import com.pengrad.telegrambot.TelegramBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.kiselev.telegram.threads.ReceiveThread;
import site.kiselev.telegram.threads.SendThread;

/**
 * Telegram class
 */
public class Telegram {

    private final Logger logger = LoggerFactory.getLogger(Telegram.class);

    private TelegramBot bot;
    private ReceiveThread receiveThread = new ReceiveThread(bot);
    private SendThread sendThread = new SendThread(bot);

    private UserSessionFactory userSessionFactory = new UserSessionFactory();

}
