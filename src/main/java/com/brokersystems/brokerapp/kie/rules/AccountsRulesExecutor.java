package com.brokersystems.brokerapp.kie.rules;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.ErrorUtils;
import com.brokersystems.brokerapp.setup.model.AccountDef;
import com.brokersystems.brokerapp.setup.model.ClientDef;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by HP on 10/7/2017.
 */
@Component
public class AccountsRulesExecutor {

    @Autowired
    @Qualifier(value = "accountKieServices")
    private KieContainer servicesBean;

    @Autowired
    private ErrorUtils errorUtils;

    public void handleAccountChecks(AccountDef accountDef) throws BadRequestException {
        KieSession sessionBean = servicesBean.newKieSession();
        errorUtils.clearErrors();
        sessionBean.setGlobal("errorUtils", errorUtils);
        sessionBean.insert(accountDef);
        sessionBean.fireAllRules();
        if(errorUtils.getErrors().size() > 0){
            String errors =String.join("<br>",errorUtils.getErrors());
            throw new BadRequestException(errors);
        }


    }
}
