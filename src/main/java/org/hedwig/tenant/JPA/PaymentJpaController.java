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
import org.hedwig.tenant.entities.Payment;
import org.hedwig.tenant.entities.PaymentPK;
import org.hedwig.tenant.entities.Prodpackage;
import org.hedwig.tenant.entities.Tenant;

/**
 *
 * @author dgrfiv
 */
public class PaymentJpaController implements Serializable {

    public PaymentJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Payment payment) throws PreexistingEntityException, Exception {
        if (payment.getPaymentPK() == null) {
            payment.setPaymentPK(new PaymentPK());
        }
        payment.getPaymentPK().setTenantId(payment.getTenant().getId());
        payment.getPaymentPK().setProductId(payment.getProdpackage().getProdpackagePK().getProductId());
        payment.getPaymentPK().setProdpackageId(payment.getProdpackage().getProdpackagePK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Prodpackage prodpackage = payment.getProdpackage();
            if (prodpackage != null) {
                prodpackage = em.getReference(prodpackage.getClass(), prodpackage.getProdpackagePK());
                payment.setProdpackage(prodpackage);
            }
            Tenant tenant = payment.getTenant();
            if (tenant != null) {
                tenant = em.getReference(tenant.getClass(), tenant.getId());
                payment.setTenant(tenant);
            }
            em.persist(payment);
            if (prodpackage != null) {
                prodpackage.getPaymentList().add(payment);
                prodpackage = em.merge(prodpackage);
            }
            if (tenant != null) {
                tenant.getPaymentList().add(payment);
                tenant = em.merge(tenant);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPayment(payment.getPaymentPK()) != null) {
                throw new PreexistingEntityException("Payment " + payment + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Payment payment) throws NonexistentEntityException, Exception {
        payment.getPaymentPK().setTenantId(payment.getTenant().getId());
        payment.getPaymentPK().setProductId(payment.getProdpackage().getProdpackagePK().getProductId());
        payment.getPaymentPK().setProdpackageId(payment.getProdpackage().getProdpackagePK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Payment persistentPayment = em.find(Payment.class, payment.getPaymentPK());
            Prodpackage prodpackageOld = persistentPayment.getProdpackage();
            Prodpackage prodpackageNew = payment.getProdpackage();
            Tenant tenantOld = persistentPayment.getTenant();
            Tenant tenantNew = payment.getTenant();
            if (prodpackageNew != null) {
                prodpackageNew = em.getReference(prodpackageNew.getClass(), prodpackageNew.getProdpackagePK());
                payment.setProdpackage(prodpackageNew);
            }
            if (tenantNew != null) {
                tenantNew = em.getReference(tenantNew.getClass(), tenantNew.getId());
                payment.setTenant(tenantNew);
            }
            payment = em.merge(payment);
            if (prodpackageOld != null && !prodpackageOld.equals(prodpackageNew)) {
                prodpackageOld.getPaymentList().remove(payment);
                prodpackageOld = em.merge(prodpackageOld);
            }
            if (prodpackageNew != null && !prodpackageNew.equals(prodpackageOld)) {
                prodpackageNew.getPaymentList().add(payment);
                prodpackageNew = em.merge(prodpackageNew);
            }
            if (tenantOld != null && !tenantOld.equals(tenantNew)) {
                tenantOld.getPaymentList().remove(payment);
                tenantOld = em.merge(tenantOld);
            }
            if (tenantNew != null && !tenantNew.equals(tenantOld)) {
                tenantNew.getPaymentList().add(payment);
                tenantNew = em.merge(tenantNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                PaymentPK id = payment.getPaymentPK();
                if (findPayment(id) == null) {
                    throw new NonexistentEntityException("The payment with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(PaymentPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Payment payment;
            try {
                payment = em.getReference(Payment.class, id);
                payment.getPaymentPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The payment with id " + id + " no longer exists.", enfe);
            }
            Prodpackage prodpackage = payment.getProdpackage();
            if (prodpackage != null) {
                prodpackage.getPaymentList().remove(payment);
                prodpackage = em.merge(prodpackage);
            }
            Tenant tenant = payment.getTenant();
            if (tenant != null) {
                tenant.getPaymentList().remove(payment);
                tenant = em.merge(tenant);
            }
            em.remove(payment);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Payment> findPaymentEntities() {
        return findPaymentEntities(true, -1, -1);
    }

    public List<Payment> findPaymentEntities(int maxResults, int firstResult) {
        return findPaymentEntities(false, maxResults, firstResult);
    }

    private List<Payment> findPaymentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Payment.class));
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

    public Payment findPayment(PaymentPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Payment.class, id);
        } finally {
            em.close();
        }
    }

    public int getPaymentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Payment> rt = cq.from(Payment.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
