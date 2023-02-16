package com.shopme.admin.brand;

import com.shopme.admin.category.CategoryService;
import com.shopme.admin.file.FileUploadUtils;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import lombok.AllArgsConstructor;
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

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
@AllArgsConstructor
public class BrandController {

    private final BrandService brandService;
    private final CategoryService categoryService;

    @GetMapping("/brands")
    public String viewFirstBrandPage(Model model) {
        return viewBrandsByPage(1, "asc", null, model);
    }

    @GetMapping("/brands/page/{id}")
    public String viewBrandsByPage(@PathVariable("id") int pageNum,
                                   @RequestParam("sortDir") String sortDir,
                                   @RequestParam("keyword") String keyword,
                                   Model model) {
        if(keyword == null) keyword = "";

        Page<Brand> brandPage = brandService.getBrandsByPage(pageNum, sortDir, keyword);

        List<Brand> brandList = brandPage.getContent();
        long totalElements = brandPage.getTotalElements();
        int totalPages = brandPage.getTotalPages();
        int startCount = (pageNum - 1) * BrandService.NUMBER_OF_BRANDS_PER_PAGE + 1;
        int endCount = startCount + BrandService.NUMBER_OF_BRANDS_PER_PAGE - 1;
        if(endCount > totalElements) {
            endCount = (int) totalElements;
        }

        model.addAttribute("brands", brandList);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("reversedSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        return "/brands/brands.html";
    }

    @GetMapping("/brands/new")
    public String viewCreateBrandForm(Model model) {
        Brand brand = new Brand();
        List<Category> categoryList = categoryService.getCategoriesUsedInForm();

        model.addAttribute("brand", brand);
        model.addAttribute("categories", categoryList);
        model.addAttribute("pageTitle", "Create New Brand");

        return "/brands/brand-form.html";
    }

    @PostMapping("/brands/save")
    public String handleBrandForm(@Valid Brand brand,
                                  BindingResult bindingResult,
                                  @RequestParam("imageFile") MultipartFile multipartFile,
                                  Model model,
                                  RedirectAttributes ra) throws IOException {
        boolean isEditMode = (brand.getId() != null);
        boolean isLogoValid = false;
        boolean isCategoryChosen = (brand.getCategories().size() != 0);

        if(isEditMode || !multipartFile.isEmpty()) {
            isLogoValid = true;
        }

        if(bindingResult.hasErrors() || !isLogoValid
                || brandService.checkNameDuplicated(brand.getId(), brand.getName())
                || !isCategoryChosen) {
            List<Category> categoryList = categoryService.getCategoriesUsedInForm();

            model.addAttribute("brand", brand);
            model.addAttribute("categories", categoryList);
            model.addAttribute("pageTitle", "Create New Brand");

            if(brandService.checkNameDuplicated(brand.getId(), brand.getName())) {
                model.addAttribute("duplicatedError", true);
            }

            if(!isLogoValid) {
                model.addAttribute("logoError", true);
            }

            if(!isCategoryChosen) {
                model.addAttribute("categoryError", true);
            }

            return "/brands/brand-form.html";
        }

        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            brand.setLogo(fileName);

            Brand savedBrand = brandService.saveBrand(brand);
            String uploadDir = "u/brand-logos/" + savedBrand.getId();
            if(isEditMode) {
                FileUploadUtils.cleanDir(uploadDir);
            }
            FileUploadUtils.saveFile(uploadDir, fileName, multipartFile);
        } else {
            brandService.saveBrand(brand);
        }

        ra.addFlashAttribute("message", "Save brand successfully!");
        return "redirect:/brands";
    }

    @GetMapping("/brands/edit/{id}")
    public String viewEditBrandForm(@PathVariable("id") int id,
                                    RedirectAttributes ra,
                                    Model model) {
        try {
            Brand brand = brandService.getBrandById(id);
            List<Category> categoryList = categoryService.getCategoriesUsedInForm();

            model.addAttribute("brand", brand);
            model.addAttribute("categories", categoryList);
            model.addAttribute("pageTitle", "Edit Brand(ID: " + id + ")");

            return "/brands/brand-form.html";
        } catch(BrandNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/brands";
        }
    }

    @GetMapping("/brands/delete/{id}")
    public String deleteBrand(@PathVariable("id") int id,
                              RedirectAttributes ra) {
        try {
            brandService.deleteBrand(id);
            ra.addFlashAttribute("message", "Delete brand ID " + id + " successfully!");

            String uploadFile = "u/brand-logos/" + id;
            FileUploadUtils.deleteUploadDir(uploadFile);
        } catch(BrandNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/brands";
    }

}
