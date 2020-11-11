/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.entities;

import java.io.Serializable;
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
@Table(name = "subscription")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Subscription.findAll", query = "SELECT s FROM Subscription s")})
public class Subscription implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected SubscriptionPK subscriptionPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "prodpackage_id")
    private int prodpackageId;
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
    @Size(min = 1, max = 45)
    @Column(name = "dbadmin_user_id")
    private String dbadminUserId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "dbadmin_password")
    private String dbadminPassword;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "dbconn_url")
    private String dbconnUrl;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "subscription")
    private List<Role> roleList;
    @JoinColumn(name = "product_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Product product;
    @JoinColumn(name = "tenant_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Tenant tenant;

    public Subscription() {
    }

    public Subscription(SubscriptionPK subscriptionPK) {
        this.subscriptionPK = subscriptionPK;
    }

    public Subscription(SubscriptionPK subscriptionPK, int prodpackageId, Date startDate, Date endDate, Date createTime, Date updateTime, String dbadminUserId, String dbadminPassword, String dbconnUrl) {
        this.subscriptionPK = subscriptionPK;
        this.prodpackageId = prodpackageId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.dbadminUserId = dbadminUserId;
        this.dbadminPassword = dbadminPassword;
        this.dbconnUrl = dbconnUrl;
    }

    public Subscription(int tenantId, int productId) {
        this.subscriptionPK = new SubscriptionPK(tenantId, productId);
    }

    public SubscriptionPK getSubscriptionPK() {
        return subscriptionPK;
    }

    public void setSubscriptionPK(SubscriptionPK subscriptionPK) {
        this.subscriptionPK = subscriptionPK;
    }

    public int getProdpackageId() {
        return prodpackageId;
    }

    public void setProdpackageId(int prodpackageId) {
        this.prodpackageId = prodpackageId;
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

    public String getDbadminUserId() {
        return dbadminUserId;
    }

    public void setDbadminUserId(String dbadminUserId) {
        this.dbadminUserId = dbadminUserId;
    }

    public String getDbadminPassword() {
        return dbadminPassword;
    }

    public void setDbadminPassword(String dbadminPassword) {
        this.dbadminPassword = dbadminPassword;
    }

    public String getDbconnUrl() {
        return dbconnUrl;
    }

    public void setDbconnUrl(String dbconnUrl) {
        this.dbconnUrl = dbconnUrl;
    }

    @XmlTransient
    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (subscriptionPK != null ? subscriptionPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Subscription)) {
            return false;
        }
        Subscription other = (Subscription) object;
        if ((this.subscriptionPK == null && other.subscriptionPK != null) || (this.subscriptionPK != null && !this.subscriptionPK.equals(other.subscriptionPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.dgrf.dgrftenant.entities.Subscription[ subscriptionPK=" + subscriptionPK + " ]";
    }
    
}
