package com.shopme.admin.user;

import com.shopme.admin.file.FileUploadUtils;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@AllArgsConstructor
public class AccountController {

    private final UserService userService;

    @GetMapping("/account-details")
    public String viewAccountDetails(@AuthenticationPrincipal ShopmeUserDetails loggedUser,
                                     Model model) {
        String email = loggedUser.getUsername();
        User user = userService.getUserByEmail(email);
        List<Role> roleList = userService.getAllRoles();

        model.addAttribute("user", user);
        model.addAttribute("roles", roleList);

        return "/users/account-details.html";
    }

    @PostMapping("/account-details/update")
    public String handleAccountDetailsForm(@Valid User user,
                                           BindingResult bindingResult,
                                           Model model,
                                           RedirectAttributes ra,
                                           @RequestParam("image") MultipartFile multipartFile,
                                           @AuthenticationPrincipal ShopmeUserDetails loggedUser) throws Exception {
        boolean isUserHasPhoto = !user.getPhoto().isEmpty();

        // Check User's information
        if(bindingResult.hasErrors() || !userService.checkPasswordMatch(user)) {
            List<Role> roleList = userService.getAllRoles();

            model.addAttribute("user", user);
            model.addAttribute("roles", roleList);

            if(!userService.checkPasswordMatch(user)) {
                model.addAttribute("passwordMatchError", true);
            }
            return "/users/account-details.html";
        }

        /*
        * Handle 'multipartFile'
            - multipartFile is null(User don't change the photo)
            - multipartFile is not null(User change the photo)
        */
        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhoto(fileName);

            String uploadDir = "user-photos/" + user.getId();

            // We need to clean upload directory before save file image
            if(isUserHasPhoto) {
                FileUploadUtils.cleanDir(uploadDir);
            }
            FileUploadUtils.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if(user.getPhoto() == null || user.getPhoto().isBlank())
                user.setPhoto(null);
        }

        userService.updateAccountDetails(user);
        loggedUser.setFullName(user.getFirstName(), user.getLastName());
        ra.addFlashAttribute("message", "Your account has been updated!");
        return "redirect:/account-details";
    }
}
