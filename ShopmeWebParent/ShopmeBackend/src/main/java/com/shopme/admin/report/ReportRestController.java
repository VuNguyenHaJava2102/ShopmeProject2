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

    @GetMapping("/reports/sales-by-date/{start-date}/{end-date}")
    public List<ReportItem> getReportDataByDatePeriod(@PathVariable("start-date") String startDateStr,
                                                      @PathVariable("end-date") String endDateStr) throws ParseException {
        return masterOrderReportService.getReportDateByDateRange(startDateStr, endDateStr);
    }
}
