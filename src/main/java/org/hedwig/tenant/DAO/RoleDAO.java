/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.DAO;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import org.hedwig.tenant.JPA.RoleJpaController;

/**
 *
 * @author dgrf-iv
 */
public class RoleDAO extends RoleJpaController {

    public RoleDAO(EntityManagerFactory emf) {
        super(emf);
    }

    public int getMaxRoleId() {
        EntityManager em = getEntityManager();
        int m;
        try {
            TypedQuery<Integer> query = em.createNamedQuery("Role.findMaxRoleId", Integer.class);
            m = query.getSingleResult();
            return m;
        }
        catch(NullPointerException e) {
            return 0;
        }
    }

}
