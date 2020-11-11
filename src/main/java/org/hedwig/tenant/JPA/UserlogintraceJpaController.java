/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.JPA;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hedwig.tenant.JPA.exceptions.NonexistentEntityException;
import org.hedwig.tenant.JPA.exceptions.PreexistingEntityException;
import org.hedwig.tenant.entities.User;
import org.hedwig.tenant.entities.Userlogintrace;
import org.hedwig.tenant.entities.UserlogintracePK;

/**
 *
 * @author dgrfiv
 */
public class UserlogintraceJpaController implements Serializable {

    public UserlogintraceJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Userlogintrace userlogintrace) throws PreexistingEntityException, Exception {
        if (userlogintrace.getUserlogintracePK() == null) {
            userlogintrace.setUserlogintracePK(new UserlogintracePK());
        }
        userlogintrace.getUserlogintracePK().setUserId(userlogintrace.getUser().getUserPK().getId());
        userlogintrace.getUserlogintracePK().setRoleId(userlogintrace.getUser().getUserPK().getRoleId());
        userlogintrace.getUserlogintracePK().setTenantId(userlogintrace.getUser().getUserPK().getTenantId());
        userlogintrace.getUserlogintracePK().setProductId(userlogintrace.getUser().getUserPK().getProductId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User user = userlogintrace.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getUserPK());
                userlogintrace.setUser(user);
            }
            em.persist(userlogintrace);
            if (user != null) {
                user.getUserlogintraceList().add(userlogintrace);
                user = em.merge(user);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUserlogintrace(userlogintrace.getUserlogintracePK()) != null) {
                throw new PreexistingEntityException("Userlogintrace " + userlogintrace + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Userlogintrace userlogintrace) throws NonexistentEntityException, Exception {
        userlogintrace.getUserlogintracePK().setUserId(userlogintrace.getUser().getUserPK().getId());
        userlogintrace.getUserlogintracePK().setRoleId(userlogintrace.getUser().getUserPK().getRoleId());
        userlogintrace.getUserlogintracePK().setTenantId(userlogintrace.getUser().getUserPK().getTenantId());
        userlogintrace.getUserlogintracePK().setProductId(userlogintrace.getUser().getUserPK().getProductId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Userlogintrace persistentUserlogintrace = em.find(Userlogintrace.class, userlogintrace.getUserlogintracePK());
            User userOld = persistentUserlogintrace.getUser();
            User userNew = userlogintrace.getUser();
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getUserPK());
                userlogintrace.setUser(userNew);
            }
            userlogintrace = em.merge(userlogintrace);
            if (userOld != null && !userOld.equals(userNew)) {
                userOld.getUserlogintraceList().remove(userlogintrace);
                userOld = em.merge(userOld);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                userNew.getUserlogintraceList().add(userlogintrace);
                userNew = em.merge(userNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                UserlogintracePK id = userlogintrace.getUserlogintracePK();
                if (findUserlogintrace(id) == null) {
                    throw new NonexistentEntityException("The userlogintrace with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(UserlogintracePK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Userlogintrace userlogintrace;
            try {
                userlogintrace = em.getReference(Userlogintrace.class, id);
                userlogintrace.getUserlogintracePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The userlogintrace with id " + id + " no longer exists.", enfe);
            }
            User user = userlogintrace.getUser();
            if (user != null) {
                user.getUserlogintraceList().remove(userlogintrace);
                user = em.merge(user);
            }
            em.remove(userlogintrace);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Userlogintrace> findUserlogintraceEntities() {
        return findUserlogintraceEntities(true, -1, -1);
    }

    public List<Userlogintrace> findUserlogintraceEntities(int maxResults, int firstResult) {
        return findUserlogintraceEntities(false, maxResults, firstResult);
    }

    private List<Userlogintrace> findUserlogintraceEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Userlogintrace.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Userlogintrace findUserlogintrace(UserlogintracePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Userlogintrace.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserlogintraceCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Userlogintrace> rt = cq.from(Userlogintrace.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
