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
import javax.validation.constraints.Size;

/**
 *
 * @author bhaduri
 */
@Embeddable
public class PackageparamPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "prodpackage_id")
    private int prodpackageId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "product_id")
    private int productId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "param_key")
    private String paramKey;

    public PackageparamPK() {
    }

    public PackageparamPK(int prodpackageId, int productId, String paramKey) {
        this.prodpackageId = prodpackageId;
        this.productId = productId;
        this.paramKey = paramKey;
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

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) prodpackageId;
        hash += (int) productId;
        hash += (paramKey != null ? paramKey.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PackageparamPK)) {
            return false;
        }
        PackageparamPK other = (PackageparamPK) object;
        if (this.prodpackageId != other.prodpackageId) {
            return false;
        }
        if (this.productId != other.productId) {
            return false;
        }
        if ((this.paramKey == null && other.paramKey != null) || (this.paramKey != null && !this.paramKey.equals(other.paramKey))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.dgrf.dgrftenant.entities.PackageparamPK[ prodpackageId=" + prodpackageId + ", productId=" + productId + ", paramKey=" + paramKey + " ]";
    }
    
}
