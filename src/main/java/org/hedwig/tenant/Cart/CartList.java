/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.Cart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.hedwig.tenant.JPA.exceptions.NonexistentEntityException;
import org.hedwig.tenant.entities.Cart;
import org.hedwig.tenant.service.MasterDataService;

/**
 *
 * @author dgrf-iv
 */
@Named(value = "cartList")
@ViewScoped
public class CartList implements Serializable {

    private int tenantId;
    private List<Cart> carts;
    private List<Map<String, Object>> cartMapList;
    private Map<String, String> selectedCart;

    public CartList() {
    }

    public void fillCartValues() {
        MasterDataService mds = new MasterDataService();
        carts = mds.getCartList(tenantId);
        cartMapList = new ArrayList<>();
        for (Cart cart : carts) {

            HashMap<String, Object> cartMap = new HashMap<>();
            cartMap.put("cartId", cart.getCartPK().getId());
            cartMap.put("tenantId", cart.getCartPK().getTenantId());
            cartMap.put("packageId", cart.getCartPK().getProdpackageId());
            cartMap.put("productId", cart.getCartPK().getProductId());
            String productName = mds.getProductName(cart.getCartPK().getProductId());
            cartMap.put("productName", productName);
            String packageName = mds.getPackageName(cart.getCartPK().getProdpackageId(), cart.getCartPK().getProductId());
            cartMap.put("packageName", packageName);

            cartMapList.add(cartMap);
        }
    }

    public String goToViewCart() {
        return "CartView";
    }

    public String goToBuyPackage() {
        return "MakePayment";
    }

    public String goToDeleteCart(Map<String, Object> cart) throws NonexistentEntityException {

        MasterDataService mds = new MasterDataService();
        int cartId = (int) cart.get("cartId");
        int tenantid = (int) cart.get("tenantId");
        int packageId = (int) cart.get("packageId");
        int productId = (int) cart.get("productId");
        Cart deleteCartBean = new Cart(cartId, tenantid, packageId, productId);
        mds.deleteCart(deleteCartBean);

        String redirectUrl = "Landing";
        return redirectUrl;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public List<Cart> getCarts() {
        return carts;
    }

    public void setCarts(List<Cart> carts) {
        this.carts = carts;
    }

    public List<Map<String, Object>> getCartMapList() {
        return cartMapList;
    }

    public void setCartMapList(List<Map<String, Object>> cartMapList) {
        this.cartMapList = cartMapList;
    }

    public Map<String, String> getSelectedCart() {
        return selectedCart;
    }

    public void setSelectedCart(Map<String, String> selectedCart) {
        this.selectedCart = selectedCart;
    }

}
