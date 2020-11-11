/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.hedwig.cloud.dto.TenantDTO;
import org.hedwig.tenant.service.MasterDataService;

/**
 *
 * @author dgrf-iv
 */
@Named(value = "tenantListService")
@Path("tenantlist")
@RequestScoped
public class TenantListService {

    @Context
    private UriInfo context;
    
    public TenantListService() {
    }
    

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    //@Consumes(MediaType.APPLICATION_XML)
    
    public String tenantListByProductId(@QueryParam("productID") int productID){
        MasterDataService mds =  new MasterDataService();
        ObjectMapper objectMapper = new ObjectMapper();
        List<TenantDTO> tenantDTOs = mds.getTenantListOfProduct(productID);
        String tenantDTOJSON;
        try {
            tenantDTOJSON = objectMapper.writeValueAsString(tenantDTOs);
            return tenantDTOJSON;
        } catch (JsonProcessingException ex) {
            Logger.getLogger(DataConnService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
    }

}
