package hibernate.UsersDao;

import hibernate.HibernateUtil;
import hibernate.entity.GroupData;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Репозиторий для взаимодействия с Entity классом и базой данных
 */
public class GroupDao {
    /**
     * Метод для сохранения класса группы в базе данных
     * @param groupData - Entity класс
     * @return логические выражения
     */
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

    /**
     * Метод для получения группы по её названию
     * @param groupName имя группы
     * @return Entity класс группы
     */
    public GroupData getGroup(String groupName) {
        Transaction transaction = null;
        GroupData groupData;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            groupData = session.get(GroupData.class, groupName);
            transaction.commit();
        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }

            return null;
        }
        return groupData;
    }

    /**
     * Метод для обновления группы в базе данных
     * @param groupData Entity класс группы
     * @return логическое выражение
     */
    public boolean updateGroup(GroupData groupData) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
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

    /**
     * Метод для удаления группы из базы данных
     * @param groupId Entity класс группы
     */
    public void deleteGroup(String groupId) {
        Transaction transaction = null;
        GroupData group = new GroupData();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
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

    /**
     * Метод для получения всех групп в таблице базы данных
     * @return Список Entity классов группы
     */
    public List<GroupData> getAllGroups() {
        Transaction transaction = null;
        List<GroupData> groups = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            groups = session.createQuery("from GroupData",GroupData.class).list();
            transaction.commit();
        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }

        }
        return groups;
    }
}
