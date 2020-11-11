/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.Tenant;

import java.util.Map;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.primefaces.validate.ClientValidator;

/**
 *
 * @author dgrf-iv
 */
@FacesValidator("custom.tenantContactValidator")
public class TenantContactValidator implements Validator, ClientValidator{
    private Pattern pattern;
    private static final String CONTACT_PATTERN = "^[0-9]{10}$";
    
    public TenantContactValidator() {
        pattern = Pattern.compile(CONTACT_PATTERN);
    }
    @Override
    public void validate(FacesContext fc, UIComponent uic, Object obj) throws ValidatorException {
        if(obj == null) {
            return;
        }
         
        if(!pattern.matcher(obj.toString()).matches()) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sorry, ", obj + " is not a valid phone number."));
        }
    }

    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }

    @Override
    public String getValidatorId() {
        return "custom.tenantContactValidator";
    }
    
}
