package dao;

import entities.Cours;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import utils.HibernateUtil;

import java.util.Date;
import java.util.List;

public class CoursDAO {

    public List<Cours> getAllCours() {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        List<Cours> coursList = null;

        try {
            coursList = entityManager.createQuery("FROM Cours", Cours.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return coursList;
    }

    public void ajouterCours(Cours cours) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            if (cours.getDateCreation() == null) {
                cours.setDateCreation(new Date());
            }

            entityManager.persist(cours);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public Cours getCoursById(int id) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        Cours cours = null;

        try {
            cours = entityManager.find(Cours.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
        return cours;
    }

    public void updateCours(Cours cours) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.merge(cours);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public void deleteCours(int id) {
        EntityManager entityManager = HibernateUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Cours cours = entityManager.find(Cours.class, id);
            if (cours != null) {
                entityManager.remove(cours);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
