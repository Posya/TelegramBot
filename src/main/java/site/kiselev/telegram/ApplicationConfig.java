package site.kiselev.telegram;

import java.util.concurrent.TimeUnit;

/**
 * ApplicationConfig
 */
public class ApplicationConfig {
    // Telegram
    public static final String TELEGRAM_BOT_API_TOKEN = "221342586:AAEnk-hdOBtaFQaPlhT01zsx8HdPH5Wlg_o";

    // UserSessionFactory
    public static final long EXPIRE_AFTER_TIMEOUT = 15;
    public static final TimeUnit EXPIRE_AFTER_TIMEOUT_TIME_UNIT = TimeUnit.MINUTES;

}
