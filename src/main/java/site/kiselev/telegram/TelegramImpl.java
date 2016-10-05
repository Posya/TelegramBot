package site.kiselev.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.kiselev.telegram.threads.ReceiveThread;
import site.kiselev.telegram.threads.SendThread;

/**
 * Telegram class
 */
public class TelegramImpl implements Telegram {

    private final Logger logger = LoggerFactory.getLogger(TelegramImpl.class);

    private TelegramBot bot = TelegramBotAdapter.build("BOT_TOKEN");;
    private SendThread sendThread;
    private UserSessionFactory userSessionFactory;
    private ReceiveThread receiveThread;


    @Override
    public void run() {

    }
}
