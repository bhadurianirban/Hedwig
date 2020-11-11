/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.MediaType;
import org.hedwig.tenant.service.MasterDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import org.hedwig.cloud.dto.DataConnDTO;

/**
 * REST Web Service
 *
 * @author bhaduri
 */
@Path("dataconn")
@RequestScoped
public class DataConnService {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of DataConnService
     */
    public DataConnService() {
    }

    /**
     * Retrieves representation of an instance of
     * org.dgrf.dgrftenant.rest.DataConnService
     *
     * @param dataConnDTOJSON
     * @return an instance of java.lang.String
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getDataConn(String dataConnDTOJSON) {
        ObjectMapper objectMapper = new ObjectMapper();
        DataConnDTO dataConnDTO;
        try {
            dataConnDTO = objectMapper.readValue(dataConnDTOJSON, DataConnDTO.class);
            int tenantID, productID;
            tenantID = dataConnDTO.getCloudAuthCredentials().getTenantId();
            productID = dataConnDTO.getCloudAuthCredentials().getProductId();
            MasterDataService mds = new MasterDataService();
            dataConnDTO = mds.getDataConn(tenantID, productID);
        } catch (IOException ex) {
            Logger.getLogger(DataConnService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        String respDataConnDTOJSON;
        try {
            respDataConnDTOJSON = objectMapper.writeValueAsString(dataConnDTO);
            return respDataConnDTOJSON;
        } catch (JsonProcessingException ex) {
            Logger.getLogger(DataConnService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

}
