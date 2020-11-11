/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.product;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.hedwig.cloud.response.HedwigResponseMessage;

import org.hedwig.tenant.entities.Product;
import org.hedwig.tenant.service.MasterDataService;

/**
 *
 * @author dgrf-iv
 */
@Named(value = "productList")
@ViewScoped
public class ProductList implements Serializable {

    private List<Product> products;
    private Product selectedProduct;

    public ProductList() {
    }

    @PostConstruct
    public void init() {
    }

    public void fillProductValues() {
        MasterDataService mds = new MasterDataService();
        products = mds.getProductList();
    }

    public String goToEditProduct() {
        return "ProductEdit";
    }

    public String goToPackageList() {
        return "PackageList";
    }

    public String deleteProduct(Product product) {
        MasterDataService mds = new MasterDataService();
        int response = mds.deleteProduct(product);
        HedwigResponseMessage responseMessage = new HedwigResponseMessage();
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(response));
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);
        String redirectUrl = "ProductList";
        return redirectUrl;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Product getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

}
