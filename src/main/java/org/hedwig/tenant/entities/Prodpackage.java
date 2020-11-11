/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author bhaduri
 */
@Entity
@Table(name = "prodpackage")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Prodpackage.findAll", query = "SELECT p FROM Prodpackage p")})
public class Prodpackage implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ProdpackagePK prodpackagePK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name")
    private String name;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "price")
    private BigDecimal price;
    @Basic(optional = false)
    @NotNull
    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "duration_days")
    private int durationDays;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "prodpackage")
    private List<Packageparam> packageparamList;
    @JoinColumn(name = "product_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Product product;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "prodpackage")
    private List<Payment> paymentList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "prodpackage")
    private List<Cart> cartList;

    public Prodpackage() {
    }

    public Prodpackage(ProdpackagePK prodpackagePK) {
        this.prodpackagePK = prodpackagePK;
    }

    public Prodpackage(ProdpackagePK prodpackagePK, String name, BigDecimal price, Date startDate, Date endDate, Date createTime, Date updateTime, int durationDays) {
        this.prodpackagePK = prodpackagePK;
        this.name = name;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.durationDays = durationDays;
    }

    public Prodpackage(int id, int productId) {
        this.prodpackagePK = new ProdpackagePK(id, productId);
    }

    public ProdpackagePK getProdpackagePK() {
        return prodpackagePK;
    }

    public void setProdpackagePK(ProdpackagePK prodpackagePK) {
        this.prodpackagePK = prodpackagePK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    @XmlTransient
    public List<Packageparam> getPackageparamList() {
        return packageparamList;
    }

    public void setPackageparamList(List<Packageparam> packageparamList) {
        this.packageparamList = packageparamList;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @XmlTransient
    public List<Payment> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<Payment> paymentList) {
        this.paymentList = paymentList;
    }

    @XmlTransient
    public List<Cart> getCartList() {
        return cartList;
    }

    public void setCartList(List<Cart> cartList) {
        this.cartList = cartList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (prodpackagePK != null ? prodpackagePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Prodpackage)) {
            return false;
        }
        Prodpackage other = (Prodpackage) object;
        if ((this.prodpackagePK == null && other.prodpackagePK != null) || (this.prodpackagePK != null && !this.prodpackagePK.equals(other.prodpackagePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.dgrf.dgrftenant.entities.Prodpackage[ prodpackagePK=" + prodpackagePK + " ]";
    }
    
}
