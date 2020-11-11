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
@Named(value = "userList")
@ViewScoped
public class UserList implements Serializable {

    private int roleId;
    private int tenantId;
    private int productId;
    private String roleName;
    List<User> users;
    private User selectedUser;

    public UserList() {
    }

    public void fillUserValues() {
        MasterDataService mds = new MasterDataService();
        users = mds.getUserList(roleId, tenantId, productId);
        roleName = mds.getRoleName(roleId, tenantId, productId);
    }

    public String goToEditUser() {
        return "UserEdit";
    }

    public String backToRoleList() {
        String redirectUrl = "/Role/RoleList?faces-redirect=true&tenantid=" + tenantId + "&productid=" + productId;
        return redirectUrl;
    }

    public String goToDeleteUser(User user) {
        MasterDataService mds = new MasterDataService();
        int response = mds.deleteUser(user);
        HedwigResponseMessage responseMessage = new HedwigResponseMessage();
        FacesMessage message;
        if ( response != HedwigResponseCode.SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(response));
        }else{
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(response));
        }
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);
        String redirectUrl = "UserList";
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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

}
