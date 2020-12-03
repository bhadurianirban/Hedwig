/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.service;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.hedwig.tenant.DAO.CartDAO;
import org.hedwig.tenant.DAO.LoginTraceDAO;
import org.hedwig.tenant.DAO.PackageParamDAO;
import org.hedwig.tenant.DAO.PaymentDAO;
import org.hedwig.tenant.DAO.ProdPackageDAO;
import org.hedwig.tenant.DAO.ProductDAO;
import org.hedwig.tenant.DAO.RoleDAO;
import org.hedwig.tenant.DAO.SubscriptionDAO;
import org.hedwig.tenant.DAO.TenantDAO;
import org.hedwig.tenant.DAO.UserDAO;
import org.hedwig.tenant.JPA.exceptions.IllegalOrphanException;
import org.hedwig.tenant.JPA.exceptions.NonexistentEntityException;
import org.hedwig.tenant.JPA.exceptions.PreexistingEntityException;
import org.hedwig.tenant.entities.Cart;
import org.hedwig.tenant.entities.CartPK;
import org.hedwig.tenant.entities.Packageparam;
import org.hedwig.tenant.entities.PackageparamPK;
import org.hedwig.tenant.entities.Payment;
import org.hedwig.tenant.entities.PaymentPK;
import org.hedwig.tenant.entities.Prodpackage;
import org.hedwig.tenant.entities.ProdpackagePK;
import org.hedwig.tenant.entities.Product;
import org.hedwig.tenant.entities.Role;
import org.hedwig.tenant.entities.RolePK;
import org.hedwig.tenant.entities.Subscription;
import org.hedwig.tenant.entities.SubscriptionPK;
import org.hedwig.tenant.entities.Tenant;
import org.hedwig.tenant.entities.User;
import org.hedwig.tenant.entities.UserPK;
import org.hedwig.tenant.entities.Userlogintrace;
import org.hedwig.tenant.entities.UserlogintracePK;
import org.hedwig.cloud.dto.DataConnDTO;
import org.hedwig.cloud.dto.RoleDTO;
import org.hedwig.cloud.dto.TenantDTO;
import org.hedwig.cloud.dto.UserAuthDTO;
import org.hedwig.cloud.response.HedwigResponseCode;
import org.hedwig.cloud.response.HedwigResponseMessage;

/**
 *
 * @author dgrf-iv
 */
public class MasterDataService {

    private EntityManagerFactory emf;

