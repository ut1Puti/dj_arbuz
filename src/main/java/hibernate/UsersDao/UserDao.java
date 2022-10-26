package hibernate.UsersDao;

import hibernate.HibernateUtil;
import org.hibernate.Session;
import hibernate.entity.UserData;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UserDao {
    public void saveUser(UserData user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory()
                                            .openSession()) {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    public UserData getUser(String telegramId) {
        Transaction transaction = null;
        UserData user = null;
        try (Session session = HibernateUtil.getSessionFactory()
                                            .openSession()) {
            transaction = session.beginTransaction();
            user = session.get(UserData.class, telegramId);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
        return user;
    }

    public void updateUser(UserData user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory()
                                            .openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
    public void deleteUser(String id) {
        Transaction transaction = null;
        UserData user = new UserData();
        try (Session session = HibernateUtil.getSessionFactory()
                                            .openSession()) {
            transaction = session.beginTransaction();
            user = session.get(UserData.class,id);
            session.delete(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
}
