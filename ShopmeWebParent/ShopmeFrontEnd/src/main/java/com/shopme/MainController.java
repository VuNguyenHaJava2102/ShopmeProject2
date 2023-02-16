package com.shopme;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class MainController {

    private final CategoryService categoryService;

    @GetMapping("/")
    public String viewHomePage(Model model) {
        List<Category> showedCategoryList = categoryService.getAllCategoryHasNoChildren();

        model.addAttribute("categories", showedCategoryList);

        return "index.html";
    }

    @GetMapping("/login")
    public String viewLoginForm() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login.html";
        }
        return "redirect:/";
    }

}
