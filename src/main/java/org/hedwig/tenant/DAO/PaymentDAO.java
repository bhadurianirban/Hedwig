/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.DAO;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import org.hedwig.tenant.JPA.PaymentJpaController;

/**
 *
 * @author dgrf-iv
 */
public class PaymentDAO extends PaymentJpaController{
    
    public PaymentDAO(EntityManagerFactory emf) {
        super(emf);
    }

    public int getMaxPaymentId() {
        EntityManager em = getEntityManager();
        int m;
        try {
            TypedQuery<Integer> query = em.createNamedQuery("Payment.findMaxPaymentId", Integer.class);
            m = query.getSingleResult();
            return m;
        }
        catch(NullPointerException e) {
            return 0;
        }
    }
    
}
