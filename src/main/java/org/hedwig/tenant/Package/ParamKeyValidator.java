/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hedwig.tenant.Package;

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
 * @author dgrf
 */
@FacesValidator("custom.paramKeyValidator")
public class ParamKeyValidator implements Validator, ClientValidator{
    private static final String KEY_PATTERN = "[a-zA-Z]+";
    private Pattern pattern;
    
    public ParamKeyValidator() {
        pattern = Pattern.compile(KEY_PATTERN);
    }
    
    @Override
    public void validate(FacesContext fc, UIComponent uic, Object o) throws ValidatorException {
        if (o == null) {
            return;
        }
        if(!pattern.matcher(o.toString()).matches()) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data entry error","Parameter key should not contain spaces;"));
        }
//        MasterDataService mds = new MasterDataService();
//        String paramKey = (String)o;
//        boolean keyExists = mds.isKeyExists(paramKey);
//        if (keyExists) {
//            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Duplicate Key",paramKey + " is already present;"));
//        }
    }

    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }

    @Override
    public String getValidatorId() {
        return null;
    }
    
}
