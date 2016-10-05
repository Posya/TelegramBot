package site.kiselev.telegram;

import com.pengrad.telegrambot.model.Update;

/**
 * UserSession interface
 */
public interface UserSession {
    void newMessage(Update update);
    void stop();
}
