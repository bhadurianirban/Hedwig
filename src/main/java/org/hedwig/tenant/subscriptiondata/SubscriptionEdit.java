/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.subscriptiondata;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.hedwig.cloud.response.HedwigResponseCode;
import org.hedwig.cloud.response.HedwigResponseMessage;
import org.hedwig.tenant.entities.Subscription;
import org.hedwig.tenant.service.MasterDataService;

/**
 *
 * @author dgrf-iv
 */
@Named(value = "subscriptionEdit")
@ViewScoped
public class SubscriptionEdit implements Serializable{

    private int tenantId;
    private int productId;
    private String productName;
    private String tenantName;
    private Subscription subscriptionEdit;
    
    public SubscriptionEdit() {
    }

    public int getTenantId() {
        return tenantId;
    }
    
    public void fillEditValues() {
        MasterDataService mds = new MasterDataService();
        subscriptionEdit = mds.getSubscription(tenantId , productId);
        productName = mds.getProductName(productId);
        tenantName = mds.getTenantName(tenantId);
        subscriptionEdit.setDbconnUrl("localhost:3306");
    }
    
    public String save(){
        MasterDataService mds = new MasterDataService();
        int response = mds.editSubscription(subscriptionEdit);
        FacesMessage message;
        String redirectUrl;
        if (response != HedwigResponseCode.SUCCESS) {
            redirectUrl = "SubscriptionEdit";
            HedwigResponseMessage responseMessage = new HedwigResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(response));
        } else {
            HedwigResponseMessage responseMessage = new HedwigResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(response));
            redirectUrl = "SubscriptionList";
            
        }
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);
        return redirectUrl;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Subscription getSubscriptionEdit() {
        return subscriptionEdit;
    }

    public void setSubscriptionEdit(Subscription subscriptionEdit) {
        this.subscriptionEdit = subscriptionEdit;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
    
}
