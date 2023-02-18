package com.shopme.address;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.security.CustomerUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@AllArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/address-book")
    public String viewAddressBook(Model model,
                                  @AuthenticationPrincipal CustomerUserDetails customerUserDetails) {
        Customer loggedCustomer = customerUserDetails.getCustomer();

        List<Address> addressList = addressService.getAllAddresses(loggedCustomer);

        model.addAttribute("addresses", addressList);
        model.addAttribute("customer", loggedCustomer);

        return "address/address-book.html";
    }

    @GetMapping("/address-book/new")
    public String viewCreateNewAddressForm(Model model) {
        List<Country> countryList = addressService.getAllCountriesOrderByName();

        model.addAttribute("address", new Address());
        model.addAttribute("countries", countryList);
        model.addAttribute("pageTitle", "Add New Address");

        return "address/address-form.html";
    }

    @PostMapping("/address-book/save")
    public String handleAddressForm(@ModelAttribute @Valid Address address,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes,
                                    @AuthenticationPrincipal CustomerUserDetails customerUserDetails) {
        Customer customer = customerUserDetails.getCustomer();
        address.setCustomer(customer);

        if(bindingResult.hasErrors()) {
            List<Country> countryList = addressService.getAllCountriesOrderByName();

            model.addAttribute("address", address);
            model.addAttribute("countries", countryList);
            model.addAttribute("pageTitle", "Add New Address");

            return "address/address-form.html";
        }

        addressService.saveAddress(address);
        redirectAttributes.addFlashAttribute("message", "Address has been saved!");
        return "redirect:/address-book";
    }
}
