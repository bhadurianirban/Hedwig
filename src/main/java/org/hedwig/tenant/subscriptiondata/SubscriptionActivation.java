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
import org.hedwig.cloud.response.HedwigResponseCode;
import org.hedwig.cloud.response.HedwigResponseMessage;
import org.hedwig.tenant.entities.Subscription;
import org.hedwig.tenant.service.MasterDataService;

/**
 *
 * @author dgrf
 */
@Named(value = "subscriptionActivation")
@ViewScoped
public class SubscriptionActivation implements Serializable {

    private List<Subscription> subscriptions;
    private List<Map<String, Object>> subscriptionActiveMapList;
    private List<Map<String, Object>> subscriptionNonctiveMapList;
    private List<Map<String, Object>> selectedSubscriptions;

    public SubscriptionActivation() {
    }

    public void fillInactiveSubscriptionValues() {
        MasterDataService mds = new MasterDataService();
        subscriptions = mds.getSubscriptionList();
        subscriptionActiveMapList = new ArrayList<>();
        subscriptionNonctiveMapList = new ArrayList<>();
        for (Subscription subscription : subscriptions) {

            HashMap<String, Object> subscriptionMap = new HashMap<>();
            if (subscription.getStartDate().equals(subscription.getEndDate())) {
                subscriptionMap.put("productId", subscription.getSubscriptionPK().getProductId());
                subscriptionMap.put("tenantId", subscription.getSubscriptionPK().getTenantId());
                subscriptionMap.put("packageId", subscription.getProdpackageId());
                subscriptionMap.put("startDate", subscription.getStartDate());
                subscriptionMap.put("endDate", subscription.getEndDate());
                subscriptionMap.put("dbAdminUser", subscription.getDbadminUserId());
                subscriptionMap.put("dbAdminPassword", subscription.getDbadminPassword());
                subscriptionMap.put("dbconnUrl", subscription.getDbconnUrl());
                String productName = mds.getProductName(subscription.getSubscriptionPK().getProductId());
                subscriptionMap.put("productName", productName);
                String tenantName = mds.getTenantName(subscription.getSubscriptionPK().getTenantId());
                subscriptionMap.put("tenantName", tenantName);

                subscriptionNonctiveMapList.add(subscriptionMap);
            } else {
                subscriptionMap.put("productId", subscription.getSubscriptionPK().getProductId());
                subscriptionMap.put("tenantId", subscription.getSubscriptionPK().getTenantId());
                subscriptionMap.put("startDate", subscription.getStartDate());
                subscriptionMap.put("endDate", subscription.getEndDate());
                subscriptionMap.put("dbAdminUser", subscription.getDbadminUserId());
                subscriptionMap.put("dbAdminPassword", subscription.getDbadminPassword());
                subscriptionMap.put("dbconnUrl", subscription.getDbconnUrl());
                String productName = mds.getProductName(subscription.getSubscriptionPK().getProductId());
                subscriptionMap.put("productName", productName);
                String tenantName = mds.getTenantName(subscription.getSubscriptionPK().getTenantId());
                subscriptionMap.put("tenantName", tenantName);

                subscriptionActiveMapList.add(subscriptionMap);
            }
        }
    }

    public String makeSubscriptionActive() {
        MasterDataService mds = new MasterDataService();
        int response = mds.activeSubscriptions(selectedSubscriptions);
        HedwigResponseMessage responseMessage = new HedwigResponseMessage();
        FacesMessage message;

        if (response != HedwigResponseCode.SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(response));
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(response));
        }
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);
        return "SubscriptionActivation";
    }

    public String makeSubscriptionDeactive() {
        MasterDataService mds = new MasterDataService();
        int response = mds.deactiveSubscriptions(selectedSubscriptions);
        HedwigResponseMessage responseMessage = new HedwigResponseMessage();
        FacesMessage message;

        if (response != HedwigResponseCode.SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(response));
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(response));
        }
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);
        return "SubscriptionDeactivation";
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<Map<String, Object>> getSelectedSubscriptions() {
        return selectedSubscriptions;
    }

    public void setSelectedSubscriptions(List<Map<String, Object>> selectedSubscriptions) {
        this.selectedSubscriptions = selectedSubscriptions;
    }

    public List<Map<String, Object>> getSubscriptionActiveMapList() {
        return subscriptionActiveMapList;
    }

    public void setSubscriptionActiveMapList(List<Map<String, Object>> subscriptionActiveMapList) {
        this.subscriptionActiveMapList = subscriptionActiveMapList;
    }

    public List<Map<String, Object>> getSubscriptionNonctiveMapList() {
        return subscriptionNonctiveMapList;
    }

    public void setSubscriptionNonctiveMapList(List<Map<String, Object>> subscriptionNonctiveMapList) {
        this.subscriptionNonctiveMapList = subscriptionNonctiveMapList;
    }

}
