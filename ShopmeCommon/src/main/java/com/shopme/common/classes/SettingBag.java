package com.shopme.common.classes;

import com.shopme.common.entity.Setting;

import java.util.List;

public class SettingBag {

    private List<Setting> settings;

    public SettingBag(List<Setting> settings) {
        this.settings = settings;
    }

    public Setting getSettingByKey(String key) {
        int indexOfSetting = settings.indexOf(new Setting(key));

        if(indexOfSetting >= 0) {
            return settings.get(indexOfSetting);
        }
        return null;
    }

    public String getSettingValueByKey(String key) {
        Setting setting = getSettingByKey(key);
        if(setting == null) {
            return null;
        }
        return setting.getValue();
    }

    public void updateValueOfSetting(String key, String value) {
        Setting setting = getSettingByKey(key);
        if(setting != null && !value.isBlank()) {
            setting.setValue(value);
        }
    }

    public List<Setting> getAll() {
        return this.settings;
    }
}
