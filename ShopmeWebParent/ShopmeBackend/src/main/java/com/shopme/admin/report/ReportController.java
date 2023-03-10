package com.shopme.admin.report;

import com.shopme.admin.order.OrderService;
import com.shopme.common.entity.Setting;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class ReportController {

    private final OrderService orderService;

    @GetMapping("/reports")
    public String viewReportPage(Model model) {
        List<Setting> currencySettingList = orderService.getAllCurrencySetting();

        for(Setting setting : currencySettingList) {
            model.addAttribute(setting.getKey(), setting.getValue());
        }
        return "report/reports.html";
    }

}
