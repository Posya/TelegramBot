package site.kiselev.telegram.threads;

/**
 * Created by posya on 10/5/16.
 */
public interface ReceiveThread extends Runnable {
    @Override
    void run();

    void exit();
}
