/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.rest;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author bhaduri
 */
@javax.ws.rs.ApplicationPath("rest")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(org.hedwig.tenant.rest.BheuResource.class);
        resources.add(org.hedwig.tenant.rest.DataConnService.class);
        resources.add(org.hedwig.tenant.rest.ParamListService.class);
        resources.add(org.hedwig.tenant.rest.ProductListResource.class);
        resources.add(org.hedwig.tenant.rest.RoleListService.class);
        resources.add(org.hedwig.tenant.rest.TenantListService.class);
        resources.add(org.hedwig.tenant.rest.UserAuthService.class);
    }
    
}
