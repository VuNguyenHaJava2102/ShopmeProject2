package com.shopme.setting;

import com.shopme.common.entity.Setting;
import com.shopme.common.enums.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SettingRepository extends JpaRepository<Setting, String> {

    List<Setting> findByCategory(SettingCategory category);

    @Query("""
            SELECT s FROM Setting s WHERE s.category = :cat1 OR s.category = :cat2
            """)
    List<Setting> findByTwoCategories(@Param("cat1") SettingCategory cat1,
                                      @Param("cat2") SettingCategory cat2);
}
