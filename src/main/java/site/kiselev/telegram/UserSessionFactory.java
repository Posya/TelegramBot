package site.kiselev.telegram;

/**
 * UserSessionFactory interface
 */
public interface UserSessionFactory {
    UserSession getUserSession(Integer userID);
}
