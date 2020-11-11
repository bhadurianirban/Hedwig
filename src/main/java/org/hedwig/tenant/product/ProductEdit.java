/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.product;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.hedwig.cloud.response.HedwigResponseCode;
import org.hedwig.cloud.response.HedwigResponseMessage;
import org.hedwig.tenant.entities.Product;
import org.hedwig.tenant.service.MasterDataService;

/**
 *
 * @author dgrf-iv
 */
@Named(value = "productEdit")
@ViewScoped
public class ProductEdit implements Serializable {

    private int productId;
    private Product productBeanEdit;

    public ProductEdit() {
    }

    public void fillProductValues() {
        MasterDataService mds = new MasterDataService();
        productBeanEdit = mds.getProduct(productId);
    }
    
    public String saveProduct(){
        MasterDataService mds = new MasterDataService();
        int response = mds.editProduct(productBeanEdit);
        FacesMessage message;
        String redirectUrl;
        if (response != HedwigResponseCode.SUCCESS) {
            redirectUrl = "ProductEdit";
            HedwigResponseMessage responseMessage = new HedwigResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(response));
        } else {
            HedwigResponseMessage responseMessage = new HedwigResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(response));
            redirectUrl = "ProductList";
            
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

    public Product getProductBeanEdit() {
        return productBeanEdit;
    }

    public void setProductBeanEdit(Product productBeanEdit) {
        this.productBeanEdit = productBeanEdit;
    }
    
}
