package com.shopme.common.classes;

import com.shopme.common.entity.Setting;

import java.util.List;

public class CurrencyAndGeneralSettingBag extends SettingBag {

    public CurrencyAndGeneralSettingBag(List<Setting> settings) {
        super(settings);
    }

    public void updateSiteLogo(String value) {
        super.updateValueOfSetting("SITE_LOGO", value);
    }

    public void updateCurrencySymbol(String value) {
        super.updateValueOfSetting("CURRENCY_SYMBOL", value);
    }

}
