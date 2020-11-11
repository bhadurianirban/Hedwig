/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.User;

import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.hedwig.cloud.response.HedwigResponseCode;
import org.hedwig.cloud.response.HedwigResponseMessage;
import org.hedwig.tenant.entities.User;
import org.hedwig.tenant.service.MasterDataService;

/**
 *
 * @author dgrf-iv
 */
@Named(value = "userActivation")
@ViewScoped
public class UserActivation implements Serializable {

    private int tenantId;
    private int productId;
    private String tenantName;
    private String productName;
    private List<User> users;
    private List<User> selectedUsers;

    public UserActivation() {
    }

    public void fillInactiveUserValues() {
        MasterDataService mds = new MasterDataService();
        users = mds.getInactiveUserList(tenantId, productId);
        productName = mds.getProductName(productId);
        tenantName = mds.getTenantName(tenantId);
    }

    public void fillIninactiveUserValues() {
        MasterDataService mds = new MasterDataService();
        users = mds.getActiveUserList(tenantId, productId);
        productName = mds.getProductName(productId);
        tenantName = mds.getTenantName(tenantId);
    }

    public String makeUsersActive() {
        MasterDataService mds = new MasterDataService();
        int response = mds.activeUsers(selectedUsers);
        HedwigResponseMessage responseMessage = new HedwigResponseMessage();
        FacesMessage message;
        String redirectUrl;
        if (response != HedwigResponseCode.SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(response));
            redirectUrl = "UserActivation";
        }else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(response));
            redirectUrl = "UserActivation";
        }
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);
        return redirectUrl;
    }

    public String makeUsersIactive() {
        MasterDataService mds = new MasterDataService();
        int response = mds.deactiveUsers(selectedUsers);
        HedwigResponseMessage responseMessage = new HedwigResponseMessage();
        FacesMessage message;

        if (response != HedwigResponseCode.SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Error", responseMessage.getResponseMessage(response));
        }else{
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(response));
        }
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);
        String redirectUrl = "UserDeactivation";
        return redirectUrl;
    }

    public int getTenantId() {
        return tenantId;
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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<User> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(List<User> selectedUsers) {
        this.selectedUsers = selectedUsers;
    }

}
