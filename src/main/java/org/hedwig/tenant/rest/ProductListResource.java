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
import java.util.stream.Collectors;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.MediaType;
import org.hedwig.cloud.dto.ProductDTO;
import org.hedwig.tenant.entities.Product;
import org.hedwig.tenant.service.MasterDataService;

/**
 * REST Web Service
 *
 * @author dgrfv
 */
@Path("productlist")
@RequestScoped
public class ProductListResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ProductListResource
     */
    public ProductListResource() {
    }

    /**
     * Retrieves representation of an instance of org.dgrf.dgrftenant.rest.ProductListResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getProductList() {
        MasterDataService mds =  new MasterDataService();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Product> products = mds.getProductList();
        List<ProductDTO> productDTOList = products.stream().map(p-> {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductId(p.getId());
            productDTO.setProductName(p.getName());
            productDTO.setProductURL(p.getProducturl());
            productDTO.setProductDescription(p.getDescription());
            return productDTO;
        }).collect(Collectors.toList());
        String productDTOListJSON=null;
        try {
            productDTOListJSON = objectMapper.writeValueAsString(productDTOList);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ProductListResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return productDTOListJSON;
    }

}
