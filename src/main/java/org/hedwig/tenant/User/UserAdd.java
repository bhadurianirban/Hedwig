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
import org.hedwig.tenant.entities.Packageparam;
import org.hedwig.tenant.entities.Subscription;
import org.hedwig.tenant.entities.User;
import org.hedwig.tenant.entities.UserPK;
import org.hedwig.tenant.service.MasterDataService;

/**
 *
 * @author dgrf-iv
 */
@Named(value = "userAdd")
@ViewScoped
public class UserAdd implements Serializable {

    private int roleId;
    private int tenantId;
    private int productId;
    private int noOfUsers;
    private String userId;
    private String prodName;
    private String roleName;
    private User user;

    public UserAdd() {
        user = new User();
    }

    public void fillAddScreen() {
        MasterDataService mds = new MasterDataService();
        prodName = mds.getProductName(productId);
        roleName = mds.getRoleName(roleId, tenantId, productId);
        List<User> users = mds.getUserListByTenantIdProductId(tenantId, productId);
        noOfUsers = users.size();
    }

    public String Add() {
        FacesMessage message;
        MasterDataService mds = new MasterDataService();
        UserPK userPK = new UserPK(userId, roleId, tenantId, productId);
        user.setUserPK(userPK);
        //checking whether total number of users more than paramvalue or not
        Subscription subscription = mds.getSubscription(tenantId, productId);
        Packageparam packageparam = mds.getPackageParam(subscription.getProdpackageId(), subscription.getSubscriptionPK().getProductId(), "userno");
        int paramValue = Integer.valueOf(packageparam.getParamValue());
        int response = 0;
        if (noOfUsers == paramValue) {
            response = HedwigResponseCode.USER_EXCEEDED;
        } else {
            response = mds.insertIntoUser(user);
        }
        HedwigResponseMessage responseMessage = new HedwigResponseMessage();
        String redirectUrl;
        if (response != HedwigResponseCode.SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(response));
            redirectUrl = "UserAdd";
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(response));
            redirectUrl = "UserList";
        }
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);
        return redirectUrl;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getNoOfUsers() {
        return noOfUsers;
    }

    public void setNoOfUsers(int noOfUsers) {
        this.noOfUsers = noOfUsers;
    }

}
