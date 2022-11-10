package hibernate.UsersDao;

import hibernate.HibernateUtil;
import hibernate.entity.GroupData;
import org.hibernate.Session;
import hibernate.entity.UserData;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class GroupDao {
    public boolean saveGroup(GroupData groupData) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory()
                                            .openSession()) {
            transaction = session.beginTransaction();
            session.save(groupData);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return false;
        }
        return true;
    }
    public GroupData getGroup(String groupName) {
        Transaction transaction = null;
        GroupData groupData = null;
        try (Session session = HibernateUtil.getSessionFactory()
                                            .openSession()) {
            transaction = session.beginTransaction();
            groupData = session.get(GroupData.class, groupName);
            transaction.commit();
        } catch (Exception e) {
            groupData = null;
            if (transaction != null) {
                transaction.rollback();
            }
        }
        return groupData;
    }

    public boolean updateGroup(GroupData groupData) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory()
                                            .openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(groupData);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return false;
        }
        return true;
    }

    public void deleteGroup(String groupId) {
        Transaction transaction = null;
        GroupData group = new GroupData();
        try (Session session = HibernateUtil.getSessionFactory()
                                            .openSession()) {
            transaction = session.beginTransaction();
            group = session.get(GroupData.class,groupId);
            session.delete(group);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
    public List<GroupData> getAllGroups() {
        Transaction transaction = null;
        List<GroupData> groups = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            groups = session.createQuery("from GroupData",GroupData.class).list();
            transaction.commit();
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

        }
        return groups;
    }
}
