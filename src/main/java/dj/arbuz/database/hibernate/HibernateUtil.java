package dj.arbuz.database.hibernate;

import dj.arbuz.ConfigPaths;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;

public final class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    /**
     * Конструктор - приватный тк этот запрещено создавать экземпляры этого класса
     */
    private HibernateUtil() {
        throw new IllegalStateException("Этот класс нельзя создать");
    }

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure(new File(ConfigPaths.HIBERNATE_CONFIG_PATH_AS_STRING)).buildSessionFactory();
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