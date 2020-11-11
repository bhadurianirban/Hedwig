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
import org.hedwig.tenant.entities.Product;
import org.hedwig.tenant.entities.Tenant;
import org.hedwig.tenant.entities.Role;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.hedwig.tenant.JPA.exceptions.IllegalOrphanException;
import org.hedwig.tenant.JPA.exceptions.NonexistentEntityException;
import org.hedwig.tenant.JPA.exceptions.PreexistingEntityException;
import org.hedwig.tenant.entities.Subscription;
import org.hedwig.tenant.entities.SubscriptionPK;

/**
 *
 * @author dgrfiv
 */
public class SubscriptionJpaController implements Serializable {

    public SubscriptionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Subscription subscription) throws PreexistingEntityException, Exception {
        if (subscription.getSubscriptionPK() == null) {
            subscription.setSubscriptionPK(new SubscriptionPK());
        }
        if (subscription.getRoleList() == null) {
            subscription.setRoleList(new ArrayList<Role>());
        }
        subscription.getSubscriptionPK().setTenantId(subscription.getTenant().getId());
        subscription.getSubscriptionPK().setProductId(subscription.getProduct().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Product product = subscription.getProduct();
            if (product != null) {
                product = em.getReference(product.getClass(), product.getId());
                subscription.setProduct(product);
            }
            Tenant tenant = subscription.getTenant();
            if (tenant != null) {
                tenant = em.getReference(tenant.getClass(), tenant.getId());
                subscription.setTenant(tenant);
            }
            List<Role> attachedRoleList = new ArrayList<Role>();
            for (Role roleListRoleToAttach : subscription.getRoleList()) {
                roleListRoleToAttach = em.getReference(roleListRoleToAttach.getClass(), roleListRoleToAttach.getRolePK());
                attachedRoleList.add(roleListRoleToAttach);
            }
            subscription.setRoleList(attachedRoleList);
            em.persist(subscription);
            if (product != null) {
                product.getSubscriptionList().add(subscription);
                product = em.merge(product);
            }
            if (tenant != null) {
                tenant.getSubscriptionList().add(subscription);
                tenant = em.merge(tenant);
            }
            for (Role roleListRole : subscription.getRoleList()) {
                Subscription oldSubscriptionOfRoleListRole = roleListRole.getSubscription();
                roleListRole.setSubscription(subscription);
                roleListRole = em.merge(roleListRole);
                if (oldSubscriptionOfRoleListRole != null) {
                    oldSubscriptionOfRoleListRole.getRoleList().remove(roleListRole);
                    oldSubscriptionOfRoleListRole = em.merge(oldSubscriptionOfRoleListRole);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findSubscription(subscription.getSubscriptionPK()) != null) {
                throw new PreexistingEntityException("Subscription " + subscription + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Subscription subscription) throws IllegalOrphanException, NonexistentEntityException, Exception {
        subscription.getSubscriptionPK().setTenantId(subscription.getTenant().getId());
        subscription.getSubscriptionPK().setProductId(subscription.getProduct().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Subscription persistentSubscription = em.find(Subscription.class, subscription.getSubscriptionPK());
            Product productOld = persistentSubscription.getProduct();
            Product productNew = subscription.getProduct();
            Tenant tenantOld = persistentSubscription.getTenant();
            Tenant tenantNew = subscription.getTenant();
            List<Role> roleListOld = persistentSubscription.getRoleList();
            List<Role> roleListNew = subscription.getRoleList();
            List<String> illegalOrphanMessages = null;
            for (Role roleListOldRole : roleListOld) {
                if (!roleListNew.contains(roleListOldRole)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Role " + roleListOldRole + " since its subscription field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (productNew != null) {
                productNew = em.getReference(productNew.getClass(), productNew.getId());
                subscription.setProduct(productNew);
            }
            if (tenantNew != null) {
                tenantNew = em.getReference(tenantNew.getClass(), tenantNew.getId());
                subscription.setTenant(tenantNew);
            }
            List<Role> attachedRoleListNew = new ArrayList<Role>();
            for (Role roleListNewRoleToAttach : roleListNew) {
                roleListNewRoleToAttach = em.getReference(roleListNewRoleToAttach.getClass(), roleListNewRoleToAttach.getRolePK());
                attachedRoleListNew.add(roleListNewRoleToAttach);
            }
            roleListNew = attachedRoleListNew;
            subscription.setRoleList(roleListNew);
            subscription = em.merge(subscription);
            if (productOld != null && !productOld.equals(productNew)) {
                productOld.getSubscriptionList().remove(subscription);
                productOld = em.merge(productOld);
            }
            if (productNew != null && !productNew.equals(productOld)) {
                productNew.getSubscriptionList().add(subscription);
                productNew = em.merge(productNew);
            }
            if (tenantOld != null && !tenantOld.equals(tenantNew)) {
                tenantOld.getSubscriptionList().remove(subscription);
                tenantOld = em.merge(tenantOld);
            }
            if (tenantNew != null && !tenantNew.equals(tenantOld)) {
                tenantNew.getSubscriptionList().add(subscription);
                tenantNew = em.merge(tenantNew);
            }
            for (Role roleListNewRole : roleListNew) {
                if (!roleListOld.contains(roleListNewRole)) {
                    Subscription oldSubscriptionOfRoleListNewRole = roleListNewRole.getSubscription();
                    roleListNewRole.setSubscription(subscription);
                    roleListNewRole = em.merge(roleListNewRole);
                    if (oldSubscriptionOfRoleListNewRole != null && !oldSubscriptionOfRoleListNewRole.equals(subscription)) {
                        oldSubscriptionOfRoleListNewRole.getRoleList().remove(roleListNewRole);
                        oldSubscriptionOfRoleListNewRole = em.merge(oldSubscriptionOfRoleListNewRole);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                SubscriptionPK id = subscription.getSubscriptionPK();
                if (findSubscription(id) == null) {
                    throw new NonexistentEntityException("The subscription with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(SubscriptionPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Subscription subscription;
            try {
                subscription = em.getReference(Subscription.class, id);
                subscription.getSubscriptionPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The subscription with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Role> roleListOrphanCheck = subscription.getRoleList();
            for (Role roleListOrphanCheckRole : roleListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Subscription (" + subscription + ") cannot be destroyed since the Role " + roleListOrphanCheckRole + " in its roleList field has a non-nullable subscription field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Product product = subscription.getProduct();
            if (product != null) {
                product.getSubscriptionList().remove(subscription);
                product = em.merge(product);
            }
            Tenant tenant = subscription.getTenant();
            if (tenant != null) {
                tenant.getSubscriptionList().remove(subscription);
                tenant = em.merge(tenant);
            }
            em.remove(subscription);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Subscription> findSubscriptionEntities() {
        return findSubscriptionEntities(true, -1, -1);
    }

    public List<Subscription> findSubscriptionEntities(int maxResults, int firstResult) {
        return findSubscriptionEntities(false, maxResults, firstResult);
    }

    private List<Subscription> findSubscriptionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Subscription.class));
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

    public Subscription findSubscription(SubscriptionPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Subscription.class, id);
        } finally {
            em.close();
        }
    }

    public int getSubscriptionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Subscription> rt = cq.from(Subscription.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
