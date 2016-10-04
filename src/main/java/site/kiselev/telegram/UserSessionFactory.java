package site.kiselev.telegram;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * UserSessionFactory
 */
public class UserSessionFactory {

    private final Logger logger = LoggerFactory.getLogger(UserSessionFactory.class);

    private RemovalListener<Integer, UserSession> removalListener = removal -> {
        UserSession userSession = removal.getValue();
        assert userSession != null;
        userSession.stop();
    };

    private LoadingCache<Integer, UserSession> cache;

    public UserSessionFactory(CacheLoader<Integer, UserSession> otherLoader) {
        logger.debug("Creating new UserSessionFactory with custom loader");
        cache = buildCache(otherLoader);
    }

    public UserSessionFactory() {
        logger.debug("Creating new UserSessionFactory");
        CacheLoader<Integer, UserSession> loader = new CacheLoader<Integer, UserSession>() {
            @Override
            public UserSession load(Integer userID) throws Exception {
                return new UserSession(userID);
            }
        };
        cache = buildCache(loader);
    }


    private LoadingCache<Integer, UserSession> buildCache(CacheLoader<Integer, UserSession> loader) {
        return CacheBuilder.newBuilder()
                .expireAfterAccess(ApplicationConfig.EXPIRE_AFTER_TIMEOUT, ApplicationConfig.EXPIRE_AFTER_TIMEOUT_TIME_UNIT)
                .removalListener(removalListener)
                .build(loader);
    }


    public  UserSession getUserSession(Integer userID)  {
        logger.trace("Getting UserSession for user {}", userID);
        try {
            return cache.get(userID);
        } catch (ExecutionException e) {
            logger.error("Can't get UserSession for user {}: {}", userID, e);
            logger.trace(Arrays.toString(e.getStackTrace()));
            throw new Error("Can't get UserSession", e);
        }
    }
}
