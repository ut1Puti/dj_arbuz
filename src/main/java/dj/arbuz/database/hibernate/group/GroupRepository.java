package dj.arbuz.database.hibernate.group;

import dj.arbuz.database.hibernate.HibernateUtil;
import dj.arbuz.database.hibernate.entity.EntityRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public final class GroupRepository extends EntityRepository<GroupDto> {

    public GroupDto getByScreenName(String groupScreenName) {
        Transaction transaction = null;
        GroupDto result;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<GroupDto> query = session.createQuery("select b from dj.arbuz.database.hibernate.group.GroupDto b where b.groupName = :name", GroupDto.class);
            query.setParameter("name", groupScreenName);
            result = query.uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        return result;
    }

    public List<GroupDto> findBySubscriberId(String subscriberId) {
        Transaction transaction = null;
        List<GroupDto> result;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<GroupDto> query = session.createQuery("select b from dj.arbuz.database.hibernate.group.GroupDto b join b.subscribedUsers u where u.telegramId = :id", GroupDto.class);
            query.setParameter("id", subscriberId);
            result = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ArrayList<>();
        }
        return result;
    }

    public List<String> findBySubscriberIdGroupsScreenName(String subscriberId) {
        Transaction transaction = null;
        List<String> result;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<String> query = session.createQuery("select b.groupName from dj.arbuz.database.hibernate.group.GroupDto b join b.subscribedUsers u where u.telegramId = :id", String.class);
            query.setParameter("id", subscriberId);
            result = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ArrayList<>();
        }
        return result;
    }

    public List<GroupDto> findAllGroups() {
        Transaction transaction = null;
        List<GroupDto> result;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            result = new ArrayList<>(session.createQuery("select b from dj.arbuz.database.hibernate.group.GroupDto b", GroupDto.class).getResultList());
            transaction.commit();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ArrayList<>();
        }
        return result;
    }

    public List<String> findAllGroupsScreenName() {
        Transaction transaction = null;
        List<String> result;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            result = new ArrayList<>(session.createQuery("select b.groupName from dj.arbuz.database.hibernate.group.GroupDto b", String.class).getResultList());
            transaction.commit();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ArrayList<>();
        }
        return result;
    }

    public List<String> findSubscribedUsersTelegramId(String groupScreenName) {
        Transaction transaction = null;
        List<String> result;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<String> query = session.createQuery("select u.telegramId from dj.arbuz.database.hibernate.group.GroupDto b join b.subscribedUsers u where b.groupName = :name", String.class);
            query.setParameter("name", groupScreenName);
            result = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ArrayList<>();
        }
        return result;
    }
}
