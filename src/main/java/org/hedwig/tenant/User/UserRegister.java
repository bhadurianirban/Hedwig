/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.User;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.hedwig.cloud.response.HedwigResponseCode;
import org.hedwig.cloud.response.HedwigResponseMessage;
import org.hedwig.tenant.entities.Packageparam;
import org.hedwig.tenant.entities.Role;
import org.hedwig.tenant.entities.Subscription;
import org.hedwig.tenant.entities.Tenant;
import org.hedwig.tenant.entities.User;
import org.hedwig.tenant.entities.UserPK;
import org.hedwig.tenant.service.MasterDataService;

/**
 *
 * @author dgrf-iv
 */
@Named(value = "userRegister")
@ViewScoped
public class UserRegister implements Serializable {

    private int tenantId;
    private String tenantName;
    private int productId;
    private String productName;
    private User user;
    private List<Subscription> subscriptions;
    private List<Role> roles;
    private Map<String, Object> productMap;
    private Map<String, Object> roleMap;
    private List<Tenant> tenants;
    private Map<String, Object> tenantMap;

    public UserRegister() {

    }

    public void fillForm() {
        user = new User();
        MasterDataService mds = new MasterDataService();
        if (tenantId != 0) {
            UserPK userPK = new UserPK();
            userPK.setTenantId(tenantId);
            tenantName = mds.getTenantName(tenantId);
            if (productId != 0) {
                productName = mds.getProductName(productId);

                userPK.setProductId(productId);
                user.setUserPK(userPK);
                roles = mds.getRoleList(user.getUserPK().getTenantId(), user.getUserPK().getProductId());
                roleMap = new HashMap<>();
                for (Role role : roles) {
                    if (role.getRolePK().getId() != 1) {
                        roleMap.put(role.getName(), role.getRolePK().getId());
                    }
                }
            } else {
                
                user.setUserPK(userPK);
                subscriptions = mds.getSubscriptionList(user.getUserPK().getTenantId());
                productMap = new HashMap<>();
                for (Subscription subscription : subscriptions) {
                    String prodName = mds.getProductName(subscription.getSubscriptionPK().getProductId());
                    productMap.put(prodName, subscription.getSubscriptionPK().getProductId());
                }
            }
        } else {
            productId=0;
            UserPK userPK = new UserPK(null, 0,0,0);
            user.setUserPK(userPK);
            //get tenant list
            tenants = mds.getTenantList();
            tenantMap = new HashMap<>();
            for (Tenant t : tenants) {
                tenantMap.put(t.getName(), t.getId());
            }
        }

    }

    public void onTenantChange() {
        MasterDataService mds = new MasterDataService();
        if (user.getUserPK().getTenantId() != 0) {
            subscriptions = mds.getSubscriptionList(user.getUserPK().getTenantId());
            productMap = new HashMap<>();
            for (Subscription subscription : subscriptions) {
                String prodName = mds.getProductName(subscription.getSubscriptionPK().getProductId());
                productMap.put(prodName, subscription.getSubscriptionPK().getProductId());
            }
        } else {
            productMap = new HashMap<>();
        }
    }

    public void onTenantProductChange() {

        MasterDataService mds = new MasterDataService();
        if (user.getUserPK().getTenantId() != 0) {
            roles = mds.getRoleList(user.getUserPK().getTenantId(), user.getUserPK().getProductId());
            roleMap = new HashMap<>();
            for (Role role : roles) {
                if (role.getRolePK().getId() != 1) {
                    roleMap.put(role.getName(), role.getRolePK().getId());
                }
            }

        } else {
            roleMap = new HashMap<>();
        }
    }

    public String userAdd() {
        int response;
        FacesMessage message;
        HedwigResponseMessage responseMessage = new HedwigResponseMessage();
        MasterDataService mds = new MasterDataService();
        user.setActive(false);
        Subscription subscription = mds.getSubscription(user.getUserPK().getTenantId(), user.getUserPK().getProductId());
        Packageparam packageparam = mds.getPackageParam(subscription.getProdpackageId(), subscription.getSubscriptionPK().getProductId(), "userno");
        int noOfUsers = Integer.valueOf(packageparam.getParamValue());
        List<User> users = mds.getUserListByTenantIdProductId(user.getUserPK().getTenantId(), user.getUserPK().getProductId());
        if (subscription.getStartDate().equals(subscription.getEndDate())) {
            response = HedwigResponseCode.SUBSCRIPTION_NOT_ACTIVE;

        } else if (users.size() == noOfUsers) {
            response = HedwigResponseCode.USER_EXCEEDED;

        } else {
            response = mds.insertIntoUser(user);
        }
        String redirectUrl;
        if (response != HedwigResponseCode.SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(response));
            redirectUrl = "/UserRegister?faces-redirect=true";
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(response));
            redirectUrl = "/UserRegisteredSuccess?faces-redirect=true";
        }
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);
        return redirectUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Map<String, Object> getProductMap() {
        return productMap;
    }

    public void setProductMap(Map<String, Object> productMap) {
        this.productMap = productMap;
    }

    public Map<String, Object> getRoleMap() {
        return roleMap;
    }

    public void setRoleMap(Map<String, Object> roleMap) {
        this.roleMap = roleMap;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Tenant> getTenants() {
        return tenants;
    }

    public void setTenants(List<Tenant> tenants) {
        this.tenants = tenants;
    }

    public Map<String, Object> getTenantMap() {
        return tenantMap;
    }

    public void setTenantMap(Map<String, Object> tenantMap) {
        this.tenantMap = tenantMap;
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

}
