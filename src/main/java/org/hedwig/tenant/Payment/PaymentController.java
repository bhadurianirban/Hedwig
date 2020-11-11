/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.Payment;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.hedwig.cloud.response.HedwigResponseCode;
import org.hedwig.cloud.response.HedwigResponseMessage;
import org.hedwig.tenant.JPA.exceptions.NonexistentEntityException;

import org.hedwig.tenant.entities.Cart;
import org.hedwig.tenant.entities.CartPK;
import org.hedwig.tenant.entities.Prodpackage;
import org.hedwig.tenant.entities.Subscription;
import org.hedwig.tenant.login.LoginController;
import org.hedwig.tenant.service.MasterDataService;

/**
 *
 * @author dgrf-iv
 */
@Named(value = "paymentController")
@ViewScoped
public class PaymentController implements Serializable {

    private int productId;
    private int packageId;
    private int cartId;
    private String productName;
    private String packageName;
    private Prodpackage paymentPackage;
    private int tenantId;

    @Inject
    LoginController loginController;

    public PaymentController() {
        paymentPackage = new Prodpackage();
    }

    public String makePayment() throws NonexistentEntityException {
        HedwigResponseMessage responseMessage = new HedwigResponseMessage();
        FacesMessage message;
        tenantId = loginController.getTenant().getId();
        
        MasterDataService mds = new MasterDataService();
        Subscription subscription = mds.getSubscription(tenantId, productId);

        if (subscription == null) {
            int response = mds.insertIntoPayment(paymentPackage, tenantId);

            if ( response != HedwigResponseCode.SUCCESS ) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(response));
                FacesContext f = FacesContext.getCurrentInstance();
                f.getExternalContext().getFlash().setKeepMessages(true);
                f.addMessage(null, message);
                String redirectUrl = "MakePayment";
                return redirectUrl;
            } else {

                int response1 = mds.insertIntoSubscription(paymentPackage, tenantId);
                if ( response1 == HedwigResponseCode.SUCCESS ) {
                    CartPK cartPK = new CartPK(cartId, tenantId, packageId, productId);
                    Cart cart = new Cart(cartPK);
                    mds.deleteCart(cart);
                }
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(response1));
                FacesContext fc = FacesContext.getCurrentInstance();
                fc.getExternalContext().getFlash().setKeepMessages(true);
                fc.addMessage(null, message);
                String redirectUrl = "SubscriptionList";
                return redirectUrl;
            }
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Payment Fail", "Subscription for this package already exists.");
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.getExternalContext().getFlash().setKeepMessages(true);
            fc.addMessage(null, message);
            String redirectUrl = "CartList";
            return redirectUrl;
        }
    }

    public void fillPaymentValues() {
        MasterDataService mds = new MasterDataService();
        paymentPackage = mds.getPackageValues(productId, packageId);
        productName = mds.getProductName(productId);
        packageName = mds.getPackageName(packageId, productId);
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

    public Prodpackage getPaymentPackage() {
        return paymentPackage;
    }

    public void setPaymentPackage(Prodpackage paymentPackage) {
        this.paymentPackage = paymentPackage;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
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

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }
    

}
