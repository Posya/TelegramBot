package site.kiselev.telegram;

/**
 * Created by posya on 10/5/16.
 */
public interface UserSessionFactory {
    UserSession getUserSession(Integer userID);
}
