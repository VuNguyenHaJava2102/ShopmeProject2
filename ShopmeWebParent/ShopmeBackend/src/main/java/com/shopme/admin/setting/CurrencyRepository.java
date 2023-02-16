package com.shopme.admin.setting;

import com.shopme.common.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {

    List<Currency> findAllByOrderByName();

}
