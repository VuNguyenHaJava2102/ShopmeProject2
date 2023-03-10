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

    // 1
    public List<ReportItem> getReportDataByDateRange(String startDateStr, String endDateStr) throws ParseException {
        Date startDate = dateFormatter.parse(startDateStr);
        Date endDate = dateFormatter.parse(endDateStr);

        Calendar endDateCalendar = Calendar.getInstance();
        endDateCalendar.setTime(endDate);
        endDateCalendar.add(Calendar.DATE, 1);
        endDate = endDateCalendar.getTime();

        return getReportDataByPeriod(startDate, endDate);
    }

    // 2
    public List<ReportItem> getReportDataLastXDays(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        Date endTime = calendar.getTime();

        calendar.add(Calendar.DATE, -(days));
        Date startTime = calendar.getTime();

        return getReportDataByPeriod(startTime, endTime);
    }

    // 3
    public List<ReportItem> getReportDataLastXMonths(int months) throws ParseException {
        List<ReportItem> reportItemList = createRawReportItemListForMonth(months);

        String lastIdentifier = reportItemList.get(0).getIdentifier();
        String startDateStr = lastIdentifier + "-01";
        Date startDate = dateFormatter.parse(startDateStr);
        Date endDate = new Date();

        List<Order> orderList = orderRepository.findByOrderTimeBetween(startDate, endDate);
        calculateSalesForReportDataForMonth(orderList, reportItemList);
        return reportItemList;
    }

    // private method
    // 1
    private List<ReportItem> getReportDataByPeriod(Date startTime, Date endTime) {
        List<Order> orderList = orderRepository.findByOrderTimeBetween(startTime, endTime);

        List<ReportItem> reportItemList = createRawReportItemList(startTime, endTime);
        calculateSalesForReportData(orderList, reportItemList);

        return reportItemList;
    }

    // 2. Ở đây ta tạo một list report item nhưng các RI này chỉ có identifier là date, month; còn lại các trường vẫn mang gt null
    // 2.1
    private List<ReportItem> createRawReportItemList(Date startTime, Date endTime) {
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

        return reportItemList;
    }

    // 2.2
    private List<ReportItem> createRawReportItemListForMonth(int months) {
        List<ReportItem> reportItemList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        for(int i = 1; i <= months; ++i) {
            calendar.add(Calendar.MONTH, -1);
            Date currentDate = calendar.getTime();

            String yearAndMonth = dateFormatter.format(currentDate).substring(0, 7);
            ReportItem reportItem = new ReportItem(yearAndMonth);
            reportItemList.add(0, reportItem);
        }
        return reportItemList;
    }

    // 3. Ở đây là dùng list RI vừa tạo để tính toán các trường còn lại dựa thêm vào list order đã lấy
    // 3.1
    private void calculateSalesForReportData(List<Order> orderList, List<ReportItem> reportItemList) {
        for(Order order : orderList) {
            String orderTimeString = dateFormatter.format(order.getOrderTime());
            ReportItem reportItem = new ReportItem(orderTimeString);
            int indexItem = reportItemList.indexOf(reportItem);

            if(indexItem >= 0) {
                reportItem = reportItemList.get(indexItem);

                reportItem.addGrossSales(order.getTotal());
                reportItem.addNetSales(order.getSubtotal() - order.getProductCost());
                reportItem.increaseNumberOfOrder();
            }
        }
    }

    // 3.2
    private void calculateSalesForReportDataForMonth(List<Order> orderList, List<ReportItem> reportItemList) {
        for(Order order : orderList) {
            String orderTimeString = dateFormatter.format(order.getOrderTime()).substring(0, 7);
            ReportItem reportItem = new ReportItem(orderTimeString);
            int indexItem = reportItemList.indexOf(reportItem);

            if(indexItem >= 0) {
                reportItem = reportItemList.get(indexItem);

                reportItem.addGrossSales(order.getTotal());
                reportItem.addNetSales(order.getSubtotal() - order.getProductCost());
                reportItem.increaseNumberOfOrder();
            }
        }
    }
}

//    private void printRawData(List<Order> orderList) {
//        orderList.forEach(o -> {
//            System.out.printf("%-3d | %s | %10.2f | %10.2f\n", o.getId(), o.getOrderTime(), o.getTotal(), o.getProductCost());
//        });
//    }
//
//    private void printReportData(List<ReportItem> reportItemList) {
//        reportItemList.forEach(item -> {
//            System.out.printf("%s | %10.2f | %10.2f | %d\n", item.getIdentifier(), item.getGrossSales(), item.getNetSales(), item.getNumberOfOrder());
//        });
//    }