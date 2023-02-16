package com.shopme.admin.customer;

import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;
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
public class CustomerController {

    private final CustomerService customerService;
    private final CountryRepository countryRepository;

    // 1
    @GetMapping("/customers")
    public String viewFirstCustomerPage(Model model) {
        return viewCustomerByPage(1, "firstName", "asc", "", model);
    }

    // 1.1
    @GetMapping("/customers/page/{pageNum}")
    public String viewCustomerByPage(@PathVariable("pageNum") int pageNum,
                                     @RequestParam("sortField") String sortField,
                                     @RequestParam("sortDir") String sortDir,
                                     @RequestParam("keyword") String keyword,
                                     Model model) {
        Page<Customer> customerPage = customerService.getCustomersByPage(pageNum, sortField, sortDir, keyword);
        List<Customer> customerList = customerPage.getContent();

        long totalElements = customerPage.getTotalElements();
        long totalPages = customerPage.getTotalPages();
        long startCount = (pageNum - 1) * customerService.NUMBER_OF_CUSTOMERS_PER_PAGE + 1;
        long endCount = startCount + customerService.NUMBER_OF_CUSTOMERS_PER_PAGE - 1;
        if(endCount < totalElements) {
            endCount = totalElements;
        }

        model.addAttribute("customers", customerList);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reversedSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        return "customer/customers.html";
    }

    // 2
    @GetMapping("/customers/update-status/{id}")
    public String updateCustomerStatus(@PathVariable("id") int id,
                                       RedirectAttributes ra) {
        Customer customer = null;
        try {
            customer = customerService.getCustomerById(id);
            boolean isEnabled = customerService.updateCustomerStatus(id).isEnabled();
            String message = "Customer ID " + id + " has been " + (isEnabled ? "enabled" : "disabled");
            ra.addFlashAttribute("message", message);
        } catch(CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/customers/page/1?pageNum=1&sortField=id&sortDir=asc&keyword=" + customer.getEmail();
    }

    // 3
    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable("id") int id,
                                 RedirectAttributes ra) {
        try {
            customerService.deleteCustomer(id);
            ra.addFlashAttribute("message", "Customer ID " + id + " has been deleted!");
        } catch(CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/customers";
    }

    // 4
    @GetMapping("/customers/edit/{id}")
    public String viewCustomerForm(@PathVariable("id") int id,
                                   RedirectAttributes ra,
                                   Model model) {
        try {
            Customer customer = customerService.getCustomerById(id);
            List<Country> countryList = countryRepository.findAllByOrderByName();

            model.addAttribute("customer", customer);
            model.addAttribute("countries", countryList);
            model.addAttribute("pageTitle", "Edit Customer (ID: " + id + ")");

            return "customer/customer-form.html";
        } catch(CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/customers";
        }
    }

    // 5
    @PostMapping("/customers/save")
    public String handleCustomerForm(@ModelAttribute @Valid Customer customer,
                                     BindingResult bindingResult,
                                     Model model,
                                     RedirectAttributes ra) {
        if(bindingResult.hasErrors()) {
            List<Country> countryList = countryRepository.findAllByOrderByName();

            model.addAttribute("customer", customer);
            model.addAttribute("countries", countryList);
            model.addAttribute("pageTitle", "Edit Customer (ID: " + customer.getId() + ")");

            return "customer/customer-form.html";
        }
        customerService.saveCustomer(customer);
        ra.addFlashAttribute("message", "Customer ID " + customer.getId() + " has been updated!");
        return "redirect:/customers";
    }

    // 6
    @GetMapping("/customers/details/{id}")
    public String viewCustomerDetails(@PathVariable("id") int id,
                                      RedirectAttributes ra,
                                      Model model) {
        try {
            Customer customer = customerService.getCustomerById(id);
            model.addAttribute("customer", customer);
            return "customer/customer-details-dialog.html";
        } catch(CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/customers";
        }
    }
}
