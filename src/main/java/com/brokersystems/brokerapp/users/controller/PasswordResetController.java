package com.brokersystems.brokerapp.users.controller;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.setup.service.ParamService;
import com.brokersystems.brokerapp.setup.service.UserService;
import com.brokersystems.brokerapp.users.model.PasswordResetDto;
import com.brokersystems.brokerapp.users.model.PasswordResetToken;
import com.brokersystems.brokerapp.users.model.QPasswordResetToken;
import com.brokersystems.brokerapp.users.repository.PasswordResetTokenRepo;
import com.brokersystems.brokerapp.users.utils.PasswordValidatorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.StringTokenizer;

/**
 * Created by HP on 4/26/2018.
 */
@Controller
@RequestMapping("/reset-password")
public class PasswordResetController {

    @Autowired
    private PasswordResetTokenRepo passwordResetTokenRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordValidatorUtils passwordValidatorUtils;

    @ModelAttribute("passwordResetForm")
    public PasswordResetDto passwordReset() {
        return new PasswordResetDto();
    }

    @RequestMapping(method={RequestMethod.GET})
    public String displayResetPasswordPage(@RequestParam(required = false) String token,
                                           Model model) {

        PasswordResetToken resetToken = passwordResetTokenRepo.findOne(QPasswordResetToken.passwordResetToken.token.eq(token));
        if (resetToken == null){
            model.addAttribute("error", "Could not find password reset token.");
        } else if (resetToken.isExpired()){
            model.addAttribute("error", "Token has expired, please request a new password reset.");
        } else {
            model.addAttribute("token", resetToken.getToken());
        }

        return "reset-password";
    }

    @RequestMapping(method={RequestMethod.POST})
    @Transactional
    public String handlePasswordReset(@ModelAttribute("passwordResetForm") @Valid PasswordResetDto form,
                                      BindingResult result,
                                      RedirectAttributes redirectAttributes) {

        if (result.hasErrors()){
            redirectAttributes.addFlashAttribute(BindingResult.class.getName() + ".passwordResetForm", result);
            redirectAttributes.addFlashAttribute("passwordResetForm", form);
            return "redirect:/reset-password?token=" + form.getToken();
        }

        if(!form.getPassword().equals(form.getConfirmPassword())){
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            redirectAttributes.addFlashAttribute("passwordResetForm", form);
            return "redirect:/reset-password?token=" + form.getToken();
        }
        if (form.getToken() == null){
            redirectAttributes.addFlashAttribute("error", "Token has expired....");
            redirectAttributes.addFlashAttribute("passwordResetForm", form);
            return "redirect:/reset-password?token=" + form.getToken();
        }

        PasswordResetToken token = passwordResetTokenRepo.findOne(QPasswordResetToken.passwordResetToken.token.eq(form.getToken()));
        if (token.isExpired()){
            redirectAttributes.addFlashAttribute("error", "Token has expired, please request a new password reset.");
            redirectAttributes.addFlashAttribute("passwordResetForm", form);
            return "redirect:/reset-password?token=" + form.getToken();
        }
        User user = token.getUser();
        if(!passwordValidatorUtils.validatePassword(form.getPassword())){
            redirectAttributes.addFlashAttribute("error", "Unable to reset your password.Your password should contain numbers capital letters small letters and special characters..If this persists contact system administrator.");
            redirectAttributes.addFlashAttribute("passwordResetForm", form);
            return "redirect:/reset-password?token=" + form.getToken();
        }
        String updatedPassword = passwordEncoder.encode(form.getPassword());
        userService.resetPassword(updatedPassword, user.getId());
        passwordResetTokenRepo.delete(token);
        return "redirect:/login";
    }



}
