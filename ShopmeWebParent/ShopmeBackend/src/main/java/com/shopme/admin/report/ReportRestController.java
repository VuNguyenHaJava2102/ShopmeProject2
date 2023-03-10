package com.shopme.admin.report;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@RestController
@AllArgsConstructor
public class ReportRestController {

    private final MasterOrderReportService masterOrderReportService;

    @GetMapping("/reports/sales-by-last-x-days/{days}")
    public List<ReportItem> getReportDataByLastXDays(@PathVariable("days") int days) {
        return masterOrderReportService.getReportDataLastXDays(days);
    }

    @GetMapping("/reports/sales-by-date-range/{start-date}/{end-date}")
    public List<ReportItem> getReportDataByDateRange(@PathVariable("start-date") String startDateStr,
                                                     @PathVariable("end-date") String endDateStr) throws ParseException {
        return masterOrderReportService.getReportDataByDateRange(startDateStr, endDateStr);
    }

    @GetMapping("/reports/sales-by-last-x-months/{months}")
    public List<ReportItem> getReportDataByLastXMonths(@PathVariable("months") int months) throws ParseException {
        return masterOrderReportService.getReportDataLastXMonths(months);
    }
}
