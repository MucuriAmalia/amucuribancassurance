package com.brokersystems.brokerapp.nav.controllers;


import com.brokersystems.brokerapp.claims.exception.ClaimException;
import com.brokersystems.brokerapp.claims.model.ClaimForm;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.server.utils.HolidayUtils;
import com.brokersystems.brokerapp.server.utils.TemplateMerger;
import com.brokersystems.brokerapp.setup.dto.OrganizationDTO;
import com.brokersystems.brokerapp.setup.model.Organization;
import com.brokersystems.brokerapp.setup.service.OrganizationService;
import com.brokersystems.brokerapp.setup.service.UserService;
import com.brokersystems.brokerapp.users.dto.ChangePasswordDTO;
import com.brokersystems.brokerapp.users.model.PasswordResetDto;
import com.brokersystems.brokerapp.users.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal ;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.brokersystems.brokerapp.setup.model.User;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
public class AuthController {

	
    private final static String DEFAULT = "index";

    @Autowired
    private OrganizationService orgService;

    @Autowired
    private UserService userService;


    @Autowired
    private TemplateMerger templateMerger;

    @ModelAttribute("changePasswordDTO")
    public ChangePasswordDTO passwordReset() {
        return new ChangePasswordDTO();
    }
    
    @RequestMapping("login")
    public String login(@RequestParam(required = false) Boolean error, ModelMap model) throws IOException {
        model.addAttribute("error", error);
        // Check if today is a holiday or weekend
        LocalDate today = LocalDate.now();
        boolean isHoliday = HolidayUtils.isHolidayOrWeekend(today);
        model.addAttribute("isHoliday", isHoliday);
        return "login";
    }

    @RequestMapping("changepass")
    public String changePassword()  {
        return "changepassword";
    }

    @RequestMapping(value = { "resetPassword" }, method = {
            RequestMethod.POST })
    @ResponseBody
    public void resetPassword(UserDto userDto, HttpServletRequest request) throws BadRequestException {
        templateMerger.sendEmail(userDto,request);
    }
  

    @RequestMapping({"index.html", "index", ""})
    public String index(@AuthenticationPrincipal User user) throws IOException {
        if (user != null) {
            return "redirect:protected/home";
        }
        return DEFAULT;
    }

    @RequestMapping(value = "changePassword",method = RequestMethod.POST)
    public ModelAndView changePassword(@ModelAttribute("changePasswordDTO") ChangePasswordDTO changePassword, BindingResult result, RedirectAttributes redirectAttrs, HttpServletRequest request) throws BadRequestException {
        try{
            final String changePass = userService.changePassword(changePassword);
            request.setAttribute("changePass", changePass);
        }
        catch (BadRequestException ex){
            redirectAttrs.addFlashAttribute("username",changePassword.getUsername());
            redirectAttrs.addFlashAttribute("error", ex.getMessage());
            redirectAttrs.addFlashAttribute("error", ex.getMessage());
            redirectAttrs.addFlashAttribute("changePasswordDTO", changePassword);
            return new ModelAndView("redirect:changepass");
        }
        return new ModelAndView("login");
    }

    @RequestMapping(value = "/logo")
    public void getImage(HttpServletResponse response) throws IOException,ServletException {
        OrganizationDTO organization = orgService.getOrganizationLogoDetails();
        if(organization.getOrgLogo()!=null && Files.exists(Paths.get(organization.getOrgLogo()))) {
            response.setContentType(organization.getOrgLogoContentType());
            response.getOutputStream().write(Files.readAllBytes(Paths.get(organization.getOrgLogo())));
            response.getOutputStream().close();
        }
    }


}
