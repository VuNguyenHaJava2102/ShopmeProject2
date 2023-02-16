package com.shopme.admin.category;

import com.shopme.admin.file.FileUploadUtils;
import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;
import lombok.AllArgsConstructor;
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

import javax.validation.Valid;
import java.util.List;

@Controller
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public String viewFirstCategoryPage(Model model) {
        return viewCategoriesByPage(1, "asc", null, model);
    }

    @GetMapping("/categories/page/{pageNum}")
    public String viewCategoriesByPage(@PathVariable("pageNum") int pageNum,
                                       @RequestParam("sortDir") String sortDir,
                                       @RequestParam("keyword") String keyword,
                                       Model model) {
        if(keyword == null) {
            keyword = "";
        }

        CategoryPageInfo pageInfo = new CategoryPageInfo();
        List<Category> categoryList = categoryService.getAllCategories(pageNum, sortDir, keyword, pageInfo);

        int startCount = (pageNum - 1) * CategoryService.NUMBER_OF_ROOT_CATEGORIES_PER_PAGE + 1;
        int endCount = startCount + CategoryService.NUMBER_OF_ROOT_CATEGORIES_PER_PAGE - 1;
        if(endCount > pageInfo.getTotalElements()) {
            endCount = (int) pageInfo.getTotalElements();
        }

        model.addAttribute("categories", categoryList);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reversedSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("totalElements", pageInfo.getTotalElements());
        model.addAttribute("totalPages", pageInfo.getTotalPages());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("keyword", keyword);

        return "/categories/categories.html";
    }

    @GetMapping("/categories/new")
    public String viewCreateCategoryForm(Model model) {
        Category category = new Category();
        List<Category> categoryList = categoryService.getCategoriesUsedInForm();

        model.addAttribute("category", category);
        model.addAttribute("categories", categoryList);
        model.addAttribute("pageTitle", "Create New Category");

        return "/categories/category-form.html";
    }

    @PostMapping("/categories/save")
    public String handleCategoryForm(@Valid Category category,
                                     BindingResult bindingResult,
                                     @RequestParam("imageFile") MultipartFile multipartFile,
                                     Model model,
                                     RedirectAttributes ra) throws Exception {
        boolean isEditMode = (category.getId() != null);
        boolean isImageValid = false;

        if(!multipartFile.isEmpty() || isEditMode) {
            isImageValid = true;
        }

        // Check category's information
        if(bindingResult.hasErrors()|| !isImageValid ||
                categoryService.checkNameDuplicated(category.getId(), category.getName()) ||
                categoryService.checkAliasDuplicated(category.getId(), category.getName())) {
            List<Category> categoryList = categoryService.getCategoriesUsedInForm();

            if(!isImageValid) {
                model.addAttribute("imageValidation", true);
            }

            if(categoryService.checkNameDuplicated(category.getId(), category.getName())) {
                model.addAttribute("isNameUnique", true);
            }

            if(categoryService.checkAliasDuplicated(category.getId(), category.getAlias())) {
                model.addAttribute("isAliasUnique", true);
            }

            model.addAttribute("category", category);
            model.addAttribute("categories", categoryList);
            model.addAttribute("pageTitle", "Create New Category");

            return "/categories/category-form.html";
        }

        /*
        Handle multipartFile
        - Create new category
            + multipartFile is not empty
        - Create new category
            + multipartFile is empty(Image doesn't change)
            + multipartFile is not empty(Image changes)
         */
        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            category.setImage(fileName);
            Category savedCategory = categoryService.saveCategory(category);

            String uploadDir = "u/category-images/" + savedCategory.getId();
            if(isEditMode) {
                FileUploadUtils.cleanDir(uploadDir);
            }
            FileUploadUtils.saveFile(uploadDir, fileName, multipartFile);
        } else {
            categoryService.saveCategory(category);
        }

        ra.addFlashAttribute("message", "Save category successfully!");
        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String viewEditCategoryForm(@PathVariable("id") int id,
                                       Model model,
                                       RedirectAttributes ra) {
        try {
            Category category = categoryService.getCategoryById(id);
            List<Category> categoryList = categoryService.getCategoriesUsedInForm();
            System.err.println(category.getName());
            category.setName(category.getName().replaceAll("-", "")); // Stupid error

            model.addAttribute("category", category);
            model.addAttribute("categories", categoryList);
            model.addAttribute("pageTitle", "Edit Category(ID: " + id + ")");

            return "/categories/category-form.html";
        } catch(CategoryNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/categories";
        }
    }

    @GetMapping("/categories/update-status/{id}")
    public String updateCategoryStatus(@PathVariable("id") int id,
                                       RedirectAttributes ra) {
        try {
            categoryService.updateCategoryStatus(id);
            ra.addFlashAttribute("message", "Update status of category ID " + id + " successfully!");
        } catch(CategoryNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") int id,
                                 RedirectAttributes ra) {
        try {
            categoryService.deleteCategory(id);
            ra.addFlashAttribute("message", "Delete category ID " + id + " successfully!");
        } catch(CategoryNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/categories";
    }

}
