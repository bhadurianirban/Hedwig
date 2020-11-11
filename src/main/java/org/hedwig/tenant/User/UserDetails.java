/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.User;

import java.io.Serializable;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.hedwig.tenant.entities.User;
import org.hedwig.tenant.service.MasterDataService;

/**
 *
 * @author dgrf-iv
 */
@Named(value = "userDetails")
@ViewScoped
public class UserDetails implements Serializable{

    private int tenantId;
    private int productId;
    private String userId;
    private String tenantName;
    private String prodName;
    private String roleName;
    private int roleId;
    private User user;
    private User selectedUser;
    
    public UserDetails() {
    }
    
    public void fillUserDetails(){
        MasterDataService mds = new MasterDataService();
        user = mds.getUser(userId, tenantId, productId, roleId);
        tenantName = mds.getTenantName(tenantId);
        prodName = mds.getProductName(productId);
        roleName = mds.getRoleName(roleId, tenantId, productId);
    }
    
    public String save(){
        MasterDataService mds = new MasterDataService();
        int response = mds.editUser(selectedUser);
        
        return "UserDetails";
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
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
    
}
