/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author bhaduri
 */
@Entity
@Table(name = "packageparam")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Packageparam.findAll", query = "SELECT p FROM Packageparam p")})
public class Packageparam implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PackageparamPK packageparamPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "param_description")
    private String paramDescription;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "param_value")
    private String paramValue;
    @JoinColumns({
        @JoinColumn(name = "prodpackage_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "product_id", referencedColumnName = "product_id", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private Prodpackage prodpackage;

    public Packageparam() {
    }

    public Packageparam(PackageparamPK packageparamPK) {
        this.packageparamPK = packageparamPK;
    }

    public Packageparam(PackageparamPK packageparamPK, String paramDescription, String paramValue) {
        this.packageparamPK = packageparamPK;
        this.paramDescription = paramDescription;
        this.paramValue = paramValue;
    }

    public Packageparam(int prodpackageId, int productId, String paramKey) {
        this.packageparamPK = new PackageparamPK(prodpackageId, productId, paramKey);
    }

    public PackageparamPK getPackageparamPK() {
        return packageparamPK;
    }

    public void setPackageparamPK(PackageparamPK packageparamPK) {
        this.packageparamPK = packageparamPK;
    }

    public String getParamDescription() {
        return paramDescription;
    }

    public void setParamDescription(String paramDescription) {
        this.paramDescription = paramDescription;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public Prodpackage getProdpackage() {
        return prodpackage;
    }

    public void setProdpackage(Prodpackage prodpackage) {
        this.prodpackage = prodpackage;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (packageparamPK != null ? packageparamPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Packageparam)) {
            return false;
        }
        Packageparam other = (Packageparam) object;
        if ((this.packageparamPK == null && other.packageparamPK != null) || (this.packageparamPK != null && !this.packageparamPK.equals(other.packageparamPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.dgrf.dgrftenant.entities.Packageparam[ packageparamPK=" + packageparamPK + " ]";
    }
    
}
