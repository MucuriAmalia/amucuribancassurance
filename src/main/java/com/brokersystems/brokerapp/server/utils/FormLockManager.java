package com.brokersystems.brokerapp.server.utils;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.users.model.UserRole;
import com.brokersystems.brokerapp.users.repository.UserRolesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FormLockManager {
    private final ConcurrentHashMap<String,String> lockedForms = new ConcurrentHashMap<>();
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private UserRolesRepo userRolesRepo;

    private String generateKey(String formName,String formId) {
        return String.format("%s-%s", formName, formId);
    }

    public void openForm(String formName, String formId) throws BadRequestException {
        String key = generateKey(formName,formId);
        String currentUser = userUtils.getCurrentUser().getUsername();
        if (hasBypassRole(userUtils.getCurrentUser())) {
            // Skip lock check
            lockedForms.put(key, currentUser);
            return;
        }


        if (lockedForms.containsKey(key) && !lockedForms.get(key).equals(currentUser)) {
            throw new BadRequestException(String.format("Form is currently being accessed by %s",lockedForms.get(key)));
        }
        lockedForms.put(key,currentUser);
    }
    private boolean hasBypassRole(User currentUser) {
        List<UserRole> roles = userRolesRepo.findByUserId(currentUser.getId());
        for (UserRole userRole : roles) {
            Long roleId = userRole.getRoles().getRoleId();
            if (roleId == 34788L || roleId == 22358L) {
                return true;
            }
        }
        return false;
    }


    public void closeForm(String formName, String formId) {
        String key = generateKey(formName, formId);
        String currentUser = userUtils.getCurrentUser().getUsername();
        if (lockedForms.containsKey(key)) {
            String owner = lockedForms.get(key);
            if (owner.equals(currentUser)) {
                lockedForms.remove(key);
            }
        }
    }

    public void closeAllFormsForUser(String username) {
        lockedForms.entrySet().removeIf(entry -> entry.getValue().equals(username));
    }

}
