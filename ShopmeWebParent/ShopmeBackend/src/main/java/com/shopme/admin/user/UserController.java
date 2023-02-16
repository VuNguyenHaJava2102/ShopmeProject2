package com.shopme.admin.user;

import com.shopme.admin.file.FileUploadUtils;
import com.shopme.admin.user.exporter.UserCSVExporter;
import com.shopme.admin.user.exporter.UserExcelExporter;
import com.shopme.admin.user.exporter.UserPdfExporter;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
@AllArgsConstructor
@Slf4j
public class UserController {

    private UserService userService;

    @GetMapping("/users")
    public String viewAllUsers(Model model) {
        return viewUsersByPage(1, "firstName", "asc", null, model);
    }

    @GetMapping("/users/page/{pageNum}")
    public String viewUsersByPage(@PathVariable("pageNum") int pageNum,
                                  @RequestParam("sortField") String sortField,
                                  @RequestParam("sortDir") String sortDir,
                                  @RequestParam("keyword") String keyword,
                                  Model model) {
        if(keyword == null) {
            keyword = "";
        }

        // Data from service layer
        Page<User> userPage = userService.getUsersByPage(pageNum, sortField, sortDir, keyword);

        // Initialize data
        List<User> userList = userPage.getContent();
        int totalElements = (int) userPage.getTotalElements();
        int totalPages = userPage.getTotalPages();

        int startCount = (pageNum - 1) * UserService.NUMBER_OF_USERS_PER_PAGE + 1;
        int endCount = startCount + UserService.NUMBER_OF_USERS_PER_PAGE - 1;
        if(endCount > totalElements) {
            endCount = totalElements;
        }

        // Send data to view
        model.addAttribute("users", userList);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("reversedSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "/users/users.html";
    }

    @GetMapping("/users/new")
    public String viewCreateUserForm(Model model) {
        User user = new User();
        user.setEnabled(true); // => By default user is enabled
        List<Role> roleList = userService.getAllRoles();

        model.addAttribute("user", user);
        model.addAttribute("roles", roleList);
        model.addAttribute("pageTitle", "Create New User");

        return "/users/user-form.html";
    }

    @PostMapping("/users/save")
    public String handleCreateUserForm(@Valid User user,
                                       BindingResult bindingResult,
                                       Model model,
                                       RedirectAttributes ra,
                                       @RequestParam("image") MultipartFile multipartFile) throws IOException {
        boolean isUserHasPhoto = !user.getPhoto().isEmpty();

        // Check User's information
        if(bindingResult.hasErrors() || user.getRoles().size() == 0 || !userService.checkUniqueEmail(user, user.getEmail())) {
            List<Role> roleList = userService.getAllRoles();

            model.addAttribute("user", user);
            model.addAttribute("roles", roleList);

            if(user.getRoles().size() == 0) {
                model.addAttribute("rolesValidation", true);
            }

            if(!userService.checkUniqueEmail(user, user.getEmail())) {
                model.addAttribute("uniqueEmailValidation", true);
            }

            return "/users/user-form.html";
        }

        /*
        * Handle 'multipartFile'
            - Create user
                + multipartFile is null
                + multipartFile is not null
            - Edit user
                + multipartFile is null
                + multipartFile is not null
        */
        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhoto(fileName);
            User savedUser = userService.saveUser(user);

            String uploadDir = "user-photos/" + savedUser.getId();

            // We need to clean upload directory before save file image
            if(isUserHasPhoto) {
                FileUploadUtils.cleanDir(uploadDir);
            }
            FileUploadUtils.saveFile(uploadDir, fileName, multipartFile);
            ra.addFlashAttribute("message", "Save user ID " + savedUser.getFormattedId() + " successfully!");
        } else {
            if(user.getPhoto() == null || user.getPhoto().isBlank())
                user.setPhoto(null);

            User savedUser = userService.saveUser(user);
            ra.addFlashAttribute("message", "Save user ID " + savedUser.getFormattedId() + " successfully!");
        }

        // We redirect to page that contain only saved user:
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + user.getEmail();
    }

    @GetMapping("/users/edit/{id}")
    public String viewEditUserForm(@PathVariable("id") int id,
                                   RedirectAttributes ra,
                                   Model model) {

        try {
            User user = userService.getUserById(id);
            List<Role> roleList = userService.getAllRoles();

            model.addAttribute("user", user);
            model.addAttribute("roles", roleList);
            model.addAttribute("pageTitle", "Edit User (ID: " + user.getFormattedId() + ")");

            return "/users/user-form.html";
        } catch(UserNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") int id,
                             RedirectAttributes ra) {
        try {
            userService.deleteUserById(id);
            ra.addFlashAttribute("message", "Delete user ID " + id + " successfully!");
        } catch(UserNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/users/update-status/{id}")
    public String updateUserStatus(@PathVariable("id") int id,
                                   RedirectAttributes ra) {
        try {
            String status = userService.updateUserStatus(id) ? "enabled" : "disabled";
            ra.addFlashAttribute("message", "User ID " + id + " has been " + status + " successfully!");
        } catch(UserNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/users/export/csv")
    public void exportCSV(HttpServletResponse response) throws IOException{
        List<User> userList = userService.getAllUsers();
        UserCSVExporter exporter = new UserCSVExporter();
        exporter.export(userList, response);
    }

    @GetMapping("/users/export/excel")
    public void exportExcel(HttpServletResponse response) throws IOException{
        List<User> userList = userService.getAllUsers();
        UserExcelExporter exporter = new UserExcelExporter();
        exporter.export(userList, response);
    }

    @GetMapping("/users/export/pdf")
    public void exportPDF(HttpServletResponse response) throws IOException{
        List<User> userList = userService.getAllUsers();
        UserPdfExporter exporter = new UserPdfExporter();
        exporter.export(userList, response);
    }

}
