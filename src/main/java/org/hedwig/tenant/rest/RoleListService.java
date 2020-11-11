/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.hedwig.cloud.dto.DataConnDTO;
import org.hedwig.cloud.dto.RoleDTO;
import org.hedwig.tenant.entities.Role;
import org.hedwig.tenant.service.MasterDataService;

/**
 * REST Web Service
 *
 * @author bhaduri
 */
@Path("rolelist")
@RequestScoped
public class RoleListService {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of DataConnService
     */
    

    /**
     * Retrieves representation of an instance of org.dgrf.dgrftenant.rest.DataConnService
     * @param roleDTOJSON
     * @return an instance of java.lang.String
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getRoleList(String roleDTOJSON) {
        RoleDTO roleDTO;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            roleDTO = objectMapper.readValue(roleDTOJSON, RoleDTO.class);
            int tenantID, productID;
            tenantID = roleDTO.getCloudAuthCredentials().getTenantId();
            productID = roleDTO.getCloudAuthCredentials().getProductId();
            MasterDataService mds = new MasterDataService();
            roleDTO = mds.getRoleList(roleDTO);
        } catch (IOException ex) {
            Logger.getLogger(DataConnService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        String respRoleDTOJSON;
        try {
            respRoleDTOJSON = objectMapper.writeValueAsString(roleDTO);
            return respRoleDTOJSON;
        } catch (JsonProcessingException ex) {
            Logger.getLogger(RoleListService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
    }


}
