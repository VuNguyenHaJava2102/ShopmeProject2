package com.shopme.admin.product;

import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.file.FileUploadUtils;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductDetail;
import com.shopme.common.entity.ProductImage;
import com.shopme.common.exception.ProductNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@AllArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final BrandService brandService;
    private final CategoryService categoryService;

    @GetMapping("/products")
    public String viewFirstProductPage(Model model) {
        return viewProductsByPage(1, "name", "asc", "", "-", model);
    }

    @GetMapping("/products/page/{pageNum}")
    public String viewProductsByPage(@PathVariable(name = "pageNum") int pageNum,
                                     @RequestParam(name = "sortField") String sortField,
                                     @RequestParam(name = "sortDir") String sortDir,
                                     @RequestParam(name = "keyword") String keyword,
                                     @RequestParam(name = "allParentIds", required = false) String allParentIds,
                                     Model model) {
        Page<Product> productPage = productService.getProductByPage(pageNum, sortField, sortDir, keyword, allParentIds);

        List<Category> categoryList = categoryService.getCategoriesUsedInForm();
        List<Product> productList = productPage.getContent();
        long totalElements = productPage.getTotalElements();
        long totalPages = productPage.getTotalPages();

        long startCount = (pageNum - 1) * productService.NUMBER_OF_PRODUCTS_PER_PAGE + 1;
        long endCount = startCount + productService.NUMBER_OF_PRODUCTS_PER_PAGE - 1;
        if(endCount > totalElements) {
            endCount = totalElements;
        }

        model.addAttribute("products", productList);
        model.addAttribute("categories", categoryList);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reversedSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);
        model.addAttribute("allParentIds", allParentIds);

        return "/products/products.html";
    }

    @GetMapping("/products/new")
    public String viewCreateProductForm(Model model) {
        Product product = new Product();
        List<Brand> brandList = brandService.getAllBrands();

        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("totalExtraImages", 1);
        model.addAttribute("totalDetails", 1);
        model.addAttribute("brands", brandList);
        model.addAttribute("product", product);
        model.addAttribute("pageTitle", "Create New Product");

        return "/products/product-form.html";
    }

    @PostMapping("/products/save")
    public String handleProductForm(@Valid Product product,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes ra,
                                    @RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipartFile,
                                    @RequestParam(value = "fileExtraImage", required = false) MultipartFile[] extraImageMultipartFiles,
                                    @RequestParam(value = "detailId", required = false) String[] detailIds,
                                    @RequestParam(value = "detailName", required = false) String[] detailNames,
                                    @RequestParam(value = "detailValue", required = false) String[] detailValues,
                                    @RequestParam(value = "imageIds", required = false) String[] imageIds,
                                    @RequestParam(value = "imageNames", required = false) String[] imageNames,
                                    @AuthenticationPrincipal ShopmeUserDetails loggedUser) throws IOException {

        // Save product for salesperson
        if(loggedUser.getAuthorities().toString().contains("Salesperson")) {
            if(bindingResult.hasErrors()) {
                bindingResult.getAllErrors().forEach(s -> log.info(s.getDefaultMessage()));

                try {
                    Product productInDb = productService.getProductById(product.getId());

                    product.setBrand(productInDb.getBrand());
                    product.setCategory(productInDb.getCategory());
                    product.setEnabled(productInDb.isEnabled());
                    product.setInStock(productInDb.isInStock());
                    product.setMainImage(productInDb.getMainImage());
                    product.setImages(productInDb.getImages());
                    product.setDetails(productInDb.getDetails());

                    model.addAttribute("totalExtraImages", 1);
                    model.addAttribute("totalDetails", 1);
                    model.addAttribute("message", "Some information isn't valid!");
                    model.addAttribute("product", product);
                    model.addAttribute("pageTitle", "Create New Product");
                } catch(ProductNotFoundException ex) {
                    log.info("Product not found with ID: " + product.getId());
                }
                return "/products/product-form.html";
            }

            try {
                Product productInDb = productService.getProductById(product.getId());
                productInDb.setCost(product.getCost());
                productInDb.setPrice(product.getPrice());
                productInDb.setDiscountPercent(product.getDiscountPercent());

                productService.saveProduct(productInDb);
                ra.addFlashAttribute("message", "Save product successfully!");
            } catch(ProductNotFoundException ex) {
                log.info("Product not found with ID: " + product.getId());
            }
            return "redirect:/products";
        }

        // Save product for admin and editor
        boolean isEditMode = (product.getId() != null);
        boolean isMainImageBlank = true;

        if(isEditMode || !mainImageMultipartFile.isEmpty()) {
            isMainImageBlank = false;
        }

        // Validate information
        if(bindingResult.hasErrors() || !productService.checkAliasLength(product.getAlias())
                || productService.checkNameDuplicated(product.getId(), product.getName())
                || productService.checkAliasDuplicated(product.getId(), product.getAlias(), product.getName())
                || isMainImageBlank) {
            List<Brand> brandList = brandService.getAllBrands();

            model.addAttribute("totalExtraImages", 1);
            model.addAttribute("totalDetails", 1);
            model.addAttribute("message", "Some information isn't valid!");
            model.addAttribute("product", product);
            model.addAttribute("brands", brandList);
            model.addAttribute("pageTitle", "Create New Product");

            if(!productService.checkAliasLength(product.getAlias())) {
                model.addAttribute("isAliasInvalid", true);
            }

            if(productService.checkNameDuplicated(product.getId(), product.getName())) {
                model.addAttribute("isNameDuplicated", true);
            }

            if(productService.checkAliasDuplicated(product.getId(), product.getAlias(), product.getName())) {
                model.addAttribute("isAliasDuplicated", true);
            }

            if(isMainImageBlank) {
                model.addAttribute("isMainImageBlank", true);
            }

            return "/products/product-form.html";
        }

        setMainImage(mainImageMultipartFile, product);
        setExistingExtraImage(imageIds, imageNames, product);
        setProductDetails(detailIds, detailNames, detailValues, product);

        /*
        Q: Why we need a save function here?
        A: Because we need save to check duplicated extra image after set existing extra images.
            check duplicated image function is used in setNewExtraImages function below
        */
        productService.saveProduct(product);

        setNewExtraImages(extraImageMultipartFiles, product);

        Product savedProduct = productService.saveProduct(product);
        uploadAllImages(mainImageMultipartFile, extraImageMultipartFiles, savedProduct, isEditMode);
        deleteExtraImagesRemoved(product);

        ra.addFlashAttribute("message", "Save product successfully!");

        return "redirect:/products";
    }

    // 1
    private void setMainImage(MultipartFile mainImageMultipartFile,
                              Product product) {
        if(!mainImageMultipartFile.isEmpty()) {
            String mainImageFileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename());
            product.setMainImage(mainImageFileName);
        }
    }

    // 2
    private void setExistingExtraImage(String[] imageIds,
                                       String[] imageNames,
                                       Product product) {
        // We need to check null before because in new mode, that two arrays is null, if null we break the function
        if(imageIds == null || imageNames == null) return;

        Set<ProductImage> images = new HashSet<>();

        for(int i = 0; i < imageIds.length; ++i) {
            int id = Integer.valueOf(imageIds[i]);
            String name = imageNames[i];
            images.add(new ProductImage(id, name, product));
        }
        product.setImages(images);
    }

    // 3
    private void setNewExtraImages(MultipartFile[] extraImageMultipartFiles,
                                   Product product) {
        for(MultipartFile multipartFile : extraImageMultipartFiles) {
            if(!multipartFile.isEmpty()) {
                String extraImageFileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

                /*
                - We need to check duplicated image before because in setExistingExtraImage func, it can contain some
                non-empty multipart file, this MF is e-image that we change
                */
                if(!productService.checkDuplicatedImage(extraImageFileName)) {
                    product.addExtraImage(extraImageFileName);
                }
            }
        }
    }

    // 4
    private void uploadAllImages(MultipartFile mainImageMultipartFile,
                                 MultipartFile[] extraImageMultipartFiles,
                                 Product savedProduct,
                                 boolean isEditMode) throws IOException {
        // upload main image:
        String uploadDir1 = "u/product-images/" + savedProduct.getId();
        if(!mainImageMultipartFile.isEmpty()) {
            if(isEditMode) {
                FileUploadUtils.cleanDir(uploadDir1);
            }
            FileUploadUtils.saveFile(uploadDir1, savedProduct.getMainImage(), mainImageMultipartFile);
        }

        // upload extra images
        String uploadDir2 = "u/product-images/" + savedProduct.getId() + "/extras";
        for(MultipartFile multipartFile : extraImageMultipartFiles) {
            String extraImageFileName = multipartFile.getOriginalFilename();
            if(!multipartFile.isEmpty()) {
                FileUploadUtils.saveFile(uploadDir2, extraImageFileName, multipartFile);
            }
        }
    }

    // 5
    private void setProductDetails(String[] detailIds,
                                   String[] detailNames,
                                   String[] detailValues,
                                   Product product) {
        if(detailIds == null || detailNames == null || detailValues == null) return;

        Set<ProductDetail> details = new HashSet<>();
        for(int i = 0; i < detailIds.length; ++i) {
            if(detailNames[i].isBlank() || detailValues[i].isBlank()) continue;

            if(!detailIds[i].equals("0")) {
                int id = Integer.valueOf(detailIds[i]);
                details.add(new ProductDetail(id, detailNames[i], detailValues[i], product));
            } else {
                details.add(new ProductDetail(detailNames[i], detailValues[i], product));
            }
        }
        product.setDetails(details);
    }

    // 6
    private void deleteExtraImagesRemoved(Product product) {
        String uploadDir = "u/product-images/" + product.getId() + "/extras";
        Path uploadDirPath = Paths.get(uploadDir);

        try {
            Files.list(uploadDirPath).forEach(file -> {
                String fileName = file.toFile().getName();
                if(!productService.checkDuplicatedImage(fileName)) {
                    try {
                        Files.delete(file);
                        log.info("Delete file: " + fileName + " successfully!");
                    } catch(IOException ex) {
                        log.error("Could not delete file: " + fileName);
                    }
                }
            });
        } catch(IOException ex) {
            log.error("Could not list directory: " + uploadDir);
        }
    }

    @GetMapping("/products/update-status/{id}")
    public String updateStatus(@PathVariable("id") int id,
                               RedirectAttributes ra) {
        try {
            Product product = productService.getProductById(id);
            productService.updateStatus(id);
            ra.addFlashAttribute("message", "Update status of product ID " + id + " successfully!");

            return "redirect:/products/page/1?sortField=name&sortDir=asc&allParentIds=-&keyword=" + product.getAlias();
        } catch(ProductNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/products";
        }
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id,
                                RedirectAttributes ra) {
        try {
            productService.deleteProduct(id);
            ra.addFlashAttribute("message",
                    "Delete product ID " + id + " successfully!");
        } catch(ProductNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String viewEditProductForm(@PathVariable("id") int id,
                                      Model model,
                                      RedirectAttributes ra) {
        try {
            Product product = productService.getProductById(id);
            List<Brand> brandList = brandService.getAllBrands();

            model.addAttribute("totalExtraImages", product.getImages().size() + 1);
            model.addAttribute("totalDetails", product.getDetails().size() + 1);
            model.addAttribute("brands", brandList);
            model.addAttribute("product", product);
            model.addAttribute("categoryName", product.getCategory().getName());
            model.addAttribute("pageTitle", "Edit Product ID(" + id + ")");

            return "/products/product-form.html";
        } catch(ProductNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/products";
        }
    }

    @GetMapping("/products/details/{id}")
    public String viewProductDetailsDialog(@PathVariable("id") int id,
                                           Model model,
                                           RedirectAttributes ra) {
        try {
            Product product = productService.getProductById(id);
            model.addAttribute("product", product);

            return "products/product-details-dialog.html";
        } catch(ProductNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/products";
        }
    }

}
