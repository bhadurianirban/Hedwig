/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.Package;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.hedwig.cloud.response.HedwigResponseCode;
import org.hedwig.cloud.response.HedwigResponseMessage;

import org.hedwig.tenant.entities.Prodpackage;
import org.hedwig.tenant.entities.Subscription;
import org.hedwig.tenant.login.LoginController;
import org.hedwig.tenant.service.MasterDataService;

/**
 *
 * @author dgrf-iv
 */
@Named(value = "packageList")
@ViewScoped
public class PackageList implements Serializable {

    private int productId;
    private String productName;
    private List<Prodpackage> prodpackages;
    private Subscription subscription;
    private boolean subscriptionExists;
    private Prodpackage selectedPkg;
    private int tenantId;

    @Inject
    LoginController loginController;

    public PackageList() {
    }

    public void fillPackageValues() {
        MasterDataService mds = new MasterDataService();
        if (loginController.getLoginType() == 2) {//admin login
            prodpackages = mds.getAllPackageList(productId);
            productName = mds.getProductName(productId);
        } else {
            prodpackages = mds.getActivePackageList(productId);
            subscription = mds.getSubscription(loginController.getTenant().getId(), productId);
            Date now = new java.util.Date();
            Timestamp current = new java.sql.Timestamp(now.getTime());
            subscriptionExists = subscription != null && current.before(subscription.getEndDate());
            productName = mds.getProductName(productId);

        }

    }

    public void fillPackageValuesAdmin() {
        MasterDataService mds = new MasterDataService();

    }

    public String goToEditPackage() {
        return "PackageEdit";
    }
    
    public String goToParamList() {
        return "ParamList";
    }

    public String goToAddToCart(Prodpackage packageBean) {
        tenantId = loginController.getTenant().getId();
   
        if (tenantId != 0) {
            //Checking whether another package of the same product is in the cart or not
            MasterDataService mds = new MasterDataService();
            int noOfPackage = mds.getExistingPackage(tenantId, productId);
            if (noOfPackage == 0) {
                int response = mds.insertIntoCart(tenantId, productId, packageBean.getProdpackagePK().getId());
                HedwigResponseMessage responseMessage = new HedwigResponseMessage();
                FacesMessage message;
                String redirectUrl;
                if ( response!= HedwigResponseCode.SUCCESS) {
                    message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(response));
                    redirectUrl = "PackageList";
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Success", responseMessage.getResponseMessage(response));
                    redirectUrl = "CartList";
                }
                FacesContext f = FacesContext.getCurrentInstance();
                f.getExternalContext().getFlash().setKeepMessages(true);
                f.addMessage(null, message);
                return redirectUrl;

            } else {
                FacesMessage message;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error", "Another package of this product is already in your cart");
                FacesContext fc = FacesContext.getCurrentInstance();
                fc.getExternalContext().getFlash().setKeepMessages(true);
                fc.addMessage(null, message);
                String redirectUrl = "PackageList";
                return redirectUrl;
            }
        } else {
            String redirectUrl = "TenantLogin";
            return redirectUrl;
        }
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<Prodpackage> getProdpackages() {
        return prodpackages;
    }

    public void setProdpackages(List<Prodpackage> prodpackages) {
        this.prodpackages = prodpackages;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public boolean isSubscriptionExists() {
        return subscriptionExists;
    }

    public void setSubscriptionExists(boolean subscriptionExists) {
        this.subscriptionExists = subscriptionExists;
    }

    public Prodpackage getSelectedPkg() {
        return selectedPkg;
    }

    public void setSelectedPkg(Prodpackage selectedPkg) {
        this.selectedPkg = selectedPkg;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }
    
}
