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
import org.hedwig.tenant.entities.Packageparam;
import org.hedwig.tenant.service.MasterDataService;

/**
 *
 * @author dgrf
 */
@Named(value = "paramEdit")
@ViewScoped
public class ParamEdit implements Serializable{

    private int productId;
    private int packageId;
    private String paramKey;
    private String productName;
    private String packageName;
  
    private Packageparam packageparamEdit;
    
    public ParamEdit() {
    }

    public int getProductId() {
        return productId;
    }
    
    public void fillParamValues() {
        MasterDataService mds = new MasterDataService();
        packageparamEdit = mds.getParamValues(productId,packageId,paramKey);
        productName = mds.getProductName(productId);
        packageName = mds.getPackageName(packageId, productId);
    }
    
    public String saveParam() {
        HedwigResponseMessage responseMessage = new HedwigResponseMessage();
        MasterDataService mds = new MasterDataService();
        int response = mds.editParam(packageparamEdit);
        FacesMessage message;
        String redirectUrl;
        
        if ( response != HedwigResponseCode.SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(response));
            redirectUrl = "ParamEdit";
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Success", responseMessage.getResponseMessage(response));
            redirectUrl = "ParamList";
        }
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);
        return redirectUrl;
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

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public Packageparam getPackageparamEdit() {
        return packageparamEdit;
    }

    public void setPackageparamEdit(Packageparam packageparamEdit) {
        this.packageparamEdit = packageparamEdit;
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
