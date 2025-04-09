package utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.SessionFactory;

public class HibernateUtil {
    private static final EntityManagerFactory entityManagerFactory = buildEntityManagerFactory();

    private static EntityManagerFactory buildEntityManagerFactory() {
        try {
            // Charge la configuration Hibernate
            SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
            return sessionFactory.unwrap(EntityManagerFactory.class); // Convertit en EntityManagerFactory
        } catch (Throwable ex) {
            System.err.println("❌ Erreur d'initialisation de l'EntityManagerFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManager getEntityManager() {
        if (entityManagerFactory == null) {
            System.err.println("❌ EntityManagerFactory est null !");
            return null;
        }
        return entityManagerFactory.createEntityManager();
    }
}
