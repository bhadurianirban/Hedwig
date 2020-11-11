/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author bhaduri
 */
@Embeddable
public class PaymentPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "tenant_id")
    private int tenantId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "prodpackage_id")
    private int prodpackageId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "product_id")
    private int productId;

    public PaymentPK() {
    }

    public PaymentPK(int id, int tenantId, int prodpackageId, int productId) {
        this.id = id;
        this.tenantId = tenantId;
        this.prodpackageId = prodpackageId;
        this.productId = productId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public int getProdpackageId() {
        return prodpackageId;
    }

    public void setProdpackageId(int prodpackageId) {
        this.prodpackageId = prodpackageId;
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
        hash += (int) id;
        hash += (int) tenantId;
        hash += (int) prodpackageId;
        hash += (int) productId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PaymentPK)) {
            return false;
        }
        PaymentPK other = (PaymentPK) object;
        if (this.id != other.id) {
            return false;
        }
        if (this.tenantId != other.tenantId) {
            return false;
        }
        if (this.prodpackageId != other.prodpackageId) {
            return false;
        }
        if (this.productId != other.productId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.dgrf.dgrftenant.entities.PaymentPK[ id=" + id + ", tenantId=" + tenantId + ", prodpackageId=" + prodpackageId + ", productId=" + productId + " ]";
    }
    
}
