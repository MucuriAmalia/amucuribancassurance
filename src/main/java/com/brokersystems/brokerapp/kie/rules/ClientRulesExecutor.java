package com.brokersystems.brokerapp.kie.rules;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.ErrorUtils;
import com.brokersystems.brokerapp.setup.model.ClientDef;
import com.brokersystems.brokerapp.setup.model.ProspectDef;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by HP on 10/3/2017.
 */
@Component
public class ClientRulesExecutor {


    @Autowired
    @Qualifier(value = "clientKieServices")
    private KieContainer servicesBean;

    @Autowired
    @Qualifier(value = "prospectKieServices")
    private KieContainer clientServiceBean;

    @Autowired
    private ErrorUtils errorUtils;

    public String validateClientChecks(ClientDef clientDef){
        KieSession sessionBean = servicesBean.newKieSession();
        errorUtils.clearErrors();
        sessionBean.setGlobal("errorUtils", errorUtils);
        sessionBean.insert(clientDef);
        sessionBean.fireAllRules();
        System.out.println(errorUtils.getErrors());
        if(errorUtils.getErrors().size() > 0){
            String errors =String.join("<br>",errorUtils.getErrors());
            return errors;
        }
       return null;

    }


    public void handleClientChecks(ClientDef clientDef) throws BadRequestException{
        KieSession sessionBean = servicesBean.newKieSession();
        errorUtils.clearErrors();
        sessionBean.setGlobal("errorUtils", errorUtils);
        sessionBean.insert(clientDef);
        sessionBean.fireAllRules();
        System.out.println(errorUtils.getErrors());
        if(errorUtils.getErrors().size() > 0){
            String errors =String.join("<br>",errorUtils.getErrors());
            throw new BadRequestException(errors);
        }


    }

    public void handleProspectChecks(ProspectDef clientDef) throws BadRequestException{
        KieSession sessionBean = clientServiceBean.newKieSession();
        errorUtils.clearErrors();
        sessionBean.setGlobal("errorUtils", errorUtils);
        sessionBean.insert(clientDef);
        sessionBean.fireAllRules();
        System.out.println(errorUtils.getErrors());
        if(errorUtils.getErrors().size() > 0){
            String errors =String.join("<br>",errorUtils.getErrors());
            throw new BadRequestException(errors);
        }


    }


}
