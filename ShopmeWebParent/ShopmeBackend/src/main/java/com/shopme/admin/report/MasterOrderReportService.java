package com.shopme.admin.report;

import com.shopme.admin.order.OrderRepository;
import com.shopme.common.entity.Order;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class MasterOrderReportService {
    private final DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    private final OrderRepository orderRepository;

    public List<ReportItem> getReportDataLast7Days() {
        return getReportDataLastXDays(7);
    }

    public List<ReportItem> getReportDateByDateRange(String startDateStr, String endDateStr) throws ParseException {
        Date startDate = dateFormatter.parse(startDateStr);
        Date endDate = dateFormatter.parse(endDateStr);

        List<ReportItem> reportItemList = getReportDataByPeriod(startDate, endDate);
        return reportItemList;
    }

    // private method
    private List<ReportItem> getReportDataLastXDays(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -(days));
        Date startTime = calendar.getTime();
        Date endTime = new Date();

        return getReportDataByPeriod(startTime, endTime);
    }

    private List<ReportItem> getReportDataByPeriod(Date startTime, Date endTime) {
        List<Order> orderList = orderRepository.findByOrderTimeBetween(startTime, endTime);

        List<ReportItem> reportItemList = createReportData(startTime, endTime);
        calculateSalesForReportData(orderList, reportItemList);

        printRawData(orderList);
        printReportData(reportItemList);
        return reportItemList;
    }

    // 1
    private void printRawData(List<Order> orderList) {
        orderList.forEach(o -> {
            System.out.printf("%-3d | %s | %10.2f | %10.2f\n", o.getId(), o.getOrderTime(), o.getTotal(), o.getProductCost());
        });
    }

    // 2
    private List<ReportItem> createReportData(Date startTime, Date endTime) {
        List<ReportItem> reportItemList = new ArrayList<>();

        do {
            String currentDate = dateFormatter.format(startTime);
            ReportItem reportItem = new ReportItem(currentDate);
            reportItemList.add(reportItem);

            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(startTime);
            currentCalendar.add(Calendar.DATE, 1);
            startTime = currentCalendar.getTime();
        } while(startTime.before(endTime));

        String endDateString = dateFormatter.format(endTime);
        ReportItem reportItem = new ReportItem(endDateString);
        reportItemList.add(reportItem);

        return reportItemList;
    }

    // 3
    private void calculateSalesForReportData(List<Order> orderList, List<ReportItem> reportItemList) {
        for(Order order : orderList) {
            String orderTimeString = dateFormatter.format(order.getOrderTime());
            ReportItem reportItem = new ReportItem(orderTimeString);
            int indexItem = reportItemList.indexOf(reportItem);

            if(indexItem > 0) {
                reportItem = reportItemList.get(indexItem);

                reportItem.addGrossSales(order.getTotal());
                reportItem.addNetSales(order.getSubtotal() - order.getProductCost());
                reportItem.increaseNumberOfOrder();
            }
        }
    }

    // 4
    private void printReportData(List<ReportItem> reportItemList) {
        reportItemList.forEach(item -> {
            System.out.printf("%s | %10.2f | %10.2f | %d\n", item.getIdentifier(), item.getGrossSales(), item.getNetSales(), item.getNumberOfOrder());
        });
    }
}
