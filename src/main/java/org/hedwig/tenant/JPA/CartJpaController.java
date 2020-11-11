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
import org.hedwig.tenant.entities.Cart;
import org.hedwig.tenant.entities.CartPK;
import org.hedwig.tenant.entities.Prodpackage;
import org.hedwig.tenant.entities.Tenant;

/**
 *
 * @author dgrfiv
 */
public class CartJpaController implements Serializable {

    public CartJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cart cart) throws PreexistingEntityException, Exception {
        if (cart.getCartPK() == null) {
            cart.setCartPK(new CartPK());
        }
        cart.getCartPK().setTenantId(cart.getTenant().getId());
        cart.getCartPK().setProductId(cart.getProdpackage().getProdpackagePK().getProductId());
        cart.getCartPK().setProdpackageId(cart.getProdpackage().getProdpackagePK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Prodpackage prodpackage = cart.getProdpackage();
            if (prodpackage != null) {
                prodpackage = em.getReference(prodpackage.getClass(), prodpackage.getProdpackagePK());
                cart.setProdpackage(prodpackage);
            }
            Tenant tenant = cart.getTenant();
            if (tenant != null) {
                tenant = em.getReference(tenant.getClass(), tenant.getId());
                cart.setTenant(tenant);
            }
            em.persist(cart);
            if (prodpackage != null) {
                prodpackage.getCartList().add(cart);
                prodpackage = em.merge(prodpackage);
            }
            if (tenant != null) {
                tenant.getCartList().add(cart);
                tenant = em.merge(tenant);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCart(cart.getCartPK()) != null) {
                throw new PreexistingEntityException("Cart " + cart + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cart cart) throws NonexistentEntityException, Exception {
        cart.getCartPK().setTenantId(cart.getTenant().getId());
        cart.getCartPK().setProductId(cart.getProdpackage().getProdpackagePK().getProductId());
        cart.getCartPK().setProdpackageId(cart.getProdpackage().getProdpackagePK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cart persistentCart = em.find(Cart.class, cart.getCartPK());
            Prodpackage prodpackageOld = persistentCart.getProdpackage();
            Prodpackage prodpackageNew = cart.getProdpackage();
            Tenant tenantOld = persistentCart.getTenant();
            Tenant tenantNew = cart.getTenant();
            if (prodpackageNew != null) {
                prodpackageNew = em.getReference(prodpackageNew.getClass(), prodpackageNew.getProdpackagePK());
                cart.setProdpackage(prodpackageNew);
            }
            if (tenantNew != null) {
                tenantNew = em.getReference(tenantNew.getClass(), tenantNew.getId());
                cart.setTenant(tenantNew);
            }
            cart = em.merge(cart);
            if (prodpackageOld != null && !prodpackageOld.equals(prodpackageNew)) {
                prodpackageOld.getCartList().remove(cart);
                prodpackageOld = em.merge(prodpackageOld);
            }
            if (prodpackageNew != null && !prodpackageNew.equals(prodpackageOld)) {
                prodpackageNew.getCartList().add(cart);
                prodpackageNew = em.merge(prodpackageNew);
            }
            if (tenantOld != null && !tenantOld.equals(tenantNew)) {
                tenantOld.getCartList().remove(cart);
                tenantOld = em.merge(tenantOld);
            }
            if (tenantNew != null && !tenantNew.equals(tenantOld)) {
                tenantNew.getCartList().add(cart);
                tenantNew = em.merge(tenantNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                CartPK id = cart.getCartPK();
                if (findCart(id) == null) {
                    throw new NonexistentEntityException("The cart with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(CartPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cart cart;
            try {
                cart = em.getReference(Cart.class, id);
                cart.getCartPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cart with id " + id + " no longer exists.", enfe);
            }
            Prodpackage prodpackage = cart.getProdpackage();
            if (prodpackage != null) {
                prodpackage.getCartList().remove(cart);
                prodpackage = em.merge(prodpackage);
            }
            Tenant tenant = cart.getTenant();
            if (tenant != null) {
                tenant.getCartList().remove(cart);
                tenant = em.merge(tenant);
            }
            em.remove(cart);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cart> findCartEntities() {
        return findCartEntities(true, -1, -1);
    }

    public List<Cart> findCartEntities(int maxResults, int firstResult) {
        return findCartEntities(false, maxResults, firstResult);
    }

    private List<Cart> findCartEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cart.class));
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

    public Cart findCart(CartPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cart.class, id);
        } finally {
            em.close();
        }
    }

    public int getCartCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cart> rt = cq.from(Cart.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
