/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.Package;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.hedwig.cloud.response.HedwigResponseCode;
import org.hedwig.cloud.response.HedwigResponseMessage;
import org.hedwig.tenant.entities.Prodpackage;
import org.hedwig.tenant.service.MasterDataService;

/**
 *
 * @author dgrf-iv
 */
@Named(value = "packageEdit")
@ViewScoped
public class PackageEdit implements Serializable {

    private int productId;
    private int packageId;
    private String productName;
    private String packageName;
    private Prodpackage packageBeanEdit;

    public PackageEdit() {
    }

    public void fillPackageValues() {
        MasterDataService mds = new MasterDataService();
        packageBeanEdit = mds.getPackageValues(productId, packageId);
        productName = mds.getProductName(productId);
        packageName = mds.getPackageName(packageId, productId);
    }

    public String savePackage() {
        HedwigResponseMessage responseMessage = new HedwigResponseMessage();
        FacesMessage message;
        String redirectUrl;
        MasterDataService mds = new MasterDataService();
        int response = mds.editPackage(packageBeanEdit);
        
        if (response != HedwigResponseCode.SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(response));
            redirectUrl = "PackageEdit";
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(response));
            redirectUrl = "PackageList";
        }
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);
        return redirectUrl;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public Prodpackage getPackageBeanEdit() {
        return packageBeanEdit;
    }

    public void setPackageBeanEdit(Prodpackage packageBeanEdit) {
        this.packageBeanEdit = packageBeanEdit;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

}
