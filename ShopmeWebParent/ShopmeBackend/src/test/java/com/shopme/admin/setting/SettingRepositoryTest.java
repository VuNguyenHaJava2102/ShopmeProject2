package com.shopme.admin.setting;

import com.shopme.common.entity.Setting;
import com.shopme.common.enums.SettingCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SettingRepositoryTest {

    @Autowired
    private SettingRepository settingRepository;

    @Test
    public void testSaveSetting() {
//        Setting siteName = new Setting("SITE_NAME", "Shopme", SettingCategory.GENERAL);
        Setting siteLogo = new Setting("SITE_LOGO", "Shopme.png", SettingCategory.GENERAL);
        Setting copyright = new Setting("COPYRIGHT", "Copyright (C) 2023 Shopme Ltd.", SettingCategory.GENERAL);

        settingRepository.saveAll(List.of(siteLogo, copyright));
    }

    @Test
    public void testSaveCurrencySetting() {
        Setting currencyId = new Setting("CURRENCY_ID", "1", SettingCategory.CURRENCY);
        Setting symbol = new Setting("CURRENCY_SYMBOL", "$", SettingCategory.CURRENCY);
        Setting symbolPosition = new Setting("CURRENCY_SYMBOL_POSITION", "before", SettingCategory.CURRENCY);
        Setting decimalPointType = new Setting("DECIMAL_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
        Setting decimalDigits = new Setting("DECIMAL_DIGITS", "2", SettingCategory.CURRENCY);
        Setting thousandPointType = new Setting("THOUSAND_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);

        settingRepository.saveAll(List.of(currencyId, symbol, symbolPosition, decimalPointType, decimalDigits, thousandPointType));
    }

    @Test
    public void testGetSettingByKey() {
        List<Setting> settingList = settingRepository.findAll();
        int indexOfSetting = settingList.indexOf(new Setting("SITE_NAME"));

        System.err.println(indexOfSetting);
    }

    @Test
    public void testGetSettingsByCategory() {
        List<Setting> settingList = settingRepository.findByCategory(SettingCategory.GENERAL);
        System.err.println(settingList.size());
        settingList.forEach(s -> System.out.println(s.getKey()));
    }

}
