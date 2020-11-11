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
import org.hedwig.tenant.JPA.CartJpaController;
import org.hedwig.tenant.entities.Cart;

/**
 *
 * @author dgrf-iv
 */
public class CartDAO extends CartJpaController{
    
    public CartDAO(EntityManagerFactory emf) {
        super(emf);
    }
    
    public int getMaxCartId() {
        EntityManager em = getEntityManager();
        int m;
        try {
            TypedQuery<Integer> query = em.createNamedQuery("Cart.findMaxCartId", Integer.class);
            m = query.getSingleResult();
            return m;
        }
        catch(NullPointerException e) {
            return 0;
        }
    }

    public List<Cart> findCartList(int tenantId) {
        EntityManager em = getEntityManager();
        TypedQuery<Cart> query = em.createNamedQuery("Cart.findByTenantId", Cart.class);
        query.setParameter("tenantId", tenantId);
        List<Cart> carts = query.getResultList();
        return carts;
    }

    public List<Cart> findCart(int tenantId, int productId) {
        EntityManager em = getEntityManager();
        TypedQuery<Cart> query = em.createNamedQuery("Cart.findByTenantIdAndProductId", Cart.class);
        query.setParameter("tenantId", tenantId);
        query.setParameter("prodpackageProductId", productId);
        List<Cart> carts = query.getResultList();
        return carts;
    }
}
