package com.shopme.setting;

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
@Rollback(value = false)
public class SettingRepositoryTest {

    @Autowired
    private SettingRepository settingRepository;

    @Test
    public void testGetSettingByTwoCategory() {
        List<Setting> settingList = settingRepository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
        System.err.println(settingList.size());
    }
}
