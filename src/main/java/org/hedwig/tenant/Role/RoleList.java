/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.Role;

import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.hedwig.cloud.response.HedwigResponseMessage;
import org.hedwig.tenant.entities.Role;
import org.hedwig.tenant.service.MasterDataService;

/**
 *
 * @author dgrf-iv
 */
@Named(value = "roleList")
@ViewScoped
public class RoleList implements Serializable {

    private int tenantId;
    private int productId;
    private String productName;
    private List<Role> roles;
    private Role selectedRole;

    public RoleList() {
    }

    public void fillRoleValues() {
        MasterDataService mds = new MasterDataService();
        roles = mds.getRoleList(tenantId, productId);
        productName = mds.getProductName(productId);
    }

    public String goToEditRole() {
        return "RoleEdit";
    }

    public String goToAddUser() {
        return "UserList";
    }

    public String goToDeleteRole(Role role) {
        MasterDataService mds = new MasterDataService();
        int response = mds.deleteRole(role);
        HedwigResponseMessage responseMessage = new HedwigResponseMessage();
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(response));
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);
        String redirectUrl = "RoleList";
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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Role getSelectedRole() {
        return selectedRole;
    }

    public void setSelectedRole(Role selectedRole) {
        this.selectedRole = selectedRole;
    }

}
