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
import org.hedwig.tenant.entities.Packageparam;
import org.hedwig.tenant.entities.PackageparamPK;
import org.hedwig.tenant.entities.Prodpackage;

/**
 *
 * @author dgrfiv
 */
public class PackageparamJpaController implements Serializable {

    public PackageparamJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Packageparam packageparam) throws PreexistingEntityException, Exception {
        if (packageparam.getPackageparamPK() == null) {
            packageparam.setPackageparamPK(new PackageparamPK());
        }
        packageparam.getPackageparamPK().setProdpackageId(packageparam.getProdpackage().getProdpackagePK().getId());
        packageparam.getPackageparamPK().setProductId(packageparam.getProdpackage().getProdpackagePK().getProductId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Prodpackage prodpackage = packageparam.getProdpackage();
            if (prodpackage != null) {
                prodpackage = em.getReference(prodpackage.getClass(), prodpackage.getProdpackagePK());
                packageparam.setProdpackage(prodpackage);
            }
            em.persist(packageparam);
            if (prodpackage != null) {
                prodpackage.getPackageparamList().add(packageparam);
                prodpackage = em.merge(prodpackage);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPackageparam(packageparam.getPackageparamPK()) != null) {
                throw new PreexistingEntityException("Packageparam " + packageparam + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Packageparam packageparam) throws NonexistentEntityException, Exception {
        packageparam.getPackageparamPK().setProdpackageId(packageparam.getProdpackage().getProdpackagePK().getId());
        packageparam.getPackageparamPK().setProductId(packageparam.getProdpackage().getProdpackagePK().getProductId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Packageparam persistentPackageparam = em.find(Packageparam.class, packageparam.getPackageparamPK());
            Prodpackage prodpackageOld = persistentPackageparam.getProdpackage();
            Prodpackage prodpackageNew = packageparam.getProdpackage();
            if (prodpackageNew != null) {
                prodpackageNew = em.getReference(prodpackageNew.getClass(), prodpackageNew.getProdpackagePK());
                packageparam.setProdpackage(prodpackageNew);
            }
            packageparam = em.merge(packageparam);
            if (prodpackageOld != null && !prodpackageOld.equals(prodpackageNew)) {
                prodpackageOld.getPackageparamList().remove(packageparam);
                prodpackageOld = em.merge(prodpackageOld);
            }
            if (prodpackageNew != null && !prodpackageNew.equals(prodpackageOld)) {
                prodpackageNew.getPackageparamList().add(packageparam);
                prodpackageNew = em.merge(prodpackageNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                PackageparamPK id = packageparam.getPackageparamPK();
                if (findPackageparam(id) == null) {
                    throw new NonexistentEntityException("The packageparam with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(PackageparamPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Packageparam packageparam;
            try {
                packageparam = em.getReference(Packageparam.class, id);
                packageparam.getPackageparamPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The packageparam with id " + id + " no longer exists.", enfe);
            }
            Prodpackage prodpackage = packageparam.getProdpackage();
            if (prodpackage != null) {
                prodpackage.getPackageparamList().remove(packageparam);
                prodpackage = em.merge(prodpackage);
            }
            em.remove(packageparam);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Packageparam> findPackageparamEntities() {
        return findPackageparamEntities(true, -1, -1);
    }

    public List<Packageparam> findPackageparamEntities(int maxResults, int firstResult) {
        return findPackageparamEntities(false, maxResults, firstResult);
    }

    private List<Packageparam> findPackageparamEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Packageparam.class));
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

    public Packageparam findPackageparam(PackageparamPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Packageparam.class, id);
        } finally {
            em.close();
        }
    }

    public int getPackageparamCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Packageparam> rt = cq.from(Packageparam.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
