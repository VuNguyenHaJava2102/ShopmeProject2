package com.shopme.customer;

import com.shopme.common.classes.EmailSettingBag;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.setting.SettingService;
import com.shopme.utility.MailHelper;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
@AllArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final SettingService settingService;

    // 1
    @GetMapping("/register")
    public String viewRegistrationForm(Model model) {
        List<Country> countryList = customerService.getAllCountries();

        model.addAttribute("countries", countryList);
        model.addAttribute("customer", new Customer());

        return "registration/registration-form.html";
    }

    // 2
    @PostMapping("/create-customer")
    public String handleRegistrationForm(@Valid Customer customer,
                                         BindingResult bindingResult,
                                         HttpServletRequest servletRequest,
                                         Model model)
            throws MessagingException, UnsupportedEncodingException {
        if(bindingResult.hasErrors()) {
            List<Country> countryList = customerService.getAllCountries();

            model.addAttribute("countries", countryList);
            model.addAttribute("customer", customer);
            return "registration/registration-form.html";
        }
        customerService.createCustomer(customer);
        sendVerificationUrl(servletRequest, customer);
        return "registration/register-succeed.html";
    }

    // 2.1
    private void sendVerificationUrl(HttpServletRequest servletRequest,
                                     Customer customer)
            throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettingBag = settingService.getEmailSettingBag();
        JavaMailSenderImpl mailSender = MailHelper.prepareMailSender(emailSettingBag);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        // Get email content template then replace customer's name and verification URL:
        String emailContent = emailSettingBag.getCustomerVerificationContent();
        String customerName = customer.getFullName();
        String verificationUrl = MailHelper.getSiteUrl(servletRequest) + "/verify?code=" + customer.getVerificationCode();

        emailContent = emailContent.replace("[[customerName]]", customerName);
        emailContent = emailContent.replace("[[URL]]", verificationUrl);

        // Set sender, receiver, subject and content for Email
        mimeMessageHelper.setFrom(emailSettingBag.getFromAddress(), emailSettingBag.getSenderName());
        mimeMessageHelper.setTo(customer.getEmail());
        mimeMessageHelper.setSubject(emailSettingBag.getCustomerVerificationSubject());
        mimeMessageHelper.setText(emailContent, false);

        mailSender.send(mimeMessage);
    }

    // 3
    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("code") String code) {
        boolean verifyResult = customerService.verifyAccount(code);
        return verifyResult ? "registration/verify-succeed.html" : "registration/verify-fail.html";
    }
}
