package jee;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HiberManager {

    private static SessionFactory sessionFactory = null;

    public HiberManager() {
        try {
            loadSessionFactory();
        } catch (Throwable e) {
            System.err.println("Exception while initializing hibernate util.. ");
            e.printStackTrace();
        }
    }

    public static void loadSessionFactory() {

        Configuration configuration = new Configuration();
        configuration.configure("/hibernate.cfg.xml");
        configuration.addAnnotatedClass(UserEntity.class);
        configuration.addAnnotatedClass(FileEntity.class);
        configuration.addAnnotatedClass(CategoryEntity.class);
        ServiceRegistry srvcReg = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        sessionFactory = configuration.buildSessionFactory(srvcReg);
    }

    public static Session getSession() throws HibernateException {

        Session retSession = null;
        try {
            retSession = sessionFactory.openSession();
        } catch (Throwable t) {
            System.err.println("Exception while getting session.. ");
            t.printStackTrace();
        }
        if (retSession == null) {
            System.err.println("session is discovered null");
        }

        return retSession;
    }

    public List<UserEntity> getAllUsers() {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        Query q = s.createQuery("from UserEntity order by id");
        List<UserEntity> res = q.list();
        s.getTransaction().commit();
        return res;
    }

    public Integer countAllUsers() {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        Query q = s.createQuery("select count(*) from UserEntity usr");
        Integer res = ((Long) q.uniqueResult()).intValue();
        s.getTransaction().commit();
        return res;
    }

    public Integer countAllCategories() {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        Query q = s.createQuery("select count(*) from CategoryEntity cat");
        Integer res = ((Long) q.uniqueResult()).intValue();
        s.getTransaction().commit();
        return res;
    }

    public UserEntity findUser(String username) {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        Query q = s.createQuery("from UserEntity where username = :username");
        q.setParameter("username", username);
        try {
            s.getTransaction().commit();
        } catch (org.hibernate.StaleStateException e) {
            return null;
        }
        if (q.list().isEmpty()) {
            return null;
        } else {
            return (UserEntity) q.list().get(0);
        }
    }

    public UserEntity findUser(Integer uid) {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        Query q = s.createQuery("from UserEntity where id = :id");
        q.setParameter("id", uid);
        try {
            s.getTransaction().commit();
        } catch (org.hibernate.StaleStateException e) {
            return null;
        }
        if (q.list().isEmpty()) {
            return null;
        } else {
            return (UserEntity) q.list().get(0);
        }
    }

    public CategoryEntity findCategory(Integer id) {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        Query q = s.createQuery("from CategoryEntity where id = :id");
        q.setParameter("id", id);
        s.getTransaction().commit();
        if (q.list().isEmpty()) {
            return null;
        } else {
            return (CategoryEntity) q.list().get(0);
        }
    }

    public FileEntity getFile(Integer file_id) {
        Session s = sessionFactory.openSession();
        s.beginTransaction();
        Query q = s.createQuery("from FileEntity where id = :id");
        q.setParameter("id", file_id);
        s.getTransaction().commit();
        if (q.list().isEmpty()) {
            return null;
        } else {
            return (FileEntity) q.list().get(0);
        }
    }

    public List<FileEntity> getEveryoneFiles(Integer category_id) {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        Query q = s.createQuery("from FileEntity where category_id = :id and visibility = 'everyone'");
        q.setParameter("id", category_id);
        s.getTransaction().commit();
        if (q.list().isEmpty()) {
            return null;
        } else {
            return (List<FileEntity>) q.list();
        }
    }

    public List<FileEntity> getEveryoneFiles() {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        Query q = s.createQuery("from FileEntity where visibility = 'everyone'");
        s.getTransaction().commit();
        if (q.list().isEmpty()) {
            return null;
        } else {
            return (List<FileEntity>) q.list();
        }
    }

    public Integer getEveryoneFilesCount() {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        Query q = s.createQuery("select count(*) from FileEntity fe where visibility = 'everyone'");
        s.getTransaction().commit();
        return ((Long) q.uniqueResult()).intValue();
    }
    
    public List<FileEntity> getSUFiles(Integer category_id, Integer user_id) {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        Query q = s.createQuery("from FileEntity where category_id = :id and (visibility = 'everyone' or visibility = 'registered' or visibility = 'me' and owner_id = :owner_id)");
        q.setParameter("id", category_id);
        q.setParameter("owner_id", user_id);
        s.getTransaction().commit();
        if (q.list().isEmpty()) {
            return null;
        } else {
            return (List<FileEntity>) q.list();
        }
    }

    public List<FileEntity> getSUFiles(Integer user_id) {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        Query q = s.createQuery("from FileEntity where (visibility = 'everyone' or visibility = 'registered' or visibility = 'me' and owner_id = :owner_id)");
        q.setParameter("owner_id", user_id);
        s.getTransaction().commit();
        if (q.list().isEmpty()) {
            return null;
        } else {
            return (List<FileEntity>) q.list();
        }
    }

    public Integer getSUFilesCount(Integer user_id) {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        Query q = s.createQuery("select count(*) from FileEntity fe where (visibility = 'everyone' or visibility = 'registered' or visibility = 'me' and owner_id = :owner_id)");
        q.setParameter("owner_id", user_id);
        s.getTransaction().commit();
        return ((Long) q.uniqueResult()).intValue();
    }

    public List<FileEntity> getPUFiles(Integer category_id) {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        Query q = s.createQuery("from FileEntity where category_id = :id");
        q.setParameter("id", category_id);
        s.getTransaction().commit();
        if (q.list().isEmpty()) {
            return null;
        } else {
            return (List<FileEntity>) q.list();
        }
    }

    public Integer getPUFilesCount() {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        Query q = s.createQuery("select count(*) from FileEntity fe");
        s.getTransaction().commit();
        return ((Long) q.uniqueResult()).intValue();
    }

    public List<FileEntity> getPUFiles() {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        Query q = s.createQuery("from FileEntity order by id");
        s.getTransaction().commit();
        if (q.list().isEmpty()) {
            return null;
        } else {
            return (List<FileEntity>) q.list();
        }
    }

    public void saveUser(UserEntity toSave) {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        s.save(toSave);
        s.getTransaction().commit();
    }

    public void updateUser(UserEntity toSave) {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        s.saveOrUpdate(toSave);
        s.getTransaction().commit();
    }

    public void removeUser(UserEntity toRemove) {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        s.delete(toRemove);
        s.getTransaction().commit();
    }

    public List<CategoryEntity> getAllCategories() {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        Query q = s.createQuery("from CategoryEntity order by id");
        List<CategoryEntity> res = q.list();
        s.getTransaction().commit();
        return res;
    }

    public void saveCategory(CategoryEntity toSave) {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        s.saveOrUpdate(toSave);
        s.getTransaction().commit();
    }
    
    private void clearCategory(CategoryEntity toRemove) {
        List<FileEntity> leftoutFiles = getPUFiles(toRemove.getId());
        for(FileEntity f : leftoutFiles) removeFile(f);
    }

    public void removeCategory(CategoryEntity toRemove) {

        if(getPUFiles(toRemove.getId()) != null) clearCategory(toRemove);
        
        Session s = sessionFactory.openSession();
        s.beginTransaction();
        s.delete(toRemove);
        s.getTransaction().commit();
    }

    public void removeFile(FileEntity toRemove) {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        s.delete(toRemove);
        s.getTransaction().commit();
    }

    public void saveFile(FileEntity toSave) {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        s.saveOrUpdate(toSave);
        s.getTransaction().commit();
    }
    
    public void updateFile(FileEntity toSave) {

        Session s = sessionFactory.openSession();
        s.beginTransaction();
        s.update(toSave);
        s.getTransaction().commit();
    }
}
