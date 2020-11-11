/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.DAO;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import org.hedwig.tenant.JPA.SubscriptionJpaController;
import org.hedwig.tenant.entities.Subscription;

/**
 *
 * @author dgrf-iv
 */
public class SubscriptionDAO extends SubscriptionJpaController{
    
    public SubscriptionDAO(EntityManagerFactory emf) {
        super(emf);
    }

    public List<Subscription> findSubscriptionListByTenantId(int tenantId) {
        EntityManager em = getEntityManager();
        TypedQuery<Subscription> query = em.createNamedQuery("Subscription.findByTenantId", Subscription.class);
        query.setParameter("tenantId", tenantId);
        List<Subscription> subscriptions = query.getResultList();
        return subscriptions;
    }
    
}
