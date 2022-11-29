package dj.arbuz.bots.database.hibernate.user;

import dj.arbuz.bots.database.hibernate.HibernateUtil;
import dj.arbuz.bots.database.hibernate.entity.EntityRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public final class UserRepository extends EntityRepository<UserDto> {
    public UserDto findByTelegramId(String searchUserTelegramId) {
        Transaction transaction = null;
        UserDto result;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<UserDto> query = session.createQuery("SELECT b FROM dj.arbuz.bots.database.hibernate.user.UserDto b WHERE b.telegramId = :id", UserDto.class);
            query.setParameter("id", searchUserTelegramId);
            result = query.uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            System.err.print(e.getMessage());
            return null;
        }
        return result;
    }

    public List<UserDto> findAll() {
        Transaction transaction = null;
        List<UserDto> result;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            result = new ArrayList<>(session.createQuery("select b from dj.arbuz.bots.database.hibernate.user.UserDto b", UserDto.class).getResultList());
            transaction.commit();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ArrayList<>();
        }
        return result;
    }

    public List<String> getAllUsersId() {
        Transaction transaction = null;
        List<String> result;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            result = new ArrayList<>(session.createQuery("select b.telegramId from dj.arbuz.bots.database.hibernate.user.UserDto b", String.class).getResultList());
            transaction.commit();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ArrayList<>();
        }
        return result;
    }
}
