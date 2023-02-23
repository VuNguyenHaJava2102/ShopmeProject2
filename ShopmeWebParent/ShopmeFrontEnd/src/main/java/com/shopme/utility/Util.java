package com.shopme.utility;

import com.shopme.common.classes.CurrencySettingBag;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Util {

    public static String formatCurrency(CurrencySettingBag currencySettingBag,
                                        float number) {
        String currencySymbol = currencySettingBag.getCurrencySymbol();
        String currencySymbolPosition = currencySettingBag.getCurrencySymbolPosition();
        String decimalPointType = currencySettingBag.getDecimalPointType();
        String thousandPointType = currencySettingBag.getThousandPointType();
        int decimalDigits = currencySettingBag.getDecimalDigits();

        String pattern = "";
        pattern += currencySymbolPosition.equals("BEFORE") ? currencySymbol : "";
        pattern += "###,###";

        if(decimalDigits > 0) {
            pattern += ".";
            for(int i = 1; i <= decimalDigits; ++i) {
                pattern += "#";
            }
        }
        pattern += currencySymbolPosition.equals("AFTER") ? currencySymbol : "";

        char decimalPoint = decimalPointType.equals(",") ? ',' : '.';
        char thousandPoint = thousandPointType.equals(",") ? ',' : '.';

        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
        decimalFormatSymbols.setDecimalSeparator(decimalPoint);
        decimalFormatSymbols.setGroupingSeparator(thousandPoint);

        DecimalFormat decimalFormat = new DecimalFormat(pattern, decimalFormatSymbols);
        return decimalFormat.format(number);
    }

}
