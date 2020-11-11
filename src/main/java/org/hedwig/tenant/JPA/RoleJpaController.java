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
import org.hedwig.tenant.entities.Subscription;
import org.hedwig.tenant.entities.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.hedwig.tenant.JPA.exceptions.IllegalOrphanException;
import org.hedwig.tenant.JPA.exceptions.NonexistentEntityException;
import org.hedwig.tenant.JPA.exceptions.PreexistingEntityException;
import org.hedwig.tenant.entities.Role;
import org.hedwig.tenant.entities.RolePK;

/**
 *
 * @author dgrfiv
 */
public class RoleJpaController implements Serializable {

    public RoleJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Role role) throws PreexistingEntityException, Exception {
        if (role.getRolePK() == null) {
            role.setRolePK(new RolePK());
        }
        if (role.getUserList() == null) {
            role.setUserList(new ArrayList<User>());
        }
        role.getRolePK().setProductId(role.getSubscription().getSubscriptionPK().getProductId());
        role.getRolePK().setTenantId(role.getSubscription().getSubscriptionPK().getTenantId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Subscription subscription = role.getSubscription();
            if (subscription != null) {
                subscription = em.getReference(subscription.getClass(), subscription.getSubscriptionPK());
                role.setSubscription(subscription);
            }
            List<User> attachedUserList = new ArrayList<User>();
            for (User userListUserToAttach : role.getUserList()) {
                userListUserToAttach = em.getReference(userListUserToAttach.getClass(), userListUserToAttach.getUserPK());
                attachedUserList.add(userListUserToAttach);
            }
            role.setUserList(attachedUserList);
            em.persist(role);
            if (subscription != null) {
                subscription.getRoleList().add(role);
                subscription = em.merge(subscription);
            }
            for (User userListUser : role.getUserList()) {
                Role oldRoleOfUserListUser = userListUser.getRole();
                userListUser.setRole(role);
                userListUser = em.merge(userListUser);
                if (oldRoleOfUserListUser != null) {
                    oldRoleOfUserListUser.getUserList().remove(userListUser);
                    oldRoleOfUserListUser = em.merge(oldRoleOfUserListUser);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRole(role.getRolePK()) != null) {
                throw new PreexistingEntityException("Role " + role + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Role role) throws IllegalOrphanException, NonexistentEntityException, Exception {
        role.getRolePK().setProductId(role.getSubscription().getSubscriptionPK().getProductId());
        role.getRolePK().setTenantId(role.getSubscription().getSubscriptionPK().getTenantId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Role persistentRole = em.find(Role.class, role.getRolePK());
            Subscription subscriptionOld = persistentRole.getSubscription();
            Subscription subscriptionNew = role.getSubscription();
            List<User> userListOld = persistentRole.getUserList();
            List<User> userListNew = role.getUserList();
            List<String> illegalOrphanMessages = null;
            for (User userListOldUser : userListOld) {
                if (!userListNew.contains(userListOldUser)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain User " + userListOldUser + " since its role field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (subscriptionNew != null) {
                subscriptionNew = em.getReference(subscriptionNew.getClass(), subscriptionNew.getSubscriptionPK());
                role.setSubscription(subscriptionNew);
            }
            List<User> attachedUserListNew = new ArrayList<User>();
            for (User userListNewUserToAttach : userListNew) {
                userListNewUserToAttach = em.getReference(userListNewUserToAttach.getClass(), userListNewUserToAttach.getUserPK());
                attachedUserListNew.add(userListNewUserToAttach);
            }
            userListNew = attachedUserListNew;
            role.setUserList(userListNew);
            role = em.merge(role);
            if (subscriptionOld != null && !subscriptionOld.equals(subscriptionNew)) {
                subscriptionOld.getRoleList().remove(role);
                subscriptionOld = em.merge(subscriptionOld);
            }
            if (subscriptionNew != null && !subscriptionNew.equals(subscriptionOld)) {
                subscriptionNew.getRoleList().add(role);
                subscriptionNew = em.merge(subscriptionNew);
            }
            for (User userListNewUser : userListNew) {
                if (!userListOld.contains(userListNewUser)) {
                    Role oldRoleOfUserListNewUser = userListNewUser.getRole();
                    userListNewUser.setRole(role);
                    userListNewUser = em.merge(userListNewUser);
                    if (oldRoleOfUserListNewUser != null && !oldRoleOfUserListNewUser.equals(role)) {
                        oldRoleOfUserListNewUser.getUserList().remove(userListNewUser);
                        oldRoleOfUserListNewUser = em.merge(oldRoleOfUserListNewUser);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RolePK id = role.getRolePK();
                if (findRole(id) == null) {
                    throw new NonexistentEntityException("The role with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(RolePK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Role role;
            try {
                role = em.getReference(Role.class, id);
                role.getRolePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The role with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<User> userListOrphanCheck = role.getUserList();
            for (User userListOrphanCheckUser : userListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Role (" + role + ") cannot be destroyed since the User " + userListOrphanCheckUser + " in its userList field has a non-nullable role field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Subscription subscription = role.getSubscription();
            if (subscription != null) {
                subscription.getRoleList().remove(role);
                subscription = em.merge(subscription);
            }
            em.remove(role);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Role> findRoleEntities() {
        return findRoleEntities(true, -1, -1);
    }

    public List<Role> findRoleEntities(int maxResults, int firstResult) {
        return findRoleEntities(false, maxResults, firstResult);
    }

    private List<Role> findRoleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Role.class));
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

    public Role findRole(RolePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Role.class, id);
        } finally {
            em.close();
        }
    }

    public int getRoleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Role> rt = cq.from(Role.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
