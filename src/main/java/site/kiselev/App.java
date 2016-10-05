package site.kiselev;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import site.kiselev.telegram.Telegram;

/**
 * Main Application class
 */
public class App {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");

        Telegram telegram = context.getBean("Telegram", Telegram.class);
        telegram.run();
    }

}
