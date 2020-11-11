/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.User;

import java.io.Serializable;
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
@Named(value = "userEdit")
@ViewScoped
public class UserEdit implements Serializable {

    private String userId;
    private int tenantId;
    private int roleId;
    private int productId;
    private User userEditBean;

    public UserEdit() {
    }

    public void fillEditUserValues() {
        MasterDataService mds = new MasterDataService();
        userEditBean = mds.getUser(userId, tenantId, productId, roleId);
    }

    public String save() {
        MasterDataService mds = new MasterDataService();
        int response = mds.editUser(userEditBean);
        HedwigResponseMessage responseMessage = new HedwigResponseMessage();
        FacesMessage message;
        String redirectUrl;
        if ( response != HedwigResponseCode.SUCCESS ) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(response));
            redirectUrl = "UserEdit";
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(response));
            redirectUrl = "UserList";
        }
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);
        return redirectUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public User getUserEditBean() {
        return userEditBean;
    }

    public void setUserEditBean(User userEditBean) {
        this.userEditBean = userEditBean;
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

}
