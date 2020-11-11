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
import org.hedwig.tenant.entities.Payment;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.hedwig.tenant.JPA.exceptions.IllegalOrphanException;
import org.hedwig.tenant.JPA.exceptions.NonexistentEntityException;
import org.hedwig.tenant.JPA.exceptions.PreexistingEntityException;
import org.hedwig.tenant.entities.Subscription;
import org.hedwig.tenant.entities.Cart;
import org.hedwig.tenant.entities.Tenant;

/**
 *
 * @author dgrfiv
 */
public class TenantJpaController implements Serializable {

    public TenantJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tenant tenant) throws PreexistingEntityException, Exception {
        if (tenant.getPaymentList() == null) {
            tenant.setPaymentList(new ArrayList<Payment>());
        }
        if (tenant.getSubscriptionList() == null) {
            tenant.setSubscriptionList(new ArrayList<Subscription>());
        }
        if (tenant.getCartList() == null) {
            tenant.setCartList(new ArrayList<Cart>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Payment> attachedPaymentList = new ArrayList<Payment>();
            for (Payment paymentListPaymentToAttach : tenant.getPaymentList()) {
                paymentListPaymentToAttach = em.getReference(paymentListPaymentToAttach.getClass(), paymentListPaymentToAttach.getPaymentPK());
                attachedPaymentList.add(paymentListPaymentToAttach);
            }
            tenant.setPaymentList(attachedPaymentList);
            List<Subscription> attachedSubscriptionList = new ArrayList<Subscription>();
            for (Subscription subscriptionListSubscriptionToAttach : tenant.getSubscriptionList()) {
                subscriptionListSubscriptionToAttach = em.getReference(subscriptionListSubscriptionToAttach.getClass(), subscriptionListSubscriptionToAttach.getSubscriptionPK());
                attachedSubscriptionList.add(subscriptionListSubscriptionToAttach);
            }
            tenant.setSubscriptionList(attachedSubscriptionList);
            List<Cart> attachedCartList = new ArrayList<Cart>();
            for (Cart cartListCartToAttach : tenant.getCartList()) {
                cartListCartToAttach = em.getReference(cartListCartToAttach.getClass(), cartListCartToAttach.getCartPK());
                attachedCartList.add(cartListCartToAttach);
            }
            tenant.setCartList(attachedCartList);
            em.persist(tenant);
            for (Payment paymentListPayment : tenant.getPaymentList()) {
                Tenant oldTenantOfPaymentListPayment = paymentListPayment.getTenant();
                paymentListPayment.setTenant(tenant);
                paymentListPayment = em.merge(paymentListPayment);
                if (oldTenantOfPaymentListPayment != null) {
                    oldTenantOfPaymentListPayment.getPaymentList().remove(paymentListPayment);
                    oldTenantOfPaymentListPayment = em.merge(oldTenantOfPaymentListPayment);
                }
            }
            for (Subscription subscriptionListSubscription : tenant.getSubscriptionList()) {
                Tenant oldTenantOfSubscriptionListSubscription = subscriptionListSubscription.getTenant();
                subscriptionListSubscription.setTenant(tenant);
                subscriptionListSubscription = em.merge(subscriptionListSubscription);
                if (oldTenantOfSubscriptionListSubscription != null) {
                    oldTenantOfSubscriptionListSubscription.getSubscriptionList().remove(subscriptionListSubscription);
                    oldTenantOfSubscriptionListSubscription = em.merge(oldTenantOfSubscriptionListSubscription);
                }
            }
            for (Cart cartListCart : tenant.getCartList()) {
                Tenant oldTenantOfCartListCart = cartListCart.getTenant();
                cartListCart.setTenant(tenant);
                cartListCart = em.merge(cartListCart);
                if (oldTenantOfCartListCart != null) {
                    oldTenantOfCartListCart.getCartList().remove(cartListCart);
                    oldTenantOfCartListCart = em.merge(oldTenantOfCartListCart);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTenant(tenant.getId()) != null) {
                throw new PreexistingEntityException("Tenant " + tenant + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tenant tenant) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tenant persistentTenant = em.find(Tenant.class, tenant.getId());
            List<Payment> paymentListOld = persistentTenant.getPaymentList();
            List<Payment> paymentListNew = tenant.getPaymentList();
            List<Subscription> subscriptionListOld = persistentTenant.getSubscriptionList();
            List<Subscription> subscriptionListNew = tenant.getSubscriptionList();
            List<Cart> cartListOld = persistentTenant.getCartList();
            List<Cart> cartListNew = tenant.getCartList();
            List<String> illegalOrphanMessages = null;
            for (Payment paymentListOldPayment : paymentListOld) {
                if (!paymentListNew.contains(paymentListOldPayment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Payment " + paymentListOldPayment + " since its tenant field is not nullable.");
                }
            }
            for (Subscription subscriptionListOldSubscription : subscriptionListOld) {
                if (!subscriptionListNew.contains(subscriptionListOldSubscription)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Subscription " + subscriptionListOldSubscription + " since its tenant field is not nullable.");
                }
            }
            for (Cart cartListOldCart : cartListOld) {
                if (!cartListNew.contains(cartListOldCart)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Cart " + cartListOldCart + " since its tenant field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Payment> attachedPaymentListNew = new ArrayList<Payment>();
            for (Payment paymentListNewPaymentToAttach : paymentListNew) {
                paymentListNewPaymentToAttach = em.getReference(paymentListNewPaymentToAttach.getClass(), paymentListNewPaymentToAttach.getPaymentPK());
                attachedPaymentListNew.add(paymentListNewPaymentToAttach);
            }
            paymentListNew = attachedPaymentListNew;
            tenant.setPaymentList(paymentListNew);
            List<Subscription> attachedSubscriptionListNew = new ArrayList<Subscription>();
            for (Subscription subscriptionListNewSubscriptionToAttach : subscriptionListNew) {
                subscriptionListNewSubscriptionToAttach = em.getReference(subscriptionListNewSubscriptionToAttach.getClass(), subscriptionListNewSubscriptionToAttach.getSubscriptionPK());
                attachedSubscriptionListNew.add(subscriptionListNewSubscriptionToAttach);
            }
            subscriptionListNew = attachedSubscriptionListNew;
            tenant.setSubscriptionList(subscriptionListNew);
            List<Cart> attachedCartListNew = new ArrayList<Cart>();
            for (Cart cartListNewCartToAttach : cartListNew) {
                cartListNewCartToAttach = em.getReference(cartListNewCartToAttach.getClass(), cartListNewCartToAttach.getCartPK());
                attachedCartListNew.add(cartListNewCartToAttach);
            }
            cartListNew = attachedCartListNew;
            tenant.setCartList(cartListNew);
            tenant = em.merge(tenant);
            for (Payment paymentListNewPayment : paymentListNew) {
                if (!paymentListOld.contains(paymentListNewPayment)) {
                    Tenant oldTenantOfPaymentListNewPayment = paymentListNewPayment.getTenant();
                    paymentListNewPayment.setTenant(tenant);
                    paymentListNewPayment = em.merge(paymentListNewPayment);
                    if (oldTenantOfPaymentListNewPayment != null && !oldTenantOfPaymentListNewPayment.equals(tenant)) {
                        oldTenantOfPaymentListNewPayment.getPaymentList().remove(paymentListNewPayment);
                        oldTenantOfPaymentListNewPayment = em.merge(oldTenantOfPaymentListNewPayment);
                    }
                }
            }
            for (Subscription subscriptionListNewSubscription : subscriptionListNew) {
                if (!subscriptionListOld.contains(subscriptionListNewSubscription)) {
                    Tenant oldTenantOfSubscriptionListNewSubscription = subscriptionListNewSubscription.getTenant();
                    subscriptionListNewSubscription.setTenant(tenant);
                    subscriptionListNewSubscription = em.merge(subscriptionListNewSubscription);
                    if (oldTenantOfSubscriptionListNewSubscription != null && !oldTenantOfSubscriptionListNewSubscription.equals(tenant)) {
                        oldTenantOfSubscriptionListNewSubscription.getSubscriptionList().remove(subscriptionListNewSubscription);
                        oldTenantOfSubscriptionListNewSubscription = em.merge(oldTenantOfSubscriptionListNewSubscription);
                    }
                }
            }
            for (Cart cartListNewCart : cartListNew) {
                if (!cartListOld.contains(cartListNewCart)) {
                    Tenant oldTenantOfCartListNewCart = cartListNewCart.getTenant();
                    cartListNewCart.setTenant(tenant);
                    cartListNewCart = em.merge(cartListNewCart);
                    if (oldTenantOfCartListNewCart != null && !oldTenantOfCartListNewCart.equals(tenant)) {
                        oldTenantOfCartListNewCart.getCartList().remove(cartListNewCart);
                        oldTenantOfCartListNewCart = em.merge(oldTenantOfCartListNewCart);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tenant.getId();
                if (findTenant(id) == null) {
                    throw new NonexistentEntityException("The tenant with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tenant tenant;
            try {
                tenant = em.getReference(Tenant.class, id);
                tenant.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tenant with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Payment> paymentListOrphanCheck = tenant.getPaymentList();
            for (Payment paymentListOrphanCheckPayment : paymentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Tenant (" + tenant + ") cannot be destroyed since the Payment " + paymentListOrphanCheckPayment + " in its paymentList field has a non-nullable tenant field.");
            }
            List<Subscription> subscriptionListOrphanCheck = tenant.getSubscriptionList();
            for (Subscription subscriptionListOrphanCheckSubscription : subscriptionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Tenant (" + tenant + ") cannot be destroyed since the Subscription " + subscriptionListOrphanCheckSubscription + " in its subscriptionList field has a non-nullable tenant field.");
            }
            List<Cart> cartListOrphanCheck = tenant.getCartList();
            for (Cart cartListOrphanCheckCart : cartListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Tenant (" + tenant + ") cannot be destroyed since the Cart " + cartListOrphanCheckCart + " in its cartList field has a non-nullable tenant field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(tenant);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tenant> findTenantEntities() {
        return findTenantEntities(true, -1, -1);
    }

    public List<Tenant> findTenantEntities(int maxResults, int firstResult) {
        return findTenantEntities(false, maxResults, firstResult);
    }

    private List<Tenant> findTenantEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tenant.class));
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

    public Tenant findTenant(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tenant.class, id);
        } finally {
            em.close();
        }
    }

    public int getTenantCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tenant> rt = cq.from(Tenant.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
