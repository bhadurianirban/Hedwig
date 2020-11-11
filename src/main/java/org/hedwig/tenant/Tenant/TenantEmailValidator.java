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
import org.hedwig.tenant.service.MasterDataService;
import org.primefaces.validate.ClientValidator;

/**
 *
 * @author dgrf-iv
 */
@FacesValidator("custom.tenantEmailValidator")
public class TenantEmailValidator implements Validator, ClientValidator {
    private Pattern pattern;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public TenantEmailValidator(){
        pattern = Pattern.compile(EMAIL_PATTERN);
    }
    @Override
    public void validate(FacesContext fc, UIComponent uic, Object value) throws ValidatorException {
        if(value == null) {
            return;
        }
         
        if(!pattern.matcher(value.toString()).matches()) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Sorry, ", value + " is not a valid email;"));
        }
        MasterDataService mds = new MasterDataService();
        String email = String.valueOf(value);
        boolean emailExists = mds.isEmailExists(email);
        if (emailExists) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Duplicate Email" , email + " is already present;"));
        }
    }

    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }

    @Override
    public String getValidatorId() {
        return "custom.tenantEmailValidator";
    }

}
