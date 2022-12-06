package hibernate.UsersDao;

import hibernate.HibernateUtil;
import org.hibernate.Session;
import hibernate.entity.UserData;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Класс для взаимодействия Entity класса юзера и базой данных
 */
public class UserDao {
    /**
     * Мето для сохранения класса юзера в бд
     * @param user  Entity класс пользователя
     */
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

    /**
     * Получения Entity класса юзера по его айди в телеграмме
     * @param telegramId телеграм айди
     * @return Entity класс юзера
     */
    public UserData getUser(String telegramId) {
        Transaction transaction = null;
        UserData user = null;
        try (Session session = HibernateUtil.getSessionFactory()
                                            .openSession()) {
            transaction = session.beginTransaction();
            user = session.get(UserData.class, telegramId);
            transaction.commit();
        } catch (Exception e) {
            user = null;
            if (transaction != null) {
                transaction.rollback();
            }
        }
        return user;
    }

    /**
     * Метод для обновления таблицы в которой хранятся данные пользователей
     * @param user Entity класс юзера
     */
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

    /**
     * Метод для удаления класса юзера по его айди
     * @param id Айди юзера в телеграме
     */
    public void deleteUser(String id) {
        Transaction transaction = null;
        UserData user = new UserData();
        try (Session session = HibernateUtil.getSessionFactory()
                                            .openSession()) {
            transaction = session.beginTransaction();
            user = session.get(UserData.class, id);
            session.delete(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
}
