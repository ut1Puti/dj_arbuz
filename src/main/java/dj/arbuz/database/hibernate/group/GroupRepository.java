package dj.arbuz.database.hibernate.group;

import dj.arbuz.database.hibernate.HibernateUtil;
import dj.arbuz.database.hibernate.entity.EntityRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class GroupRepository extends EntityRepository<GroupDto> {

    public GroupDto findByScreenName(String groupScreenName) {
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

    public List<GroupDto> findAll() {
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
}
