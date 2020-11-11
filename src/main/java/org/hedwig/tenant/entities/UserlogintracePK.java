/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author bhaduri
 */
@Embeddable
public class UserlogintracePK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "login_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date loginTime;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "user_id")
    private String userId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "role_id")
    private int roleId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "tenant_id")
    private int tenantId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "product_id")
    private int productId;

    public UserlogintracePK() {
    }

    public UserlogintracePK(Date loginTime, String userId, int roleId, int tenantId, int productId) {
        this.loginTime = loginTime;
        this.userId = userId;
        this.roleId = roleId;
        this.tenantId = tenantId;
        this.productId = productId;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (loginTime != null ? loginTime.hashCode() : 0);
        hash += (userId != null ? userId.hashCode() : 0);
        hash += (int) roleId;
        hash += (int) tenantId;
        hash += (int) productId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserlogintracePK)) {
            return false;
        }
        UserlogintracePK other = (UserlogintracePK) object;
        if ((this.loginTime == null && other.loginTime != null) || (this.loginTime != null && !this.loginTime.equals(other.loginTime))) {
            return false;
        }
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        if (this.roleId != other.roleId) {
            return false;
        }
        if (this.tenantId != other.tenantId) {
            return false;
        }
        if (this.productId != other.productId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.dgrf.dgrftenant.entities.UserlogintracePK[ loginTime=" + loginTime + ", userId=" + userId + ", roleId=" + roleId + ", tenantId=" + tenantId + ", productId=" + productId + " ]";
    }
    
}
