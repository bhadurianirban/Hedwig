/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.Package;

import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.hedwig.cloud.response.HedwigResponseMessage;
import org.hedwig.tenant.entities.Packageparam;
import org.hedwig.tenant.service.MasterDataService;

/**
 *
 * @author dgrf
 */
@Named(value = "paramList")
@ViewScoped
public class ParamList implements Serializable{

    private int productId;
    private int packageId;
    private String packageName;
    private List<Packageparam> packageparams; 
    private Packageparam selectedParam;
    
    public ParamList() {
    }
    
    public void fillParamValues() {
        MasterDataService mds = new MasterDataService();
        packageparams = mds.getAllParamList(productId,packageId);
        packageName = mds.getPackageName(packageId, productId);
    }
    
    public String goToEditParam() {
        return "ParamEdit";
    }
    
    public String deleteParam(Packageparam packageparam) {
        MasterDataService mds = new MasterDataService();
        int response = mds.deleteParam(packageparam);
        HedwigResponseMessage responseMessage = new HedwigResponseMessage();
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, responseMessage.getResponseMessage(response), responseMessage.getResponseMessage(response));
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);
        return "ParamList";
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

    public List<Packageparam> getPackageparams() {
        return packageparams;
    }

    public void setPackageparams(List<Packageparam> packageparams) {
        this.packageparams = packageparams;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Packageparam getSelectedParam() {
        return selectedParam;
    }

    public void setSelectedParam(Packageparam selectedParam) {
        this.selectedParam = selectedParam;
    }
    
}
