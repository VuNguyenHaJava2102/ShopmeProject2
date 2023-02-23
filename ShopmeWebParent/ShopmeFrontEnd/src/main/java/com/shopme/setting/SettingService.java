package com.shopme.setting;

import com.shopme.common.classes.CurrencySettingBag;
import com.shopme.common.classes.EmailSettingBag;
import com.shopme.common.entity.Setting;
import com.shopme.common.enums.SettingCategory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SettingService {

    private final SettingRepository settingRepository;

    public List<Setting> getCurrencyAndGeneralSetting() {
        return settingRepository.findByTwoCategories(SettingCategory.CURRENCY, SettingCategory.GENERAL);
    }

    public EmailSettingBag getEmailSettingBag() {
        List<Setting> emailSettings = settingRepository.findByTwoCategories(SettingCategory.MAIL_SERVER, SettingCategory.MAIL_TEMPLATES);
        return new EmailSettingBag(emailSettings);
    }

    public CurrencySettingBag getCurrencySettingBag() {
        List<Setting> currencySettings = settingRepository.findByCategory(SettingCategory.CURRENCY);
        return new CurrencySettingBag(currencySettings);
    }

}
