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
import org.hedwig.tenant.JPA.UserJpaController;
import org.hedwig.tenant.entities.User;

/**
 *
 * @author dgrf-iv
 */
public class UserDAO extends UserJpaController{
    
    public UserDAO(EntityManagerFactory emf) {
        super(emf);
    }

    public List<User> findUser(int tenantId, int productId, String userName) {
        EntityManager em = getEntityManager();
        TypedQuery<User> query = em.createNamedQuery("User.findByTenantIdProdIdUserId", User.class);
        query.setParameter("tenantId", tenantId);
        query.setParameter("productId", productId);
        query.setParameter("userId", userName);
        
        List<User> users = query.getResultList();
        return users;
    }
    
}
