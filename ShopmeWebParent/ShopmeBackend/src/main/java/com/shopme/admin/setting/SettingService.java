package com.shopme.admin.setting;

import com.shopme.common.classes.CurrencyAndGeneralSettingBag;
import com.shopme.common.entity.Setting;
import com.shopme.common.enums.SettingCategory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SettingService {

    private final SettingRepository settingRepository;

    // 1
    public List<Setting> getAllSettings() {
        return settingRepository.findAll();
    }

    // 2
    public CurrencyAndGeneralSettingBag getCurrencyAndGeneralSettingBag() {
        List<Setting> currencyAndGeneralSettingList = new ArrayList<>();

        List<Setting> currencySettings = settingRepository.findByCategory(SettingCategory.CURRENCY);
        List<Setting> generalSettings = settingRepository.findByCategory(SettingCategory.GENERAL);

        currencyAndGeneralSettingList.addAll(currencySettings);
        currencyAndGeneralSettingList.addAll(generalSettings);

        return new CurrencyAndGeneralSettingBag(currencyAndGeneralSettingList);
    }

    // 3
    public void saveSettingList(List<Setting> settings) {
        settingRepository.saveAll(settings);
    }

    // 4
    public List<Setting> getMailServerSettings() {
        return settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
    }

    // 5
    public List<Setting> getMailTemplateSettings() {
        return settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES);
    }
}
