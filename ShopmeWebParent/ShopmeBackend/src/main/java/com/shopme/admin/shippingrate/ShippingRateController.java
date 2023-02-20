package com.shopme.admin.shippingrate;

import com.shopme.admin.user.UserService;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.exception.ShippingRateNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@AllArgsConstructor
public class ShippingRateController {

    private final ShippingRateService shippingRateService;

    // 1
    @GetMapping("/shipping-rates")
    public String viewFirstShippingRatePage(Model model) {
        return viewShippingRateByPage("country", "asc", "", 1, model);
    }

    // 1.1
    @GetMapping("/shipping-rates/page/{pageNum}")
    public String viewShippingRateByPage(@RequestParam("sortField") String sortField,
                                         @RequestParam("sortDir") String sortDir,
                                         @RequestParam("keyword") String keyword,
                                         @PathVariable("pageNum") int pageNum,
                                         Model model) {
        Page<ShippingRate> shippingRatePage = shippingRateService.getAllByPage(sortField, sortDir, keyword, pageNum);

        List<ShippingRate> shippingRateList = shippingRatePage.getContent();
        long totalElements = shippingRatePage.getTotalElements();
        long totalPages = shippingRatePage.getTotalPages();

        long startCount = (pageNum - 1) * ShippingRateService.NUMBER_OF_SHIPPING_RATES_PER_PAGE + 1;
        long endCount = startCount + ShippingRateService.NUMBER_OF_SHIPPING_RATES_PER_PAGE - 1;
        if(endCount > totalElements) {
            endCount = totalElements;
        }

        model.addAttribute("shippingRates", shippingRateList);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("reversedSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "shippingrate/shipping-rates.html";
    }

    // 2
    @GetMapping("/shipping-rates/new")
    public String viewCreateShippingRateForm(Model model) {
        List<Country> countryList = shippingRateService.getAllCountriesOrderByName();

        model.addAttribute("shippingRate", new ShippingRate());
        model.addAttribute("countries", countryList);
        model.addAttribute("pageTitle", "Create New Shipping Rate");

        return "shippingrate/shipping-rate-form.html";
    }

    // 3
    @PostMapping("/shipping-rates/save")
    public String handleShippingRateForm(@ModelAttribute @Valid ShippingRate shippingRate,
                                         BindingResult bindingResult,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            List<Country> countryList = shippingRateService.getAllCountriesOrderByName();

            model.addAttribute("shippingRate", shippingRate);
            model.addAttribute("countries", countryList);
            model.addAttribute("pageTitle", "Create New Shipping Rate");

            return "shippingrate/shipping-rate-form.html";
        }

        shippingRateService.saveShippingRate(shippingRate);
        redirectAttributes.addFlashAttribute("message", "New shipping rate has been saved!");

        return "redirect:/shipping-rates";
    }

    // 4
    @GetMapping("/shipping-rates/edit/{id}")
    public String viewEditShippingRateForm(@PathVariable("id") int id,
                                           Model model,
                                           RedirectAttributes redirectAttributes) {
        try {
            ShippingRate shippingRate = shippingRateService.getShippingRateById(id);
            List<Country> countryList = shippingRateService.getAllCountriesOrderByName();

            model.addAttribute("shippingRate", shippingRate);
            model.addAttribute("countries", countryList);
            model.addAttribute("pageTitle", "Edit Shipping Rate (ID: " + id + ")");

            return "shippingrate/shipping-rate-form.html";
        } catch(ShippingRateNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/shipping-rates";
        }
    }

    // 5
    @GetMapping("/shipping-rates/delete/{id}")
    public String deleteShippingRate(@PathVariable("id") int id,
                                     RedirectAttributes redirectAttributes) {
        try {
            shippingRateService.deleteShippingRate(id);
            redirectAttributes.addFlashAttribute("message", "Delete shipping rate successfully!");
        } catch(ShippingRateNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/shipping-rates";
    }

    // 6
    @GetMapping("/shipping-rates/update-cod-supported/{id}")
    public String updateCodSupported(@PathVariable("id") int id,
                                     RedirectAttributes redirectAttributes) {
        ShippingRate shippingRate = null;
        try {
            shippingRate = shippingRateService.updateCodSupported(id);
            redirectAttributes.addFlashAttribute("message", "Update COD status of shipping rate ID " + id + " successfully!");
        } catch(ShippingRateNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/shipping-rates/page/1?sortField=country&sortDir=asc&keyword=" + shippingRate.getCountry().getName() + " " + shippingRate.getState();
    }
}
