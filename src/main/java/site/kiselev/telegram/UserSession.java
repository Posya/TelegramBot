package site.kiselev.telegram;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

/**
 * UserSession interface
 */
public interface UserSession {
    void newMessage(Update update);
    void stop();
}
