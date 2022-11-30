package dj.arbuz.bots.database.hibernate;

import dj.arbuz.bots.TelegramConfigPaths;
import dj.arbuz.bots.database.hibernate.group.GroupDto;
import dj.arbuz.bots.database.hibernate.user.UserDto;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    /**
     * Конструктор - приватный тк этот запрещено создавать экземпляры этого класса
     */
    private HibernateUtil() {
        throw new IllegalStateException("Этот класс нельзя создать");
    }

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure(new File(TelegramConfigPaths.HIBERNATE_CONFIG_PATH_AS_STRING))
                    .addAnnotatedClass(UserDto.class)
                    .addAnnotatedClass(GroupDto.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}