    public MasterDataService() {
        try {
            emf = Persistence.createEntityManagerFactory("org.hedwig_persistence");
        } catch (Exception e) {
            try {
                HashMap<String, String> DBPROPERTIES = new HashMap<>();
                DBPROPERTIES.put("javax.persistence.jdbc.url", "jdbc:mysql://mysqldb:3306/tenant?zeroDateTimeBehavior=CONVERT_TO_NULL");
                emf = Persistence.createEntityManagerFactory("org.hedwig_persistence",DBPROPERTIES);
            } catch (Exception ende) {
                Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ende);
            }
        }
    }

    public List<Product> getProductList() {
        ProductDAO productDAO = new ProductDAO(emf);
        List<Product> products = productDAO.findProductEntities();
        return products;
    }

    public List<Prodpackage> getActivePackageList(int productId) {
        ProductDAO productDAO = new ProductDAO(emf);
        Product product = productDAO.findProduct(productId);
        List<Prodpackage> packages = product.getProdpackageList();
        ArrayList<Prodpackage> prodpackages = new ArrayList<>();
        Date now = new java.util.Date();
        Timestamp current = new java.sql.Timestamp(now.getTime());
        for (Prodpackage pkg : packages) {
            if (current.after(pkg.getStartDate()) && current.before(pkg.getEndDate())) {
                prodpackages.add(pkg);
            }
        }
        return prodpackages;
    }

    public List<Prodpackage> getAllPackageList(int productId) {
        ProductDAO productDAO = new ProductDAO(emf);
        Product product = productDAO.findProduct(productId);
        List<Prodpackage> packages = product.getProdpackageList();

        return packages;
    }

    public Prodpackage getPackageValues(int productId, int packageId) {
        ProdPackageDAO packageDAO = new ProdPackageDAO(emf);
        ProdpackagePK packagePK = new ProdpackagePK(packageId, productId);
        Prodpackage prodpackage = packageDAO.findProdpackage(packagePK);
        return prodpackage;
    }

    public int insertIntoProduct(Product product) {
        ProductDAO productDAO = new ProductDAO(emf);
        try {
            productDAO.create(product);
            return HedwigResponseCode.SUCCESS;
        } catch (PreexistingEntityException e) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, e);
            return HedwigResponseCode.DB_DUPLICATE;
        } catch (Exception ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_SEVERE;
        }
    }

    public int insertIntoPackage(Prodpackage prodpackage, int productId) {
        ProdPackageDAO packageDAO = new ProdPackageDAO(emf);
        ProductDAO productDAO = new ProductDAO(emf);
        ProdpackagePK packagePK = new ProdpackagePK(prodpackage.getProdpackagePK().getId(), productId);
        Product product = productDAO.findProduct(productId);
        Prodpackage pkg = new Prodpackage(packagePK);
        pkg.setName(prodpackage.getName());
        pkg.setDurationDays(prodpackage.getDurationDays());
        pkg.setPrice(prodpackage.getPrice());
        pkg.setStartDate(prodpackage.getStartDate());
        pkg.setEndDate(prodpackage.getEndDate());
        Date now = new java.util.Date();
        Timestamp current = new java.sql.Timestamp(now.getTime());
        pkg.setCreateTime(current);
        pkg.setUpdateTime(current);
        pkg.setProduct(product);
        try {
            packageDAO.create(pkg);
            return HedwigResponseCode.SUCCESS;
        } catch (PreexistingEntityException e) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, e);
            return HedwigResponseCode.DB_DUPLICATE;
        } catch (Exception ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_SEVERE;
        }
    }

    public Product getProduct(int productId) {
        ProductDAO productDAO = new ProductDAO(emf);
        Product product = productDAO.findProduct(productId);
        return product;
    }

    public int editProduct(Product productBeanEdit) {
        ProductDAO productDAO = new ProductDAO(emf);
        try {
            productDAO.edit(productBeanEdit);
            return HedwigResponseCode.SUCCESS;
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_NON_EXISTING;
        } catch (Exception ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_SEVERE;
        }
    }

    public int editPackage(Prodpackage prodpackage) {

        ProdPackageDAO packageDAO = new ProdPackageDAO(emf);
        try {
            packageDAO.edit(prodpackage);
            return HedwigResponseCode.SUCCESS;
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_NON_EXISTING;
        } catch (Exception ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_SEVERE;
        }
    }

    public int deleteProduct(Product selectedProduct) {

        ProductDAO productDAO = new ProductDAO(emf);
        try {
            productDAO.destroy(selectedProduct.getId());
            return HedwigResponseCode.SUCCESS;
        } catch (IllegalOrphanException ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_ILLEGAL_ORPHAN;
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_NON_EXISTING;
        } catch (Exception ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_SEVERE;
        }

    }

    public int insertIntoPayment(Prodpackage paymentPackage, int tenantId) {
        PaymentDAO paymentDAO = new PaymentDAO(emf);
        TenantDAO tenantDAO = new TenantDAO(emf);
        ProdPackageDAO packageDAO = new ProdPackageDAO(emf);
        Payment payment = new Payment();
        ProdpackagePK ppk = new ProdpackagePK(paymentPackage.getProdpackagePK().getId(), paymentPackage.getProdpackagePK().getProductId());
        Prodpackage prodpackage = packageDAO.findProdpackage(ppk);
        Tenant tenant = tenantDAO.findTenant(tenantId);
        int maxPaymenttId = paymentDAO.getMaxPaymentId();
        int paymentId = maxPaymenttId + 1;
        PaymentPK paymentPK = new PaymentPK(paymentId, tenantId, paymentPackage.getProdpackagePK().getId(), paymentPackage.getProdpackagePK().getProductId());
        payment.setPaymentPK(paymentPK);
        payment.setAmount(paymentPackage.getPrice());
        Date now = new java.util.Date();
        Timestamp current = new java.sql.Timestamp(now.getTime());
        payment.setCreateTime(current);
        payment.setUpdateTime(current);
        payment.setTenant(tenant);
        payment.setProdpackage(prodpackage);
        try {
            paymentDAO.create(payment);
            return HedwigResponseCode.SUCCESS;
        } catch (PreexistingEntityException e) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, e);
            return HedwigResponseCode.DB_DUPLICATE;
        } catch (Exception ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_SEVERE;
        }

    }

    public int deactiveSubscriptions(List<Map<String, Object>> selectedSubscriptions) {

        SubscriptionDAO subscriptionDAO = new SubscriptionDAO(emf);
        for (Map<String, Object> subscriptions : selectedSubscriptions) {
            int tenantId = (int) subscriptions.get("tenantId");
            int productId = (int) subscriptions.get("productId");
            SubscriptionPK spk = new SubscriptionPK(tenantId, productId);
            Subscription subscription = subscriptionDAO.findSubscription(spk);
            subscription.setEndDate(subscription.getStartDate());
            try {
                subscriptionDAO.edit(subscription);
            } catch (NonexistentEntityException ex) {
                Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
                return HedwigResponseCode.DB_NON_EXISTING;
            } catch (Exception ex) {
                Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
                return HedwigResponseCode.DB_SEVERE;
            }
        }
        return HedwigResponseCode.SUCCESS;
    }

    public int activeSubscriptions(List<Map<String, Object>> selectedSubscriptions) {

        SubscriptionDAO subscriptionDAO = new SubscriptionDAO(emf);
        ProdPackageDAO packageDAO = new ProdPackageDAO(emf);
        for (Map<String, Object> subscriptions : selectedSubscriptions) {
            int tenantId = (int) subscriptions.get("tenantId");
            int productId = (int) subscriptions.get("productId");
            int packageId = (int) subscriptions.get("packageId");
            SubscriptionPK spk = new SubscriptionPK(tenantId, productId);
            Subscription subscription = subscriptionDAO.findSubscription(spk);
            ProdpackagePK ppk = new ProdpackagePK(packageId, productId);
            Prodpackage prodpackage = packageDAO.findProdpackage(ppk);
            int packageDuration = prodpackage.getDurationDays();
            Date now = new java.util.Date();
            Calendar c = Calendar.getInstance();
            c.setTime(now);
            c.add(Calendar.DATE, packageDuration);
            now = c.getTime();
            Timestamp current = new java.sql.Timestamp(now.getTime());
            subscription.setEndDate(current);
            try {
                subscriptionDAO.edit(subscription);
            } catch (NonexistentEntityException ex) {
                Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
                return HedwigResponseCode.DB_NON_EXISTING;
            } catch (Exception ex) {
                Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
                return HedwigResponseCode.DB_SEVERE;
            }
        }
        return HedwigResponseCode.SUCCESS;
    }

    public int insertIntoSubscription(Prodpackage packageBean, int tenantId) {
        SubscriptionDAO subscriptionDAO = new SubscriptionDAO(emf);
        Subscription subscription = new Subscription();
        TenantDAO tenantDAO = new TenantDAO(emf);
        ProductDAO productDAO = new ProductDAO(emf);
        SubscriptionPK spk = new SubscriptionPK(tenantId, packageBean.getProdpackagePK().getProductId());
        subscription.setSubscriptionPK(spk);
        subscription.setProdpackageId(packageBean.getProdpackagePK().getId());
        Date now = new java.util.Date();
        Timestamp startDate = new java.sql.Timestamp(now.getTime());
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.add(Calendar.DATE, packageBean.getDurationDays());
        now = c.getTime();
        Timestamp current = new java.sql.Timestamp(now.getTime());
        subscription.setStartDate(startDate);
        subscription.setEndDate(startDate); //subscription needs approval from admin
        subscription.setCreateTime(startDate);
        subscription.setUpdateTime(startDate);
        subscription.setDbadminUserId("");
        subscription.setDbadminPassword("");
        subscription.setDbconnUrl("");

        Tenant tenant = tenantDAO.findTenant(tenantId);
        Product product = productDAO.findProduct(packageBean.getProdpackagePK().getProductId());

        subscription.setTenant(tenant);
        subscription.setProduct(product);
        try {
            subscriptionDAO.create(subscription);
            return HedwigResponseCode.SUCCESS;
        } catch (PreexistingEntityException e) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, e);
            return HedwigResponseCode.DB_DUPLICATE;
        } catch (Exception ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_SEVERE;
        }

    }

    public int insertIntoRole(Role role, int roleId, int tenantId, int productId) {
        RoleDAO roleDAO = new RoleDAO(emf);
        SubscriptionDAO subscriptionDAO = new SubscriptionDAO(emf);
        RolePK rolePK = new RolePK(roleId, tenantId, productId);
        role.setRolePK(rolePK);
        role.setName(role.getName());
        SubscriptionPK spk = new SubscriptionPK(tenantId, productId);
        Subscription subscription = subscriptionDAO.findSubscription(spk);
        role.setSubscription(subscription);
        try {
            roleDAO.create(role);
            return HedwigResponseCode.SUCCESS;
        } catch (PreexistingEntityException e) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, e);
            return HedwigResponseCode.DB_DUPLICATE;
        } catch (Exception ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_SEVERE;
        }

    }

    public int insertIntoUser(User user) {
        UserDAO userDAO = new UserDAO(emf);
        RoleDAO roleDAO = new RoleDAO(emf);

        Date now = new java.util.Date();
        Timestamp current = new java.sql.Timestamp(now.getTime());
        user.setWrongAttempt(0);
        user.setCreateTime(current);
        user.setUpdateTime(current);

        RolePK rolePK = new RolePK(user.getUserPK().getRoleId(), user.getUserPK().getTenantId(), user.getUserPK().getProductId());
        Role role = roleDAO.findRole(rolePK);

        user.setRole(role);
        try {
            userDAO.create(user);
            return HedwigResponseCode.SUCCESS;
        } catch (PreexistingEntityException e) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, e);
            return HedwigResponseCode.DB_DUPLICATE;
        } catch (Exception ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_SEVERE;
        }
    }

    public Tenant getTenantValues(int tenantId) {
        TenantDAO tenantDAO = new TenantDAO(emf);
        Tenant tenant = tenantDAO.findTenant(tenantId);
        return tenant;
    }

    public int createTenant(Tenant tenant) {
        TenantDAO tenantDAO = new TenantDAO(emf);
        int maxTenantId = tenantDAO.getMaxTenantId();
        int tenantId = maxTenantId + 1;
        tenant.setId(tenantId);
        Date now = new java.util.Date();
        Timestamp current = new java.sql.Timestamp(now.getTime());
        tenant.setCreateTime(current);
        tenant.setUpdateTime(current);
        try {
            tenantDAO.create(tenant);
            return HedwigResponseCode.SUCCESS;
        } catch (PreexistingEntityException e) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, e);
            return HedwigResponseCode.DB_DUPLICATE;
        } catch (Exception ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_SEVERE;
        }
    }

    public List<Tenant> findTenant(Tenant tenant) {
        TenantDAO tenantDAO = new TenantDAO(emf);
        List<Tenant> tenants = tenantDAO.getTenant(tenant.getEmail(), tenant.getPassword());
        return tenants;
    }

    public int insertIntoCart(int tenantId, int productId, int packageId) {
        CartDAO cartDAO = new CartDAO(emf);
        TenantDAO tenantDAO = new TenantDAO(emf);
        ProdPackageDAO prodPackageDAO = new ProdPackageDAO(emf);
        Tenant tenant = tenantDAO.findTenant(tenantId);
        ProdpackagePK ppk = new ProdpackagePK(packageId, productId);
        Prodpackage prodpackage = prodPackageDAO.findProdpackage(ppk);
        int maxCartId = cartDAO.getMaxCartId();
        int cartId = maxCartId + 1;
        CartPK cartPK = new CartPK(cartId, tenantId, packageId, productId);
        Cart cart = new Cart(cartPK);
        cart.setProdpackage(prodpackage);
        cart.setTenant(tenant);

        try {
            cartDAO.create(cart);
            return HedwigResponseCode.SUCCESS;
        } catch (PreexistingEntityException e) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, e);
            return HedwigResponseCode.DB_DUPLICATE;
        } catch (Exception ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_SEVERE;
        }
    }

    public List<Cart> getCartList(int tenantId) {

        CartDAO cartDAO = new CartDAO(emf);
        List<Cart> carts = cartDAO.findCartEntities();
        List<Cart> cartTenant = carts.stream()
                .filter(u -> u.getCartPK().getTenantId() == tenantId)
                .collect(Collectors.toList());
        return cartTenant;
    }

    public int getExistingPackage(int tenantId, int productId) {
        CartDAO cartDAO = new CartDAO(emf);
        List<Cart> carts = cartDAO.findCart(tenantId, productId);
        return carts.size();
    }

    public void deleteCart(Cart cart) throws NonexistentEntityException {
        CartDAO cartDAO = new CartDAO(emf);
        CartPK cartPK = cart.getCartPK();
        cartDAO.destroy(cartPK);

    }

    public List<Subscription> getSubscriptionList(int tenantId) {
        SubscriptionDAO subscriptionDAO = new SubscriptionDAO(emf);
        List<Subscription> subscriptions = subscriptionDAO.findSubscriptionListByTenantId(tenantId);
        return subscriptions;
    }

    public Subscription getSubscription(int tenantId, int productId) {
        SubscriptionDAO subscriptionDAO = new SubscriptionDAO(emf);
        SubscriptionPK spk = new SubscriptionPK(tenantId, productId);
        Subscription subscription = subscriptionDAO.findSubscription(spk);
        return subscription;
    }

    public int editSubscription(Subscription subscriptionEdit) {
        SubscriptionDAO subscriptionDAO = new SubscriptionDAO(emf);

        try {
            subscriptionDAO.edit(subscriptionEdit);
            return HedwigResponseCode.SUCCESS;
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_NON_EXISTING;
        } catch (Exception ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_SEVERE;
        }
    }

    public RoleDTO getRoleList(RoleDTO roleDTO) {
        SubscriptionDAO subscriptionDAO = new SubscriptionDAO(emf);
        int tenantId = roleDTO.getCloudAuthCredentials().getTenantId();
        int productId = roleDTO.getCloudAuthCredentials().getProductId();
        SubscriptionPK spk = new SubscriptionPK(tenantId, productId);
        Subscription subscription = subscriptionDAO.findSubscription(spk);
        List<Role> roles = subscription.getRoleList();
        Map<String, String> roleMap = new HashMap<>();
        for (Role role : roles) {
            roleMap.put(Integer.toString(role.getRolePK().getId()), role.getName());
        }
        roleDTO.setRoleMap(roleMap);
        return roleDTO;
    }

    public List<Role> getRoleList(int tenantId, int productId) {
        SubscriptionDAO subscriptionDAO = new SubscriptionDAO(emf);
        SubscriptionPK spk = new SubscriptionPK(tenantId, productId);
        Subscription subscription = subscriptionDAO.findSubscription(spk);
        List<Role> roles = subscription.getRoleList();
        return roles;
    }

    public Role getRoleEditValue(int roleId, int tenantId, int productId) {
        RoleDAO roleDAO = new RoleDAO(emf);
        RolePK rolePK = new RolePK(roleId, tenantId, productId);
        Role role = roleDAO.findRole(rolePK);
        return role;
    }

    public int editRole(Role editRole) {
        RoleDAO roleDAO = new RoleDAO(emf);

        try {
            roleDAO.edit(editRole);
            return HedwigResponseCode.SUCCESS;
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_NON_EXISTING;
        } catch (Exception ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_SEVERE;
        }
    }

    public List<User> getUserList(int roleId, int tenantId, int productId) {
        RoleDAO roleDAO = new RoleDAO(emf);
        RolePK rolePK = new RolePK(roleId, tenantId, productId);
        Role role = roleDAO.findRole(rolePK);
        List<User> users = role.getUserList();
        return users;
    }

    public User getUser(String userId, int tenantId, int productId, int roleId) {
        UserDAO userDAO = new UserDAO(emf);
        UserPK userPK = new UserPK(userId, roleId, tenantId, productId);
        User user = userDAO.findUser(userPK);
        return user;
    }

    public int editUser(User userEditBean) {
        UserDAO userDAO = new UserDAO(emf);

        try {
            userDAO.edit(userEditBean);
            return HedwigResponseCode.SUCCESS;
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_NON_EXISTING;
        } catch (Exception ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_SEVERE;
        }
    }

    public int deleteUser(User user) {

        UserDAO userDAO = new UserDAO(emf);
        UserPK userPK = user.getUserPK();
        try {
            userDAO.destroy(userPK);
            return HedwigResponseCode.SUCCESS;
        } catch (IllegalOrphanException ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_ILLEGAL_ORPHAN;
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_NON_EXISTING;
        }
    }

    public int deleteRole(Role role) {

        RoleDAO roleDAO = new RoleDAO(emf);
        RolePK rolePK = role.getRolePK();
        try {
            roleDAO.destroy(rolePK);
            return HedwigResponseCode.SUCCESS;
        } catch (IllegalOrphanException ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_ILLEGAL_ORPHAN;
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_NON_EXISTING;
        }
    }

    public String getProductName(int productId) {
        ProductDAO productDAO = new ProductDAO(emf);
        Product product = productDAO.findProduct(productId);
        return product.getName();
    }

    public String getPackageName(int prodpackageId, int prodpackageProductId) {
        ProdPackageDAO prodPackageDAO = new ProdPackageDAO(emf);
        ProdpackagePK ppk = new ProdpackagePK(prodpackageId, prodpackageProductId);
        Prodpackage prodpackage = prodPackageDAO.findProdpackage(ppk);
        return prodpackage.getName();
    }

    public boolean isEmailExists(String email) {
        TenantDAO tenantDAO = new TenantDAO(emf);
        List<Tenant> tenants = tenantDAO.findTenantByEmail(email);
        if (tenants.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public String getTenantName(int tenantId) {
        TenantDAO tenantDAO = new TenantDAO(emf);
        Tenant tenant = tenantDAO.findTenant(tenantId);
        return tenant.getName();
    }

    public String getRoleName(int roleId, int tenantId, int productId) {
        RoleDAO roleDAO = new RoleDAO(emf);
        RolePK rolePK = new RolePK(roleId, tenantId, productId);
        Role role = roleDAO.findRole(rolePK);
        return role.getName();
    }

    public List<Tenant> getTenantList() {
        TenantDAO tenantDAO = new TenantDAO(emf);
        List<Tenant> tenants = tenantDAO.findTenantEntities();
        return tenants;
    }

//    public List<User> getAuthenUser(int tenantId, int productId, String userId) {
//        UserDAO userDAO = new UserDAO(emf);
//        List<User> users = userDAO.findUser(tenantId, productId, userId);
//        return users;
//    }
    public List<User> getInactiveUserList(int tenantId, int productId) {
        UserDAO userDAO = new UserDAO(emf);
        List<User> users = userDAO.findUserEntities();
        List<User> userForProdTen = users.stream()
                .filter(u -> u.getUserPK().getTenantId() == tenantId)
                .filter(u -> u.getUserPK().getProductId() == productId)
                .filter(u -> u.getActive() == false)
                .collect(Collectors.toList());
        return userForProdTen;
    }

    public int activeUsers(List<User> selectedUsers) {
        UserDAO userDAO = new UserDAO(emf);

        for (User user : selectedUsers) {
            user.setActive(true);
            try {
                userDAO.edit(user);
            } catch (NonexistentEntityException ex) {
                Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
                return HedwigResponseCode.DB_NON_EXISTING;
            } catch (Exception ex) {
                Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
                return HedwigResponseCode.DB_SEVERE;
            }
        }
        return HedwigResponseCode.SUCCESS;
    }

    public List<User> getActiveUserList(int tenantId, int productId) {
        UserDAO userDAO = new UserDAO(emf);
        List<User> users = userDAO.findUserEntities();
        List<User> userForProdTen = users.stream()
                .filter(u -> u.getUserPK().getTenantId() == tenantId)
                .filter(u -> u.getUserPK().getProductId() == productId)
                .filter(u -> u.getActive() == true)
                .filter(u -> u.getUserPK().getRoleId() != 1)
                .collect(Collectors.toList());
        return userForProdTen;
    }

    public int deactiveUsers(List<User> selectedUsers) {
        UserDAO userDAO = new UserDAO(emf);

        for (User user : selectedUsers) {
            user.setActive(false);
            try {
                userDAO.edit(user);
            } catch (NonexistentEntityException ex) {
                Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
                return HedwigResponseCode.DB_NON_EXISTING;
            } catch (Exception ex) {
                Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
                return HedwigResponseCode.DB_SEVERE;
            }
        }
        return HedwigResponseCode.SUCCESS;
    }

    private String getDBName(int tenantId, int productId) {
        String dbName = "yolldb" + String.format("%02d", productId) + String.format("%02d", tenantId);
        return dbName;
    }

    private String readProductDBScript(int productId) {
        ProductDAO productDAO = new ProductDAO(emf);

        Product product = productDAO.findProduct(productId);
        String dbScript = product.getDbCreateSql();
        return dbScript;
    }

    public int createDB(Map<String, Object> subscriptionMap) {
        String jdbcDriver = "com.mysql.cj.jdbc.Driver";
        int tenantId = (int) subscriptionMap.get("tenantId");
        int productId = (int) subscriptionMap.get("productId");
        String connection = "jdbc:mysql://" + subscriptionMap.get("dbconnUrl") + "?user=" + subscriptionMap.get("dbAdminUser") + "&password=" + subscriptionMap.get("dbAdminPassword");
        String dbName = getDBName(tenantId, productId);
        try {
            Class.forName(jdbcDriver);
            Connection conn = DriverManager.getConnection(connection);
            Statement s = conn.createStatement();
            int Result = s.executeUpdate("CREATE DATABASE " + dbName);
            s.executeUpdate("USE " + dbName);
            String dbScriptRaw = readProductDBScript(productId);
            Reader inputString = new StringReader(dbScriptRaw);

            ScriptRunner sr = new ScriptRunner(conn);
            sr.setAutoCommit(true);
            sr.setStopOnError(true);
            sr.runScript(inputString);

            return HedwigResponseCode.SUCCESS;
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_SEVERE;
        }
    }

    public boolean isDBPresent(Map<String, Object> subscriptionMap) {
        String jdbcDriver = "com.mysql.cj.jdbc.Driver";
        boolean dbExists = false;
        int tenantId = (int) subscriptionMap.get("tenantId");
        int productId = (int) subscriptionMap.get("productId");
        String dbName = getDBName(tenantId, productId);
        String connection = "jdbc:mysql://" + subscriptionMap.get("dbconnUrl") + "?user=" + subscriptionMap.get("dbAdminUser") + "&password=" + subscriptionMap.get("dbAdminPassword");
        try {
            Class.forName(jdbcDriver);
            Connection conn = DriverManager.getConnection(connection);
            Statement s = conn.createStatement();
            String checkDbQuery = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" + dbName + "'";
            ResultSet rs = s.executeQuery(checkDbQuery);
            while (rs.next()) {
                dbExists = true;
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dbExists;
    }

    public int dropSchema(Map<String, Object> subscriptionMap) {
        String jdbcDriver = "com.mysql.cj.jdbc.Driver";
        int tenantId = (int) subscriptionMap.get("tenantId");
        int productId = (int) subscriptionMap.get("productId");
        String dbName = getDBName(tenantId, productId);
        String connection = "jdbc:mysql://" + subscriptionMap.get("dbconnUrl") + "?user=" + subscriptionMap.get("dbAdminUser") + "&password=" + subscriptionMap.get("dbAdminPassword");
        try {
            Class.forName(jdbcDriver);
            Connection conn = DriverManager.getConnection(connection);
            Statement s = conn.createStatement();
            String dropDbQuery = "DROP DATABASE " + dbName;
            s.executeUpdate(dropDbQuery);

            return HedwigResponseCode.SUCCESS;
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_SEVERE;
        }
    }

    public UserAuthDTO authenticateUser(UserAuthDTO userAuthDTO) {
        UserDAO userDAO = new UserDAO(emf);
        List<User> users = userDAO.findUser(userAuthDTO.getTenantId(), userAuthDTO.getProductId(), userAuthDTO.getUserId());

        //Checking for valid user
        if (users.isEmpty()) {
            userAuthDTO.setResponseCode(HedwigResponseCode.NO_USER);
            HedwigResponseMessage responseMessage = new HedwigResponseMessage();
            userAuthDTO.setResponseMessage(responseMessage.getResponseMessage(HedwigResponseCode.NO_USER));
            return userAuthDTO;
        }
        User user = users.get(0);

        //Checking whether subscription is present
        Subscription subscription = getSubscription(user.getUserPK().getTenantId(), user.getUserPK().getProductId());
        Date currentDate = new Date();
        if (!(currentDate.after(subscription.getStartDate()) && currentDate.before(subscription.getEndDate()))) {
            userAuthDTO.setResponseCode(HedwigResponseCode.NO_SUBCRIPTION);
            HedwigResponseMessage responseMessage = new HedwigResponseMessage();
            userAuthDTO.setResponseMessage(responseMessage.getResponseMessage(HedwigResponseCode.NO_SUBCRIPTION));
            return userAuthDTO;
        }

        //checking whether user is active or not
        if (!user.getActive()) {
            userAuthDTO.setResponseCode(HedwigResponseCode.USER_INACTIVE);
            HedwigResponseMessage responseMessage = new HedwigResponseMessage();
            userAuthDTO.setResponseMessage(responseMessage.getResponseMessage(HedwigResponseCode.USER_INACTIVE));
            return userAuthDTO;
        }

        //checking whether the given password is correct or not
        if (!user.getPassword().equals(userAuthDTO.getPassword())) {
            int noOfWrongAttempts = user.getWrongAttempt();
            Packageparam packageparam = getPackageParam(subscription.getProdpackageId(), subscription.getSubscriptionPK().getProductId(), "wrongattemptno");
            int maxNoOfIncorrectPassword = Integer.valueOf(packageparam.getParamValue());
            if (noOfWrongAttempts < maxNoOfIncorrectPassword) {
                noOfWrongAttempts++;
                user.setWrongAttempt(noOfWrongAttempts);
                editUser(user);
                userAuthDTO.setResponseCode(HedwigResponseCode.PASSWORD_INCORRECT);
                HedwigResponseMessage responseMessage = new HedwigResponseMessage();
                userAuthDTO.setResponseMessage(responseMessage.getResponseMessage(HedwigResponseCode.PASSWORD_INCORRECT));
            } else {
                user.setActive(false);
                user.setWrongAttempt(0);
                editUser(user);
                userAuthDTO.setResponseCode(HedwigResponseCode.WRONG_ATTEMPTS_EXCEED);
                HedwigResponseMessage responseMessage = new HedwigResponseMessage();
                userAuthDTO.setResponseMessage(responseMessage.getResponseMessage(HedwigResponseCode.WRONG_ATTEMPTS_EXCEED));
            }
            return userAuthDTO;
        }

        String dbName = getDBName(userAuthDTO.getTenantId(), userAuthDTO.getProductId());
        String dbConnUrl = "jdbc:mysql://" + subscription.getDbconnUrl() + "/" + dbName + "?zeroDateTimeBehavior=CONVERT_TO_NULL";
        HashMap<String, Object> subscriptionMap = new HashMap<>();
        subscriptionMap.put("dbconnUrl", subscription.getDbconnUrl());
        subscriptionMap.put("dbAdminUser", subscription.getDbadminUserId());
        subscriptionMap.put("dbAdminPassword", subscription.getDbadminPassword());
        subscriptionMap.put("tenantId", userAuthDTO.getTenantId());
        subscriptionMap.put("productId", userAuthDTO.getProductId());

        //checking whether DB is present or not
        if (!isDBPresent(subscriptionMap)) {
            userAuthDTO.setResponseCode(HedwigResponseCode.DATA_CONN_ERROR);
            HedwigResponseMessage responseMessage = new HedwigResponseMessage();
            userAuthDTO.setResponseMessage(responseMessage.getResponseMessage(HedwigResponseCode.DATA_CONN_ERROR));
            return userAuthDTO;
        }
        //checking for DB size
        BigDecimal dbsize = getDBsize(dbName);
        Packageparam packageparam = getPackageParam(subscription.getProdpackageId(), subscription.getSubscriptionPK().getProductId(), "dbsize");
        BigDecimal paramValue = new BigDecimal(packageparam.getParamValue());
        if (paramValue.compareTo(dbsize) < 0) {
            userAuthDTO.setResponseCode(HedwigResponseCode.DB_SIZE_EXCEED);
            HedwigResponseMessage responseMessage = new HedwigResponseMessage();
            userAuthDTO.setResponseMessage(responseMessage.getResponseMessage(HedwigResponseCode.DB_SIZE_EXCEED));
            return userAuthDTO;
        }
        user.setWrongAttempt(0);
        editUser(user);
        //insert user details into login trace table
        insertIntoLoginTrace(user);
        List<Userlogintrace> userlogintraces = getLoginTraceList(user);
        int loginAttemptNo = userlogintraces.size();
        Packageparam param = getPackageParam(subscription.getProdpackageId(), subscription.getSubscriptionPK().getProductId(), "traceduserno");
        int maxNoOfTracedUsers = Integer.valueOf(param.getParamValue());
        if (loginAttemptNo > maxNoOfTracedUsers) {
            Userlogintrace userlogintrace = userlogintraces.get(0);
            deleteUserTrace(userlogintrace);
        } else {

        }

        userAuthDTO.setResponseCode(HedwigResponseCode.SUCCESS);
        HedwigResponseMessage responseMessage = new HedwigResponseMessage();
        userAuthDTO.setResponseMessage(responseMessage.getResponseMessage(HedwigResponseCode.SUCCESS));
        userAuthDTO.setName(user.getName());
        userAuthDTO.setRoleId(user.getRole().getRolePK().getId());
        userAuthDTO.setRoleName(user.getRole().getName());
        userAuthDTO.setDbConnUrl(dbConnUrl);
        userAuthDTO.setDbAdminUser(subscription.getDbadminUserId());
        userAuthDTO.setDbAdminPassword(subscription.getDbadminPassword());
        return userAuthDTO;
    }

    private BigDecimal getDBsize(String dbName) {
        EntityManager em = emf.createEntityManager();
        String nativeQueryString = "SELECT table_schema 'DB Name', "
                + "ROUND(SUM(data_length + index_length) / 1024 / 1024, 1) 'DB Size in MB' "
                + "FROM information_schema.tables where table_schema='" + dbName + "'";
        Query query = em.createNativeQuery(nativeQueryString);
        BigDecimal dbsize = null;
        List<Object[]> results = query.getResultList();
        for (Object[] obj : results) {
            String dbname = (String) obj[0];
            dbsize = (BigDecimal) (obj[1]);

        }
        return dbsize;
    }

    public boolean isConnValid(HashMap<String, Object> subscriptionMap) {
        String jdbcDriver = "com.mysql.cj.jdbc.Driver";
        String connection = "jdbc:mysql://" + subscriptionMap.get("dbconnUrl") + "?user=" + subscriptionMap.get("dbAdminUser") + "&password=" + subscriptionMap.get("dbAdminPassword");
        try {
            Class.forName(jdbcDriver);
            Connection conn = DriverManager.getConnection(connection);
            conn.close();
            return true;
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /*returns list of active tenants with schema created for the products*/
    public List<TenantDTO> getTenantListOfProduct(int productId) {
        SubscriptionDAO subscriptionDAO = new SubscriptionDAO(emf);
        TenantDAO tenantDAO = new TenantDAO(emf);
        List<Subscription> subscriptions = subscriptionDAO.findSubscriptionEntities();
        List<Subscription> subscriptionsByProduct = subscriptions.stream()
                .filter(u -> u.getSubscriptionPK().getProductId() == productId).collect(Collectors.toList());
        List<TenantDTO> tenantDTOs = new ArrayList<>();

        for (Subscription subscription : subscriptionsByProduct) {
            TenantDTO tenantDTO = new TenantDTO();
            Tenant tenant = tenantDAO.findTenant(subscription.getSubscriptionPK().getTenantId());
            tenantDTO.setTenantId(tenant.getId());
            tenantDTO.setProductId(productId);
            tenantDTO.setName(tenant.getName());
            tenantDTO.setEmailId(tenant.getEmail());
            tenantDTO.setPhone(tenant.getPhone());
            tenantDTO.setPassword(tenant.getPassword());
            HashMap<String, Object> subscriptionMap = new HashMap<>();
            subscriptionMap.put("dbconnUrl", subscription.getDbconnUrl());
            subscriptionMap.put("dbAdminUser", subscription.getDbadminUserId());
            subscriptionMap.put("dbAdminPassword", subscription.getDbadminPassword());
            subscriptionMap.put("tenantId", subscription.getSubscriptionPK().getTenantId());
            subscriptionMap.put("productId", subscription.getSubscriptionPK().getProductId());
            //insert code for active subscription
            if (!subscription.getStartDate().equals(subscription.getEndDate())) {
                if (isDBPresent(subscriptionMap)) {
                    tenantDTOs.add(tenantDTO);
                }
            }

        }
        return tenantDTOs;
    }

    public DataConnDTO getDataConn(int tenantID, int productID) {
        Subscription subscription = getSubscription(tenantID, productID);
        DataConnDTO dataConnDTO = new DataConnDTO();
        if (subscription == null) {
            dataConnDTO.setDbAdminPassword("");
            dataConnDTO.setDbAdminUser("");
            dataConnDTO.setDbConnUrl("");
        } else {
            String dbName = getDBName(tenantID, productID);
            String dbConnUrl = "jdbc:mysql://" + subscription.getDbconnUrl() + "/" + dbName + "?zeroDateTimeBehavior=CONVERT_TO_NULL";

            dataConnDTO.setDbAdminPassword(subscription.getDbadminPassword());
            dataConnDTO.setDbAdminUser(subscription.getDbadminUserId());
            dataConnDTO.setDbConnUrl(dbConnUrl);
        }
        return dataConnDTO;

    }

    public List<Subscription> getSubscriptionList() {
        SubscriptionDAO subscriptionDAO = new SubscriptionDAO(emf);
        List<Subscription> subscriptions = subscriptionDAO.findSubscriptionEntities();
        return subscriptions;
    }

    public List<Packageparam> getAllParamList(int productId, int packageId) {
        ProdPackageDAO prodPackageDAO = new ProdPackageDAO(emf);
        ProdpackagePK ppk = new ProdpackagePK(packageId, productId);
        Prodpackage prodpackage = prodPackageDAO.findProdpackage(ppk);
        List<Packageparam> packageparams = prodpackage.getPackageparamList();
        return packageparams;
    }

    public int insertIntoParam(Packageparam packageparamAdd) {
        PackageParamDAO packageParamDAO = new PackageParamDAO(emf);
        ProdPackageDAO packageDAO = new ProdPackageDAO(emf);
        ProdpackagePK ppk = new ProdpackagePK(packageparamAdd.getPackageparamPK().getProdpackageId(), packageparamAdd.getPackageparamPK().getProductId());
        Prodpackage prodpackage = packageDAO.findProdpackage(ppk);
        packageparamAdd.setProdpackage(prodpackage);
        try {
            packageParamDAO.create(packageparamAdd);
            return HedwigResponseCode.SUCCESS;
        } catch (PreexistingEntityException e) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, e);
            return HedwigResponseCode.DB_DUPLICATE;
        } catch (Exception ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_SEVERE;
        }
    }

    public Packageparam getParamValues(int productId, int packageId, String paramKey) {
        PackageParamDAO packageParamDAO = new PackageParamDAO(emf);
        PackageparamPK packageparamPK = new PackageparamPK(packageId, productId, paramKey);
        Packageparam packageparam = packageParamDAO.findPackageparam(packageparamPK);
        return packageparam;
    }

    public int editParam(Packageparam packageparamEdit) {

        PackageParamDAO packageParamDAO = new PackageParamDAO(emf);
        try {
            packageParamDAO.edit(packageparamEdit);
            return HedwigResponseCode.SUCCESS;
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_NON_EXISTING;
        } catch (Exception ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_SEVERE;
        }
    }

    public int deleteParam(Packageparam packageparam) {

        PackageParamDAO packageParamDAO = new PackageParamDAO(emf);
        PackageparamPK packageparamPK = packageparam.getPackageparamPK();
        try {
            packageParamDAO.destroy(packageparamPK);
            return HedwigResponseCode.SUCCESS;
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_NON_EXISTING;
        } catch (Exception ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return HedwigResponseCode.DB_SEVERE;
        }
    }

    public boolean isKeyExists(String paramKey) {
        PackageParamDAO packageParamDAO = new PackageParamDAO(emf);
        List<Packageparam> packageparams = packageParamDAO.findPackageparamEntities();
        List<Packageparam> paramListByKey = packageparams.stream()
                .filter(u -> u.getPackageparamPK().getParamKey().equals(paramKey)).collect(Collectors.toList());
        return !paramListByKey.isEmpty();
    }

    public Packageparam getPackageParam(int prodpackageId, int productId, String paramKey) {
        PackageParamDAO packageParamDAO = new PackageParamDAO(emf);
        PackageparamPK packageparamPK = new PackageparamPK(prodpackageId, productId, paramKey);
        Packageparam packageparam = packageParamDAO.findPackageparam(packageparamPK);
        return packageparam;
    }

    public List<User> getUserListByTenantIdProductId(int tenantId, int productId) {
        UserDAO userDAO = new UserDAO(emf);
        List<User> users = userDAO.findUserEntities();
        List<User> usersByTenantIdProductId = users.stream()
                .filter(u -> u.getUserPK().getTenantId() == tenantId)
                .filter(u -> u.getUserPK().getProductId() == productId)
                .collect(Collectors.toList());
        return usersByTenantIdProductId;
    }

    private void insertIntoLoginTrace(User user) {
        LoginTraceDAO loginTraceDAO = new LoginTraceDAO(emf);
        Date now = new java.util.Date();
        Timestamp current = new java.sql.Timestamp(now.getTime());
        UserlogintracePK userlogintracePK = new UserlogintracePK(current, user.getUserPK().getId(), user.getUserPK().getRoleId(), user.getUserPK().getTenantId(), user.getUserPK().getProductId());
        Userlogintrace userlogintrace = new Userlogintrace(userlogintracePK);
        userlogintrace.setUser(user);
        try {
            loginTraceDAO.create(userlogintrace);
        } catch (Exception ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void deleteUserTrace(Userlogintrace userlogintrace) {
        LoginTraceDAO loginTraceDAO = new LoginTraceDAO(emf);
        UserlogintracePK upk = userlogintrace.getUserlogintracePK();
        try {
            loginTraceDAO.destroy(upk);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<Userlogintrace> getLoginTraceList(User user) {
        LoginTraceDAO loginTraceDAO = new LoginTraceDAO(emf);
        List<Userlogintrace> userlogintraces = loginTraceDAO.findLoggedinUserList(user);
        return userlogintraces;
    }

}
