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
import org.hedwig.tenant.JPA.UserlogintraceJpaController;
import org.hedwig.tenant.entities.User;
import org.hedwig.tenant.entities.Userlogintrace;

/**
 *
 * @author dgrfiv
 */
public class LoginTraceDAO extends UserlogintraceJpaController{
    
    public LoginTraceDAO(EntityManagerFactory emf) {
        super(emf);
    }

    public List<Userlogintrace> findLoggedinUserList(User user) {
        EntityManager em = getEntityManager();
        TypedQuery<Userlogintrace> query = em.createNamedQuery("Userlogintrace.findUsersGroupbyLogintime", Userlogintrace.class);
        query.setParameter("tenantId", user.getUserPK().getTenantId());
        query.setParameter("productId", user.getUserPK().getProductId());
        query.setParameter("userId", user.getUserPK().getId());
        
        List<Userlogintrace> userlogintraces = query.getResultList();
        return userlogintraces;
    }
    
}
