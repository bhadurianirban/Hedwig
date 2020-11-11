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
import org.hedwig.tenant.entities.Prodpackage;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.hedwig.tenant.JPA.exceptions.IllegalOrphanException;
import org.hedwig.tenant.JPA.exceptions.NonexistentEntityException;
import org.hedwig.tenant.JPA.exceptions.PreexistingEntityException;
import org.hedwig.tenant.entities.Product;
import org.hedwig.tenant.entities.Subscription;

/**
 *
 * @author dgrfiv
 */
public class ProductJpaController implements Serializable {

    public ProductJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Product product) throws PreexistingEntityException, Exception {
        if (product.getProdpackageList() == null) {
            product.setProdpackageList(new ArrayList<Prodpackage>());
        }
        if (product.getSubscriptionList() == null) {
            product.setSubscriptionList(new ArrayList<Subscription>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Prodpackage> attachedProdpackageList = new ArrayList<Prodpackage>();
            for (Prodpackage prodpackageListProdpackageToAttach : product.getProdpackageList()) {
                prodpackageListProdpackageToAttach = em.getReference(prodpackageListProdpackageToAttach.getClass(), prodpackageListProdpackageToAttach.getProdpackagePK());
                attachedProdpackageList.add(prodpackageListProdpackageToAttach);
            }
            product.setProdpackageList(attachedProdpackageList);
            List<Subscription> attachedSubscriptionList = new ArrayList<Subscription>();
            for (Subscription subscriptionListSubscriptionToAttach : product.getSubscriptionList()) {
                subscriptionListSubscriptionToAttach = em.getReference(subscriptionListSubscriptionToAttach.getClass(), subscriptionListSubscriptionToAttach.getSubscriptionPK());
                attachedSubscriptionList.add(subscriptionListSubscriptionToAttach);
            }
            product.setSubscriptionList(attachedSubscriptionList);
            em.persist(product);
            for (Prodpackage prodpackageListProdpackage : product.getProdpackageList()) {
                Product oldProductOfProdpackageListProdpackage = prodpackageListProdpackage.getProduct();
                prodpackageListProdpackage.setProduct(product);
                prodpackageListProdpackage = em.merge(prodpackageListProdpackage);
                if (oldProductOfProdpackageListProdpackage != null) {
                    oldProductOfProdpackageListProdpackage.getProdpackageList().remove(prodpackageListProdpackage);
                    oldProductOfProdpackageListProdpackage = em.merge(oldProductOfProdpackageListProdpackage);
                }
            }
            for (Subscription subscriptionListSubscription : product.getSubscriptionList()) {
                Product oldProductOfSubscriptionListSubscription = subscriptionListSubscription.getProduct();
                subscriptionListSubscription.setProduct(product);
                subscriptionListSubscription = em.merge(subscriptionListSubscription);
                if (oldProductOfSubscriptionListSubscription != null) {
                    oldProductOfSubscriptionListSubscription.getSubscriptionList().remove(subscriptionListSubscription);
                    oldProductOfSubscriptionListSubscription = em.merge(oldProductOfSubscriptionListSubscription);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProduct(product.getId()) != null) {
                throw new PreexistingEntityException("Product " + product + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Product product) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Product persistentProduct = em.find(Product.class, product.getId());
            List<Prodpackage> prodpackageListOld = persistentProduct.getProdpackageList();
            List<Prodpackage> prodpackageListNew = product.getProdpackageList();
            List<Subscription> subscriptionListOld = persistentProduct.getSubscriptionList();
            List<Subscription> subscriptionListNew = product.getSubscriptionList();
            List<String> illegalOrphanMessages = null;
            for (Prodpackage prodpackageListOldProdpackage : prodpackageListOld) {
                if (!prodpackageListNew.contains(prodpackageListOldProdpackage)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Prodpackage " + prodpackageListOldProdpackage + " since its product field is not nullable.");
                }
            }
            for (Subscription subscriptionListOldSubscription : subscriptionListOld) {
                if (!subscriptionListNew.contains(subscriptionListOldSubscription)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Subscription " + subscriptionListOldSubscription + " since its product field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Prodpackage> attachedProdpackageListNew = new ArrayList<Prodpackage>();
            for (Prodpackage prodpackageListNewProdpackageToAttach : prodpackageListNew) {
                prodpackageListNewProdpackageToAttach = em.getReference(prodpackageListNewProdpackageToAttach.getClass(), prodpackageListNewProdpackageToAttach.getProdpackagePK());
                attachedProdpackageListNew.add(prodpackageListNewProdpackageToAttach);
            }
            prodpackageListNew = attachedProdpackageListNew;
            product.setProdpackageList(prodpackageListNew);
            List<Subscription> attachedSubscriptionListNew = new ArrayList<Subscription>();
            for (Subscription subscriptionListNewSubscriptionToAttach : subscriptionListNew) {
                subscriptionListNewSubscriptionToAttach = em.getReference(subscriptionListNewSubscriptionToAttach.getClass(), subscriptionListNewSubscriptionToAttach.getSubscriptionPK());
                attachedSubscriptionListNew.add(subscriptionListNewSubscriptionToAttach);
            }
            subscriptionListNew = attachedSubscriptionListNew;
            product.setSubscriptionList(subscriptionListNew);
            product = em.merge(product);
            for (Prodpackage prodpackageListNewProdpackage : prodpackageListNew) {
                if (!prodpackageListOld.contains(prodpackageListNewProdpackage)) {
                    Product oldProductOfProdpackageListNewProdpackage = prodpackageListNewProdpackage.getProduct();
                    prodpackageListNewProdpackage.setProduct(product);
                    prodpackageListNewProdpackage = em.merge(prodpackageListNewProdpackage);
                    if (oldProductOfProdpackageListNewProdpackage != null && !oldProductOfProdpackageListNewProdpackage.equals(product)) {
                        oldProductOfProdpackageListNewProdpackage.getProdpackageList().remove(prodpackageListNewProdpackage);
                        oldProductOfProdpackageListNewProdpackage = em.merge(oldProductOfProdpackageListNewProdpackage);
                    }
                }
            }
            for (Subscription subscriptionListNewSubscription : subscriptionListNew) {
                if (!subscriptionListOld.contains(subscriptionListNewSubscription)) {
                    Product oldProductOfSubscriptionListNewSubscription = subscriptionListNewSubscription.getProduct();
                    subscriptionListNewSubscription.setProduct(product);
                    subscriptionListNewSubscription = em.merge(subscriptionListNewSubscription);
                    if (oldProductOfSubscriptionListNewSubscription != null && !oldProductOfSubscriptionListNewSubscription.equals(product)) {
                        oldProductOfSubscriptionListNewSubscription.getSubscriptionList().remove(subscriptionListNewSubscription);
                        oldProductOfSubscriptionListNewSubscription = em.merge(oldProductOfSubscriptionListNewSubscription);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = product.getId();
                if (findProduct(id) == null) {
                    throw new NonexistentEntityException("The product with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Product product;
            try {
                product = em.getReference(Product.class, id);
                product.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The product with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Prodpackage> prodpackageListOrphanCheck = product.getProdpackageList();
            for (Prodpackage prodpackageListOrphanCheckProdpackage : prodpackageListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Product (" + product + ") cannot be destroyed since the Prodpackage " + prodpackageListOrphanCheckProdpackage + " in its prodpackageList field has a non-nullable product field.");
            }
            List<Subscription> subscriptionListOrphanCheck = product.getSubscriptionList();
            for (Subscription subscriptionListOrphanCheckSubscription : subscriptionListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Product (" + product + ") cannot be destroyed since the Subscription " + subscriptionListOrphanCheckSubscription + " in its subscriptionList field has a non-nullable product field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(product);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Product> findProductEntities() {
        return findProductEntities(true, -1, -1);
    }

    public List<Product> findProductEntities(int maxResults, int firstResult) {
        return findProductEntities(false, maxResults, firstResult);
    }

    private List<Product> findProductEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Product.class));
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

    public Product findProduct(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Product.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Product> rt = cq.from(Product.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
