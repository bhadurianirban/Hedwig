/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.login;


import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.hedwig.cloud.dto.UserAuthDTO;
import org.hedwig.cloud.response.HedwigResponseCode;
import org.hedwig.tenant.entities.Subscription;
import org.hedwig.tenant.entities.Tenant;

import org.hedwig.tenant.service.MasterDataService;

/**
 *
 * @author dgrf-iv
 */
@Named(value = "loginController")
@SessionScoped
public class LoginController implements Serializable {

    private int tenantId;
    private int productId;
    private int roleId;
    private Tenant tenant;
    private String userId;
    private String userName;
    private String password;
    private int loginType;
    private List<Tenant> tenants;
    private List<Subscription> subscriptions;
    private Map<String, Object> tenantMap;
    private Map<String, Object> productMap;

    public LoginController() {
        tenant = new Tenant();
    }

    @PostConstruct
    private void init() {
        loginType=0;
        tenant = new Tenant();
    }

    //method login for tenant
    public String tenantLogin() {
        FacesMessage message;
        tenant.setEmail(userId);
        tenant.setPassword(password);
        MasterDataService mds = new MasterDataService();
        List<Tenant> validTenant = mds.findTenant(tenant);
        if (validTenant.isEmpty()) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login error", "You Entered Wrong Credentials");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(null, message);
            return "TenantLogin";
        } else {
            tenant = validTenant.get(0);
            userName = tenant.getName();
            loginType = 3; //tenant login
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "login success", "Successfully Logged In");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(null, message);
            return "ProductList";
        }
    }

    //method login for admin
    public String adminLogin() {
        FacesMessage message;

        if (userId.equals("yoll") && password.equals("1234")) {
            loginType=2; //admin login type
            userName = "Hedwig Super User";
            return "ProductList";
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "login error", "User Name or Password does not match");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(null, message);
            return "AdminLogin";
        }
    }

    //method login for user
    public String userLogin() {
        FacesMessage message;
        MasterDataService mds = new MasterDataService();
        UserAuthDTO userAuthDTO = new UserAuthDTO();
        userAuthDTO.setTenantId(tenantId);
        userAuthDTO.setProductId(productId);
        userAuthDTO.setUserId(userId);
        userAuthDTO.setPassword(password);
        userAuthDTO = mds.authenticateUser(userAuthDTO);
        if (userAuthDTO.getResponseCode() == HedwigResponseCode.NO_USER) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login error", "User does not exist");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.getExternalContext().getFlash().setKeepMessages(true);
            fc.addMessage(null, message);
            return "UserLogin";
        }
        if (userAuthDTO.getResponseCode() == HedwigResponseCode.USER_INACTIVE) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login error", "User not active, Contact Admin");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.getExternalContext().getFlash().setKeepMessages(true);
            fc.addMessage(null, message);
            return "UserLogin";
        }
        if (userAuthDTO.getResponseCode() == HedwigResponseCode.NO_SUBCRIPTION) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login error", "Subsciption not active, Contact Admin");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.getExternalContext().getFlash().setKeepMessages(true);
            fc.addMessage(null, message);
            return "UserLogin";
        }
        if (userAuthDTO.getResponseCode() == HedwigResponseCode.PASSWORD_INCORRECT) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login error", "Wrong Password");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.getExternalContext().getFlash().setKeepMessages(true);
            fc.addMessage(null, message);
            return "UserLogin";
        }
        if (userAuthDTO.getResponseCode() == HedwigResponseCode.WRONG_ATTEMPTS_EXCEED) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login error", "You are now an inactive user, contact admin to active.");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.getExternalContext().getFlash().setKeepMessages(true);
            fc.addMessage(null, message);
            return "UserLogin";
        }
        if (userAuthDTO.getResponseCode() == HedwigResponseCode.SUCCESS) {
            loginType = 1;
            roleId = userAuthDTO.getRoleId();
            userName = userAuthDTO.getName();
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Login success", "Successfully Logged In");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(null, message);
            return "UserDetails";
        }
        message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login error", "Contact admin");
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.getExternalContext().getFlash().setKeepMessages(true);
        fc.addMessage(null, message);
        return "UserLogin";

    }

    public String logout() {
        tenant.setId(0);
        tenant.setName(null);
        userName = null;
        loginType = 0;
        roleId = 0;
        return "index";
    }

    public void getTenantList() {
        MasterDataService mds = new MasterDataService();
        tenants = mds.getTenantList();
        tenantMap = new HashMap<>();
        for (Tenant t : tenants) {
            tenantMap.put(t.getName(), t.getId());
        }
    }

    public void onTenantChange() {
        MasterDataService mds = new MasterDataService();
        if (tenantId != 0) {
            subscriptions = mds.getSubscriptionList(tenantId);
            productMap = new HashMap<>();
            for (Subscription subscription : subscriptions) {
                String prodName = mds.getProductName(subscription.getSubscriptionPK().getProductId());
                productMap.put(prodName, subscription.getSubscriptionPK().getProductId());
            }
        } else {
            productMap = new HashMap<>();
        }
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Tenant> getTenants() {
        return tenants;
    }

    public Map<String, Object> getTenantMap() {
        return tenantMap;
    }

    public void setTenants(List<Tenant> tenants) {
        this.tenants = tenants;
    }

    public void setTenantMap(Map<String, Object> tenantMap) {
        this.tenantMap = tenantMap;
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

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }


    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

}
