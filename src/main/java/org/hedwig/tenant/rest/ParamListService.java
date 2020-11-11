/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
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
import org.hedwig.cloud.dto.ParamDTO;
import org.hedwig.tenant.entities.Packageparam;
import org.hedwig.tenant.entities.Subscription;
import org.hedwig.tenant.service.MasterDataService;

/**
 *
 * @author dgrfiv
 */
@Path("paramlist")
@Named(value = "paramListService")
@RequestScoped
public class ParamListService {

    @Context
    private UriInfo context;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getParamList(@QueryParam("productID") int productID, @QueryParam("tenantID") int tenantID) {
        MasterDataService mds =  new MasterDataService();
        ObjectMapper objectMapper = new ObjectMapper();
        Subscription subscription = mds.getSubscription(tenantID, productID);
        List<Packageparam> packageparams = mds.getAllParamList(productID, subscription.getProdpackageId());
        List<ParamDTO> paramDTOs = new ArrayList<>();
        for(Packageparam packageparam : packageparams) {
            ParamDTO paramDTO = new ParamDTO();
            
            paramDTO.setPackageId(packageparam.getPackageparamPK().getProdpackageId());
            paramDTO.setProductId(packageparam.getPackageparamPK().getProductId());
            paramDTO.setParamKey(packageparam.getPackageparamPK().getParamKey());
            paramDTO.setParamDescription(packageparam.getParamDescription());
            paramDTO.setParamValue(packageparam.getParamValue());
            
            paramDTOs.add(paramDTO);
        }
        String paramDTOListJSON;
        try {
            paramDTOListJSON = objectMapper.writeValueAsString(paramDTOs);
            return  paramDTOListJSON;
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ParamListService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
