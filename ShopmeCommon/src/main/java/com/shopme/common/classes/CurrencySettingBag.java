package com.shopme.common.classes;

import com.shopme.common.entity.Setting;

import java.util.List;

public class CurrencySettingBag extends SettingBag {

    public CurrencySettingBag(List<Setting> settings) {
        super(settings);
    }

    public String getCurrencySymbol() {
        return super.getSettingValueByKey("CURRENCY_SYMBOL");
    }

    public String getCurrencySymbolPosition() {
        return super.getSettingValueByKey("CURRENCY_SYMBOL_POSITION");
    }

    public String getDecimalPointType() {
        return super.getSettingValueByKey("DECIMAL_POINT_TYPE");
    }

    public String getThousandPointType() {
        return super.getSettingValueByKey("THOUSAND_POINT_TYPE");
    }

    public int getDecimalDigits() {
        return Integer.parseInt(super.getSettingValueByKey("DECIMAL_DIGITS"));
    }

}
