/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.Cart;

import java.io.Serializable;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.hedwig.tenant.entities.Prodpackage;
import org.hedwig.tenant.service.MasterDataService;

/**
 *
 * @author dgrf-iv
 */
@Named(value = "cartView")
@ViewScoped
public class CartView implements Serializable{

    private int productId;
    private int packageId;
    private int tenantId;
    private String productName;
    private Prodpackage selectedPackage;
    
    public CartView() {
    }
    
    public void fillCartValue() {
        MasterDataService mds = new MasterDataService();
        selectedPackage = mds.getPackageValues(productId, packageId);
        productName = mds.getProductName(productId);
    }
    
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public Prodpackage getSelectedPackage() {
        return selectedPackage;
    }

    public void setSelectedPackage(Prodpackage selectedPackage) {
        this.selectedPackage = selectedPackage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }
    
}
