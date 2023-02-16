package com.shopme.admin.setting;

import com.shopme.admin.file.FileUploadUtils;
import com.shopme.common.entity.Currency;
import com.shopme.common.classes.CurrencyAndGeneralSettingBag;
import com.shopme.common.entity.Setting;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
@AllArgsConstructor
public class SettingController {

    private final SettingService settingService;
    private final CurrencyRepository currencyRepository;

    // 1
    @GetMapping("/settings")
    public String viewAllSettings(Model model) {
        List<Setting> settingList = settingService.getAllSettings();
        List<Currency> currencyList = currencyRepository.findAllByOrderByName();

        for(Setting setting : settingList) {
            model.addAttribute(setting.getKey(), setting.getValue());
        }
        model.addAttribute("currencies", currencyList);

        return "setting/settings.html";
    }

    // 2
    @PostMapping("/settings/save-general")
    public String saveGeneralSettings(@RequestParam("image") MultipartFile multipartFile,
                                      HttpServletRequest request,
                                      RedirectAttributes ra) throws IOException {
        CurrencyAndGeneralSettingBag currencyAndGeneralSettingBag = settingService.getCurrencyAndGeneralSettingBag();

        saveSiteLogo(multipartFile, currencyAndGeneralSettingBag);
        saveCurrencySymbol(request, currencyAndGeneralSettingBag);
        saveSettingsFromForm(request, currencyAndGeneralSettingBag.getAll());

        ra.addFlashAttribute("message", "Save general settings successfully!");
        return "redirect:/settings";
    }

    // 2.1
    private void saveSiteLogo(MultipartFile multipartFile,
                              CurrencyAndGeneralSettingBag currencyAndGeneralSettingBag) throws IOException {
        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String uploadDir = "u/site-logo";

            String siteLogoValue = "/u/site-logo/" + fileName;
            currencyAndGeneralSettingBag.updateSiteLogo(siteLogoValue);

            FileUploadUtils.cleanDir(uploadDir);
            FileUploadUtils.saveFile(uploadDir, fileName, multipartFile);
        }
    }

    // 2.2
    private void saveCurrencySymbol(HttpServletRequest request,
                                    CurrencyAndGeneralSettingBag currencyAndGeneralSettingBag) {
        int selectedCurrencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
        Currency currency = currencyRepository.findById(selectedCurrencyId).get();

        if(currency != null) {
            currencyAndGeneralSettingBag.updateCurrencySymbol(currency.getSymbol());
        }
    }

    // 2.3
    private void saveSettingsFromForm(HttpServletRequest request,
                                      List<Setting> settings) {
        String oppositeValue = "";
        for(Setting setting : settings) {
            if(setting.getKey().equals("CURRENCY_SYMBOL") || setting.getKey().equals("SITE_LOGO")) {
                continue;
            }
            if(setting.getKey().equals("DECIMAL_POINT_TYPE")) {
                String value = request.getParameter(setting.getKey());
                oppositeValue = value.equals("POINT") ? "COMMA" : "POINT";
                setting.setValue(value);
                continue;
            }
            if(setting.getKey().equals("THOUSAND_POINT_TYPE")) {
                setting.setValue(oppositeValue);
                continue;
            }
            String value = request.getParameter(setting.getKey());
            setting.setValue(value);
        }
        settingService.saveSettingList(settings);
    }

    // 3
    @PostMapping("/settings/save-mail-server")
    public String saveMailServerSettings(HttpServletRequest request,
                                       RedirectAttributes ra) {
        List<Setting> mailServerSettings = settingService.getMailServerSettings();
        saveSettingsFromForm(request, mailServerSettings);
        ra.addFlashAttribute("message", "Save mail server settings successfully!");
        return "redirect:/settings";
    }

    // 3
    @PostMapping("/settings/save-mail-templates")
    public String saveMailTemplateSettings(HttpServletRequest request,
                                           RedirectAttributes ra) {
        List<Setting> mailTemplateSettings = settingService.getMailTemplateSettings();
        saveSettingsFromForm(request, mailTemplateSettings);
        ra.addFlashAttribute("message", "Save mail template settings successfully!");
        return "redirect:/settings";
    }
}
