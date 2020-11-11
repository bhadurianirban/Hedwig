/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;

import javax.ws.rs.core.MediaType;
import org.hedwig.cloud.dto.UserAuthDTO;
import org.hedwig.tenant.service.MasterDataService;

/**
 * REST Web Service
 *
 * @author bhaduri
 */
@Path("userauth")
@RequestScoped
public class UserAuthService {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of UserAuthService
     */
    public UserAuthService() {
    }

    @POST
    //@Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String authenticate (String userAuthDTOJSON) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserAuthDTO userAuthDTO = objectMapper.readValue(userAuthDTOJSON, UserAuthDTO.class);
            MasterDataService mds =  new MasterDataService();
            
            userAuthDTO = mds.authenticateUser(userAuthDTO);
            userAuthDTOJSON = objectMapper.writeValueAsString(userAuthDTO);
            return userAuthDTOJSON;
        } catch (IOException ex) {
            Logger.getLogger(UserAuthService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
