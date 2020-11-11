/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author bhaduri
 */
@Entity
@Table(name = "userlogintrace")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Userlogintrace.findAll", query = "SELECT u FROM Userlogintrace u")})
public class Userlogintrace implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UserlogintracePK userlogintracePK;
    @JoinColumns({
        @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "role_id", referencedColumnName = "role_id", insertable = false, updatable = false),
        @JoinColumn(name = "tenant_id", referencedColumnName = "tenant_id", insertable = false, updatable = false),
        @JoinColumn(name = "product_id", referencedColumnName = "product_id", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private User user;

    public Userlogintrace() {
    }

    public Userlogintrace(UserlogintracePK userlogintracePK) {
        this.userlogintracePK = userlogintracePK;
    }

    public Userlogintrace(Date loginTime, String userId, int roleId, int tenantId, int productId) {
        this.userlogintracePK = new UserlogintracePK(loginTime, userId, roleId, tenantId, productId);
    }

    public UserlogintracePK getUserlogintracePK() {
        return userlogintracePK;
    }

    public void setUserlogintracePK(UserlogintracePK userlogintracePK) {
        this.userlogintracePK = userlogintracePK;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userlogintracePK != null ? userlogintracePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Userlogintrace)) {
            return false;
        }
        Userlogintrace other = (Userlogintrace) object;
        if ((this.userlogintracePK == null && other.userlogintracePK != null) || (this.userlogintracePK != null && !this.userlogintracePK.equals(other.userlogintracePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.dgrf.dgrftenant.entities.Userlogintrace[ userlogintracePK=" + userlogintracePK + " ]";
    }
    
}
