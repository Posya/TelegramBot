package site.kiselev.telegram.threads;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import org.junit.Before;
import org.junit.Test;
import site.kiselev.telegram.UserSessionFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;


/**
 * ReceiveCallableImplTest
 */
public class ReceiveCallableImplTest {

    private ReceiveCallable receiveCallable;
    private TelegramBot bot;

    @Before
    public void setUp() throws Exception {
        bot = mock(TelegramBot.class);
        GetUpdatesResponse updatesResponse = mock(GetUpdatesResponse.class);
        when(bot.execute(any())).thenReturn(updatesResponse);

        UserSessionFactory factory = mock(UserSessionFactory.class);
        receiveCallable = new ReceiveCallableImpl(bot, factory);

    }

    @Test
    public void call() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<Boolean> receiveFuture = executor.submit(receiveCallable);
        assertFalse("Future was unexpected stopped", receiveFuture.isDone());
        receiveCallable.exit();
        receiveFuture.get(100, TimeUnit.MILLISECONDS);
    }
}