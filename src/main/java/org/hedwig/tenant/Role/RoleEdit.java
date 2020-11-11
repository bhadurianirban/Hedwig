/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.Role;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.hedwig.cloud.response.HedwigResponseCode;
import org.hedwig.cloud.response.HedwigResponseMessage;
import org.hedwig.tenant.entities.Role;
import org.hedwig.tenant.service.MasterDataService;

/**
 *
 * @author dgrf-iv
 */
@Named(value = "roleEdit")
@ViewScoped
public class RoleEdit implements Serializable {

    private int tenantId;
    private int roleId;
    private int productId;
    private String productName;
    private Role editRole;

    public RoleEdit() {
    }

    public void fillEditRoleValues() {
        MasterDataService mds = new MasterDataService();
        editRole = mds.getRoleEditValue(roleId, tenantId, productId);
        productName = mds.getProductName(productId);
    }

    public String save() {
        MasterDataService mds = new MasterDataService();
        int response = mds.editRole(editRole);
        HedwigResponseMessage responseMessage = new HedwigResponseMessage();
        FacesMessage message;
        String redirectUrl;

        if ( response != HedwigResponseCode.SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(response));
            redirectUrl = "RoleEdit";
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(response));
            redirectUrl = "RoleList";
        }
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);
        return redirectUrl;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Role getEditRole() {
        return editRole;
    }

    public void setEditRole(Role editRole) {
        this.editRole = editRole;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

}
