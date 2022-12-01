package dj.arbuz.database.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;

public final class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private HibernateUtil() {
        throw new IllegalStateException("Этот класс нельзя создать");
    }

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure(new File("src/main/resources/anonsrc/hibernate.cfg.xml")).buildSessionFactory();
        }
        catch (Throwable ex) {
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