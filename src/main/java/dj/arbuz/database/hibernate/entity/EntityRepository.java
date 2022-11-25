package dj.arbuz.database.hibernate.entity;

import dj.arbuz.database.hibernate.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class EntityRepository<T> {
    public T save(T saveEntity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(saveEntity);
            transaction.commit();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        return saveEntity;
    }

    public T update(T updateEntity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(updateEntity);
            transaction.commit();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        return updateEntity;
    }

    public T delete(T deleteEntity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(deleteEntity);
            transaction.commit();
        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }

            return null;
        }
        return deleteEntity;
    }
}
