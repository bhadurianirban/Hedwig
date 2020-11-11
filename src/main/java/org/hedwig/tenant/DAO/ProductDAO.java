/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.DAO;

import javax.persistence.EntityManagerFactory;
import org.hedwig.tenant.JPA.ProductJpaController;

/**
 *
 * @author dgrf-iv
 */
public class ProductDAO extends ProductJpaController{
    
    public ProductDAO(EntityManagerFactory emf) {
        super(emf);
    }
    
}
