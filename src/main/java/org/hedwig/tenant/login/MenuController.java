/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.login;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author dgrf-iv
 */
@Named(value = "menuController")
@RequestScoped
public class MenuController {

    private MenuModel menuModel;

    @Inject
    LoginController loginController;

    public MenuController() {
    }

    @PostConstruct
    public void init() {
        menuModel = new DefaultMenuModel();
        DefaultMenuItem item;

        switch (loginController.getLoginType()) {
            case 1:
                //tenantLoginController.getTenant().setName(tenantLoginController.getUserName());
                if (loginController.getRoleId() == 1) {
                    item = new DefaultMenuItem("Activate");
                    item.setOutcome("/cloud/User/UserActivation?faces-redirect=true&tenantid=" + loginController.getTenantId() + "&productid=" + loginController.getProductId());
                    menuModel.addElement(item);
                    item = new DefaultMenuItem("Deactivate");
                    item.setOutcome("/cloud/User/UserDeactivation?faces-redirect=true&tenantid=" + loginController.getTenantId() + "&productid=" + loginController.getProductId());
                    menuModel.addElement(item);
                }   break;
            case 3:
                item = new DefaultMenuItem("Cart");
                item.setOutcome("/cloud/Cart/CartList?faces-redirect=true&tenantid=" + loginController.getTenant().getId());
                menuModel.addElement(item);
                item = new DefaultMenuItem("Subscription");
                item.setOutcome("/cloud/Subscription/SubscriptionList");
                menuModel.addElement(item);
                item = new DefaultMenuItem("Products");
                item.setOutcome("/cloud/Product/ProductList");
                menuModel.addElement(item);
                break;
            case 2:
                item = new DefaultMenuItem("Products");
                item.setOutcome("/cloud/Product/ProductList");
                menuModel.addElement(item);
                item = new DefaultMenuItem("Activate");
                item.setOutcome("/cloud/Subscription/SubscriptionActivation");
                menuModel.addElement(item);
                item = new DefaultMenuItem("Deactivate");
                item.setOutcome("/cloud/Subscription/SubscriptionDeactivation");
                menuModel.addElement(item);
                break;
            default:
                break;
        }
}

public MenuModel getMenuModel() {
        return menuModel;
    }

    public void setMenuModel(MenuModel menuModel) {
        this.menuModel = menuModel;
    }

}
