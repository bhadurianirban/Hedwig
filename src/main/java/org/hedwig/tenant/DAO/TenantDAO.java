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
import org.hedwig.tenant.JPA.TenantJpaController;
import org.hedwig.tenant.entities.Tenant;

/**
 *
 * @author dgrf-iv
 */
public class TenantDAO extends TenantJpaController{
    
    public TenantDAO(EntityManagerFactory emf) {
        super(emf);
    }

    public int getMaxTenantId() {
        EntityManager em = getEntityManager();
        int m;
        try {
            TypedQuery<Integer> query = em.createNamedQuery("Tenant.findMaxTenantId", Integer.class);
            m = query.getSingleResult();
            return m;
        }
        catch(NullPointerException e) {
            return 0;
        }
    }

    public List<Tenant> getTenant(String email, String password) {
        EntityManager em = getEntityManager();
        TypedQuery<Tenant> query = em.createNamedQuery("Tenant.findByTenantEmailAndPassword", Tenant.class);
        query.setParameter("email", email);
        query.setParameter("password", password);
        List<Tenant> tenants = query.getResultList();
        return tenants;
    }

    public List<Tenant> findTenantByEmail(String email) {
        EntityManager em = getEntityManager();
        TypedQuery<Tenant> query = em.createNamedQuery("Tenant.findByTenantEmail", Tenant.class);
        query.setParameter("email", email);
        List<Tenant> tenants = query.getResultList();
        return tenants;
    }
    
}
