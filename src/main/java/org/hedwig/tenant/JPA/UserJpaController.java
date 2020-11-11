/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.JPA;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hedwig.tenant.entities.Role;
import org.hedwig.tenant.entities.Userlogintrace;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.hedwig.tenant.JPA.exceptions.IllegalOrphanException;
import org.hedwig.tenant.JPA.exceptions.NonexistentEntityException;
import org.hedwig.tenant.JPA.exceptions.PreexistingEntityException;
import org.hedwig.tenant.entities.User;
import org.hedwig.tenant.entities.UserPK;

/**
 *
 * @author dgrfiv
 */
public class UserJpaController implements Serializable {

    public UserJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) throws PreexistingEntityException, Exception {
        if (user.getUserPK() == null) {
            user.setUserPK(new UserPK());
        }
        if (user.getUserlogintraceList() == null) {
            user.setUserlogintraceList(new ArrayList<Userlogintrace>());
        }
        user.getUserPK().setProductId(user.getRole().getRolePK().getProductId());
        user.getUserPK().setTenantId(user.getRole().getRolePK().getTenantId());
        user.getUserPK().setRoleId(user.getRole().getRolePK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Role role = user.getRole();
            if (role != null) {
                role = em.getReference(role.getClass(), role.getRolePK());
                user.setRole(role);
            }
            List<Userlogintrace> attachedUserlogintraceList = new ArrayList<Userlogintrace>();
            for (Userlogintrace userlogintraceListUserlogintraceToAttach : user.getUserlogintraceList()) {
                userlogintraceListUserlogintraceToAttach = em.getReference(userlogintraceListUserlogintraceToAttach.getClass(), userlogintraceListUserlogintraceToAttach.getUserlogintracePK());
                attachedUserlogintraceList.add(userlogintraceListUserlogintraceToAttach);
            }
            user.setUserlogintraceList(attachedUserlogintraceList);
            em.persist(user);
            if (role != null) {
                role.getUserList().add(user);
                role = em.merge(role);
            }
            for (Userlogintrace userlogintraceListUserlogintrace : user.getUserlogintraceList()) {
                User oldUserOfUserlogintraceListUserlogintrace = userlogintraceListUserlogintrace.getUser();
                userlogintraceListUserlogintrace.setUser(user);
                userlogintraceListUserlogintrace = em.merge(userlogintraceListUserlogintrace);
                if (oldUserOfUserlogintraceListUserlogintrace != null) {
                    oldUserOfUserlogintraceListUserlogintrace.getUserlogintraceList().remove(userlogintraceListUserlogintrace);
                    oldUserOfUserlogintraceListUserlogintrace = em.merge(oldUserOfUserlogintraceListUserlogintrace);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUser(user.getUserPK()) != null) {
                throw new PreexistingEntityException("User " + user + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(User user) throws IllegalOrphanException, NonexistentEntityException, Exception {
        user.getUserPK().setProductId(user.getRole().getRolePK().getProductId());
        user.getUserPK().setTenantId(user.getRole().getRolePK().getTenantId());
        user.getUserPK().setRoleId(user.getRole().getRolePK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User persistentUser = em.find(User.class, user.getUserPK());
            Role roleOld = persistentUser.getRole();
            Role roleNew = user.getRole();
            List<Userlogintrace> userlogintraceListOld = persistentUser.getUserlogintraceList();
            List<Userlogintrace> userlogintraceListNew = user.getUserlogintraceList();
            List<String> illegalOrphanMessages = null;
            for (Userlogintrace userlogintraceListOldUserlogintrace : userlogintraceListOld) {
                if (!userlogintraceListNew.contains(userlogintraceListOldUserlogintrace)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Userlogintrace " + userlogintraceListOldUserlogintrace + " since its user field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (roleNew != null) {
                roleNew = em.getReference(roleNew.getClass(), roleNew.getRolePK());
                user.setRole(roleNew);
            }
            List<Userlogintrace> attachedUserlogintraceListNew = new ArrayList<Userlogintrace>();
            for (Userlogintrace userlogintraceListNewUserlogintraceToAttach : userlogintraceListNew) {
                userlogintraceListNewUserlogintraceToAttach = em.getReference(userlogintraceListNewUserlogintraceToAttach.getClass(), userlogintraceListNewUserlogintraceToAttach.getUserlogintracePK());
                attachedUserlogintraceListNew.add(userlogintraceListNewUserlogintraceToAttach);
            }
            userlogintraceListNew = attachedUserlogintraceListNew;
            user.setUserlogintraceList(userlogintraceListNew);
            user = em.merge(user);
            if (roleOld != null && !roleOld.equals(roleNew)) {
                roleOld.getUserList().remove(user);
                roleOld = em.merge(roleOld);
            }
            if (roleNew != null && !roleNew.equals(roleOld)) {
                roleNew.getUserList().add(user);
                roleNew = em.merge(roleNew);
            }
            for (Userlogintrace userlogintraceListNewUserlogintrace : userlogintraceListNew) {
                if (!userlogintraceListOld.contains(userlogintraceListNewUserlogintrace)) {
                    User oldUserOfUserlogintraceListNewUserlogintrace = userlogintraceListNewUserlogintrace.getUser();
                    userlogintraceListNewUserlogintrace.setUser(user);
                    userlogintraceListNewUserlogintrace = em.merge(userlogintraceListNewUserlogintrace);
                    if (oldUserOfUserlogintraceListNewUserlogintrace != null && !oldUserOfUserlogintraceListNewUserlogintrace.equals(user)) {
                        oldUserOfUserlogintraceListNewUserlogintrace.getUserlogintraceList().remove(userlogintraceListNewUserlogintrace);
                        oldUserOfUserlogintraceListNewUserlogintrace = em.merge(oldUserOfUserlogintraceListNewUserlogintrace);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                UserPK id = user.getUserPK();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(UserPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getUserPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Userlogintrace> userlogintraceListOrphanCheck = user.getUserlogintraceList();
            for (Userlogintrace userlogintraceListOrphanCheckUserlogintrace : userlogintraceListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Userlogintrace " + userlogintraceListOrphanCheckUserlogintrace + " in its userlogintraceList field has a non-nullable user field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Role role = user.getRole();
            if (role != null) {
                role.getUserList().remove(user);
                role = em.merge(role);
            }
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
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

    public User findUser(UserPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
