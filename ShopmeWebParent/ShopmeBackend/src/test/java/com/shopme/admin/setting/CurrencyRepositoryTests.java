package com.shopme.admin.setting;

import com.shopme.common.entity.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CurrencyRepositoryTests {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Test
    public void testCreateCurrency() {
        Currency currency1 = new Currency("United States Dollar", "$", "USD");
        Currency currency2 = new Currency("British Pound", "£", "GPB");
        Currency currency3 = new Currency("Japanese Yen", "¥", "JPY");
        Currency currency4 = new Currency("Euro", "€", "EUR");
        Currency currency5 = new Currency("Russian Ruble", "₽", "RUB");
        Currency currency6 = new Currency("South Korean Won", "₩", "KRW");
        Currency currency7 = new Currency("Chinese Yuan", "¥", "CNY");
        Currency currency8 = new Currency("Brazilian Real", "R$", "BRL");
        Currency currency9 = new Currency("Australian Dollar", "$", "AUD");
        Currency currency10 = new Currency("Canadian Dollar", "$", "CAD");
        Currency currency11 = new Currency("Vietnamese Dong", "₫", "VND");
        Currency currency12 = new Currency("Indian Rupee", "₹", "INR");

        currencyRepository.saveAll(List.of(currency1,currency2,currency3,currency4,currency5,currency6,currency7,currency8,currency9,
                currency10,currency11,currency12));
    }

    @Test
    public void testFindAllByOrderByName() {
        List<Currency> currencyList = currencyRepository.findAllByOrderByName();
        System.err.println("Result");
        currencyList.forEach(c -> System.out.println(c.getName()));
    }
}
