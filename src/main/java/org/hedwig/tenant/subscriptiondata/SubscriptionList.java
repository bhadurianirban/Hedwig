/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.subscriptiondata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.hedwig.cloud.response.HedwigResponseCode;
import org.hedwig.cloud.response.HedwigResponseMessage;

import org.hedwig.tenant.entities.Subscription;
import org.hedwig.tenant.login.LoginController;
import org.hedwig.tenant.service.MasterDataService;

/**
 *
 * @author dgrf-iv
 */
@Named(value = "subscriptionList")
@ViewScoped
public class SubscriptionList implements Serializable {

    private boolean dbExists;
    private boolean connExists;
    private List<Subscription> subscriptions;
    private List<Map<String, Object>> subscriptionMapList;
    private Map<String, String> selectedSubscription;

    @Inject
    LoginController loginController;

    public SubscriptionList() {
    }

    public void fillSubscriptionValues() {
        MasterDataService mds = new MasterDataService();
        subscriptions = mds.getSubscriptionList(loginController.getTenant().getId());
        subscriptionMapList = new ArrayList<>();
        for (Subscription subscription : subscriptions) {

            HashMap<String, Object> subscriptionMap = new HashMap<>();
            subscriptionMap.put("productId", subscription.getSubscriptionPK().getProductId());
            subscriptionMap.put("tenantId", subscription.getSubscriptionPK().getTenantId());
            subscriptionMap.put("startDate", subscription.getStartDate());
            subscriptionMap.put("endDate", subscription.getEndDate());
            subscriptionMap.put("dbAdminUser", subscription.getDbadminUserId());
            subscriptionMap.put("dbAdminPassword", subscription.getDbadminPassword());
            subscriptionMap.put("dbconnUrl", subscription.getDbconnUrl());
            String productName = mds.getProductName(subscription.getSubscriptionPK().getProductId());
            subscriptionMap.put("productName", productName);

            if (subscription.getStartDate().equals(subscription.getEndDate())) {
                subscriptionMap.put("approval", "false");
            } else {
                subscriptionMap.put("approval", "true");
            }

            connExists = mds.isConnValid(subscriptionMap);
            if (connExists) {
                dbExists = mds.isDBPresent(subscriptionMap);
                if (dbExists) {
                    subscriptionMap.put("dbExists", 2);
                } else {
                    subscriptionMap.put("dbExists", 3);
                }
            } else {
                subscriptionMap.put("dbExists", 1);
            }

            subscriptionMapList.add(subscriptionMap);
        }

    }

    public String goToCreateDB(Map<String, Object> subscriptionMap) {
        MasterDataService mds = new MasterDataService();
        int response = mds.createDB(subscriptionMap);
        HedwigResponseMessage responseMessage = new HedwigResponseMessage();
        FacesMessage message = null;

        if (response != HedwigResponseCode.SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(response));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Created", responseMessage.getResponseMessage(response));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
        }

        String redirectUrl = "SubscriptionList";
        return redirectUrl;
    }

    public String goToDropSchema(Map<String, Object> subscriptionMap) {
        MasterDataService mds = new MasterDataService();
        int response = mds.dropSchema(subscriptionMap);
        HedwigResponseMessage responseMessage = new HedwigResponseMessage();
        FacesMessage message = null;

        if (response != HedwigResponseCode.SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(response));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Dropped", responseMessage.getResponseMessage(response));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
        }

        String redirectUrl = "SubscriptionList";
        return redirectUrl;
    }

    public String goToEditSubscription() {
        return "SubscriptionEdit";
    }

    public String goToAddRole() {
        return "RoleList";
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<Map<String, Object>> getSubscriptionMapList() {
        return subscriptionMapList;
    }

    public void setSubscriptionMapList(List<Map<String, Object>> subscriptionMapList) {
        this.subscriptionMapList = subscriptionMapList;
    }

    public boolean isDbExists() {
        return dbExists;
    }

    public void setDbExists(boolean dbExists) {
        this.dbExists = dbExists;
    }

    public boolean isConnExists() {
        return connExists;
    }

    public void setConnExists(boolean connExists) {
        this.connExists = connExists;
    }

    public Map<String, String> getSelectedSubscription() {
        return selectedSubscription;
    }

    public void setSelectedSubscription(Map<String, String> selectedSubscription) {
        this.selectedSubscription = selectedSubscription;
    }

}
