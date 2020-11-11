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
import org.hedwig.tenant.entities.Product;
import org.hedwig.tenant.entities.Packageparam;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.hedwig.tenant.JPA.exceptions.IllegalOrphanException;
import org.hedwig.tenant.JPA.exceptions.NonexistentEntityException;
import org.hedwig.tenant.JPA.exceptions.PreexistingEntityException;
import org.hedwig.tenant.entities.Payment;
import org.hedwig.tenant.entities.Cart;
import org.hedwig.tenant.entities.Prodpackage;
import org.hedwig.tenant.entities.ProdpackagePK;

/**
 *
 * @author dgrfiv
 */
public class ProdpackageJpaController implements Serializable {

    public ProdpackageJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Prodpackage prodpackage) throws PreexistingEntityException, Exception {
        if (prodpackage.getProdpackagePK() == null) {
            prodpackage.setProdpackagePK(new ProdpackagePK());
        }
        if (prodpackage.getPackageparamList() == null) {
            prodpackage.setPackageparamList(new ArrayList<Packageparam>());
        }
        if (prodpackage.getPaymentList() == null) {
            prodpackage.setPaymentList(new ArrayList<Payment>());
        }
        if (prodpackage.getCartList() == null) {
            prodpackage.setCartList(new ArrayList<Cart>());
        }
        prodpackage.getProdpackagePK().setProductId(prodpackage.getProduct().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Product product = prodpackage.getProduct();
            if (product != null) {
                product = em.getReference(product.getClass(), product.getId());
                prodpackage.setProduct(product);
            }
            List<Packageparam> attachedPackageparamList = new ArrayList<Packageparam>();
            for (Packageparam packageparamListPackageparamToAttach : prodpackage.getPackageparamList()) {
                packageparamListPackageparamToAttach = em.getReference(packageparamListPackageparamToAttach.getClass(), packageparamListPackageparamToAttach.getPackageparamPK());
                attachedPackageparamList.add(packageparamListPackageparamToAttach);
            }
            prodpackage.setPackageparamList(attachedPackageparamList);
            List<Payment> attachedPaymentList = new ArrayList<Payment>();
            for (Payment paymentListPaymentToAttach : prodpackage.getPaymentList()) {
                paymentListPaymentToAttach = em.getReference(paymentListPaymentToAttach.getClass(), paymentListPaymentToAttach.getPaymentPK());
                attachedPaymentList.add(paymentListPaymentToAttach);
            }
            prodpackage.setPaymentList(attachedPaymentList);
            List<Cart> attachedCartList = new ArrayList<Cart>();
            for (Cart cartListCartToAttach : prodpackage.getCartList()) {
                cartListCartToAttach = em.getReference(cartListCartToAttach.getClass(), cartListCartToAttach.getCartPK());
                attachedCartList.add(cartListCartToAttach);
            }
            prodpackage.setCartList(attachedCartList);
            em.persist(prodpackage);
            if (product != null) {
                product.getProdpackageList().add(prodpackage);
                product = em.merge(product);
            }
            for (Packageparam packageparamListPackageparam : prodpackage.getPackageparamList()) {
                Prodpackage oldProdpackageOfPackageparamListPackageparam = packageparamListPackageparam.getProdpackage();
                packageparamListPackageparam.setProdpackage(prodpackage);
                packageparamListPackageparam = em.merge(packageparamListPackageparam);
                if (oldProdpackageOfPackageparamListPackageparam != null) {
                    oldProdpackageOfPackageparamListPackageparam.getPackageparamList().remove(packageparamListPackageparam);
                    oldProdpackageOfPackageparamListPackageparam = em.merge(oldProdpackageOfPackageparamListPackageparam);
                }
            }
            for (Payment paymentListPayment : prodpackage.getPaymentList()) {
                Prodpackage oldProdpackageOfPaymentListPayment = paymentListPayment.getProdpackage();
                paymentListPayment.setProdpackage(prodpackage);
                paymentListPayment = em.merge(paymentListPayment);
                if (oldProdpackageOfPaymentListPayment != null) {
                    oldProdpackageOfPaymentListPayment.getPaymentList().remove(paymentListPayment);
                    oldProdpackageOfPaymentListPayment = em.merge(oldProdpackageOfPaymentListPayment);
                }
            }
            for (Cart cartListCart : prodpackage.getCartList()) {
                Prodpackage oldProdpackageOfCartListCart = cartListCart.getProdpackage();
                cartListCart.setProdpackage(prodpackage);
                cartListCart = em.merge(cartListCart);
                if (oldProdpackageOfCartListCart != null) {
                    oldProdpackageOfCartListCart.getCartList().remove(cartListCart);
                    oldProdpackageOfCartListCart = em.merge(oldProdpackageOfCartListCart);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProdpackage(prodpackage.getProdpackagePK()) != null) {
                throw new PreexistingEntityException("Prodpackage " + prodpackage + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Prodpackage prodpackage) throws IllegalOrphanException, NonexistentEntityException, Exception {
        prodpackage.getProdpackagePK().setProductId(prodpackage.getProduct().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Prodpackage persistentProdpackage = em.find(Prodpackage.class, prodpackage.getProdpackagePK());
            Product productOld = persistentProdpackage.getProduct();
            Product productNew = prodpackage.getProduct();
            List<Packageparam> packageparamListOld = persistentProdpackage.getPackageparamList();
            List<Packageparam> packageparamListNew = prodpackage.getPackageparamList();
            List<Payment> paymentListOld = persistentProdpackage.getPaymentList();
            List<Payment> paymentListNew = prodpackage.getPaymentList();
            List<Cart> cartListOld = persistentProdpackage.getCartList();
            List<Cart> cartListNew = prodpackage.getCartList();
            List<String> illegalOrphanMessages = null;
            for (Packageparam packageparamListOldPackageparam : packageparamListOld) {
                if (!packageparamListNew.contains(packageparamListOldPackageparam)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Packageparam " + packageparamListOldPackageparam + " since its prodpackage field is not nullable.");
                }
            }
            for (Payment paymentListOldPayment : paymentListOld) {
                if (!paymentListNew.contains(paymentListOldPayment)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Payment " + paymentListOldPayment + " since its prodpackage field is not nullable.");
                }
            }
            for (Cart cartListOldCart : cartListOld) {
                if (!cartListNew.contains(cartListOldCart)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Cart " + cartListOldCart + " since its prodpackage field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (productNew != null) {
                productNew = em.getReference(productNew.getClass(), productNew.getId());
                prodpackage.setProduct(productNew);
            }
            List<Packageparam> attachedPackageparamListNew = new ArrayList<Packageparam>();
            for (Packageparam packageparamListNewPackageparamToAttach : packageparamListNew) {
                packageparamListNewPackageparamToAttach = em.getReference(packageparamListNewPackageparamToAttach.getClass(), packageparamListNewPackageparamToAttach.getPackageparamPK());
                attachedPackageparamListNew.add(packageparamListNewPackageparamToAttach);
            }
            packageparamListNew = attachedPackageparamListNew;
            prodpackage.setPackageparamList(packageparamListNew);
            List<Payment> attachedPaymentListNew = new ArrayList<Payment>();
            for (Payment paymentListNewPaymentToAttach : paymentListNew) {
                paymentListNewPaymentToAttach = em.getReference(paymentListNewPaymentToAttach.getClass(), paymentListNewPaymentToAttach.getPaymentPK());
                attachedPaymentListNew.add(paymentListNewPaymentToAttach);
            }
            paymentListNew = attachedPaymentListNew;
            prodpackage.setPaymentList(paymentListNew);
            List<Cart> attachedCartListNew = new ArrayList<Cart>();
            for (Cart cartListNewCartToAttach : cartListNew) {
                cartListNewCartToAttach = em.getReference(cartListNewCartToAttach.getClass(), cartListNewCartToAttach.getCartPK());
                attachedCartListNew.add(cartListNewCartToAttach);
            }
            cartListNew = attachedCartListNew;
            prodpackage.setCartList(cartListNew);
            prodpackage = em.merge(prodpackage);
            if (productOld != null && !productOld.equals(productNew)) {
                productOld.getProdpackageList().remove(prodpackage);
                productOld = em.merge(productOld);
            }
            if (productNew != null && !productNew.equals(productOld)) {
                productNew.getProdpackageList().add(prodpackage);
                productNew = em.merge(productNew);
            }
            for (Packageparam packageparamListNewPackageparam : packageparamListNew) {
                if (!packageparamListOld.contains(packageparamListNewPackageparam)) {
                    Prodpackage oldProdpackageOfPackageparamListNewPackageparam = packageparamListNewPackageparam.getProdpackage();
                    packageparamListNewPackageparam.setProdpackage(prodpackage);
                    packageparamListNewPackageparam = em.merge(packageparamListNewPackageparam);
                    if (oldProdpackageOfPackageparamListNewPackageparam != null && !oldProdpackageOfPackageparamListNewPackageparam.equals(prodpackage)) {
                        oldProdpackageOfPackageparamListNewPackageparam.getPackageparamList().remove(packageparamListNewPackageparam);
                        oldProdpackageOfPackageparamListNewPackageparam = em.merge(oldProdpackageOfPackageparamListNewPackageparam);
                    }
                }
            }
            for (Payment paymentListNewPayment : paymentListNew) {
                if (!paymentListOld.contains(paymentListNewPayment)) {
                    Prodpackage oldProdpackageOfPaymentListNewPayment = paymentListNewPayment.getProdpackage();
                    paymentListNewPayment.setProdpackage(prodpackage);
                    paymentListNewPayment = em.merge(paymentListNewPayment);
                    if (oldProdpackageOfPaymentListNewPayment != null && !oldProdpackageOfPaymentListNewPayment.equals(prodpackage)) {
                        oldProdpackageOfPaymentListNewPayment.getPaymentList().remove(paymentListNewPayment);
                        oldProdpackageOfPaymentListNewPayment = em.merge(oldProdpackageOfPaymentListNewPayment);
                    }
                }
            }
            for (Cart cartListNewCart : cartListNew) {
                if (!cartListOld.contains(cartListNewCart)) {
                    Prodpackage oldProdpackageOfCartListNewCart = cartListNewCart.getProdpackage();
                    cartListNewCart.setProdpackage(prodpackage);
                    cartListNewCart = em.merge(cartListNewCart);
                    if (oldProdpackageOfCartListNewCart != null && !oldProdpackageOfCartListNewCart.equals(prodpackage)) {
                        oldProdpackageOfCartListNewCart.getCartList().remove(cartListNewCart);
                        oldProdpackageOfCartListNewCart = em.merge(oldProdpackageOfCartListNewCart);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ProdpackagePK id = prodpackage.getProdpackagePK();
                if (findProdpackage(id) == null) {
                    throw new NonexistentEntityException("The prodpackage with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(ProdpackagePK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Prodpackage prodpackage;
            try {
                prodpackage = em.getReference(Prodpackage.class, id);
                prodpackage.getProdpackagePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The prodpackage with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Packageparam> packageparamListOrphanCheck = prodpackage.getPackageparamList();
            for (Packageparam packageparamListOrphanCheckPackageparam : packageparamListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Prodpackage (" + prodpackage + ") cannot be destroyed since the Packageparam " + packageparamListOrphanCheckPackageparam + " in its packageparamList field has a non-nullable prodpackage field.");
            }
            List<Payment> paymentListOrphanCheck = prodpackage.getPaymentList();
            for (Payment paymentListOrphanCheckPayment : paymentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Prodpackage (" + prodpackage + ") cannot be destroyed since the Payment " + paymentListOrphanCheckPayment + " in its paymentList field has a non-nullable prodpackage field.");
            }
            List<Cart> cartListOrphanCheck = prodpackage.getCartList();
            for (Cart cartListOrphanCheckCart : cartListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Prodpackage (" + prodpackage + ") cannot be destroyed since the Cart " + cartListOrphanCheckCart + " in its cartList field has a non-nullable prodpackage field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Product product = prodpackage.getProduct();
            if (product != null) {
                product.getProdpackageList().remove(prodpackage);
                product = em.merge(product);
            }
            em.remove(prodpackage);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Prodpackage> findProdpackageEntities() {
        return findProdpackageEntities(true, -1, -1);
    }

    public List<Prodpackage> findProdpackageEntities(int maxResults, int firstResult) {
        return findProdpackageEntities(false, maxResults, firstResult);
    }

    private List<Prodpackage> findProdpackageEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Prodpackage.class));
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

    public Prodpackage findProdpackage(ProdpackagePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Prodpackage.class, id);
        } finally {
            em.close();
        }
    }

    public int getProdpackageCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Prodpackage> rt = cq.from(Prodpackage.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
