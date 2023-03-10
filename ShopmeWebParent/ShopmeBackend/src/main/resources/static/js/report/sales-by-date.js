let data;
let chartOptions;

let totalGrossSales;
let totalNetSales;
let totalOrders;

let startDateField;
let endDateField;

let loadSalesReportByDate = () => {
    let requestUrl = contextPath + "reports/sales-by-last-x-days/7";

    $.get(requestUrl, function(responseJson) {
        prepareChartData(responseJson, "day");
        customizeChart(7, "days");
        drawChart(7);
    });
}

$(document).ready(function() {
    startDateField = $("#startDate");
    endDateField = $("#endDate");

    $(".btn-last").on("click", function() {
        $(".btn-last").each(function() {
            $(this).removeClass("btn-primary").addClass("btn-light");
        });
        $(this).removeClass("btn-light").addClass("btn-primary");

        let period = parseInt($(this).attr("period"));
        if(period) {
            $("#chartSalesByDate").removeClass("d-none");
            $("#statistical").removeClass("d-none");
            $("#divCustomDateRange").addClass("d-none");
            if(period == 7 || period == 28) {
                let requestUrl = contextPath + "reports/sales-by-last-x-days/" + period;
                $.get(requestUrl, function(responseJson) {
                    prepareChartData(responseJson, "day");
                    customizeChart(period, "days");
                    drawChart(period);
                });
            } else if(period == 6 || period == 12) {
                let requestUrl = contextPath + "reports/sales-by-last-x-months/" + period;
                $.get(requestUrl, function(responseJson) {
                    prepareChartData(responseJson, "month");
                    customizeChart(period, "months");
                    drawChart(period);
                });
            }
        } else {
            $("#divCustomDateRange").removeClass("d-none");
            $("#chartSalesByDate").addClass("d-none");
            $("#statistical").addClass("d-none");
            // setDefaultDateRange();
        }
    });

    $("#btnView").on("click", function() {
        $("#chartSalesByDate").removeClass("d-none");
        $("#statistical").removeClass("d-none");
        validateDateRange();
    });
});

let setDefaultDateRange = () => {
    let toDate = new Date();
    endDateField.valueAsDate = toDate;

    let fromDate = new Date();
    fromDate.setDate(toDate.getDate() - 30);
    endDateField.valueAsDate = fromDate;
}

let validateDateRange = () => {
    let startDateStr = startDateField.val();
    let endDateStr = endDateField.val();

    let startDateMili = Date.parse(startDateStr);
    let endDateMili = Date.parse(endDateStr);

    let diffDays = (endDateMili - startDateMili) / (24 * 60 * 60 * 1000);
    if(diffDays >= 7 && diffDays <= 30) {
        let requestUrl = contextPath + "reports/sales-by-date-range/" + startDateStr + "/" + endDateStr;
        $.get(requestUrl, function(responseJson) {
            prepareChartData(responseJson, "day");
            customizeChart(diffDays, "days", "custom", startDateStr, endDateStr);
            drawChart(diffDays);
        });
    } else if(diffDays < 7 || diffDays > 30) {
        alert("The range must be in 7 - 30 days");
    }
}

// 1
let prepareChartData = (responseJson, type) => {
    data = new google.visualization.DataTable();
    if(type == "day") {
        data.addColumn("string", "Date");
    } else if(type == "month") {
        data.addColumn("string", "Month");
    }
    data.addColumn("number", "Gross Sales");
    data.addColumn("number", "Net Sales");
    data.addColumn("number", "Orders");

    totalGrossSales = 0.0;
    totalNetSales = 0.0;
    totalOrders = 0;

    $.each(responseJson, function(index, reportItem) {
        data.addRows([[reportItem.identifier, reportItem.grossSales, reportItem.netSales, reportItem.numberOfOrder]]);
        totalGrossSales += parseFloat(reportItem.grossSales);
        totalNetSales += parseFloat(reportItem.netSales);
        totalOrders += parseInt(reportItem.numberOfOrder);
    });
}

// 2
let customizeChart = (days, type, custom, startDateStr, endDateStr) => {
    chartOptions = {
        title: custom ? `Sales from ${startDateStr} to ${endDateStr}` :`Sales in last ${days} ${type}`,
        "height": 400,
        legend: {
            position: "top"
        },
        series: {
            0: {targetAxisIndex: 0},
            1: {targetAxisIndex: 0},
            2: {targetAxisIndex: 1},
        },
        vAxes: {
            0: {
                title: "Sale Amount",
                format: "currency",
            },
            1: {
                title: "Number of Orders",
            }
        }
    };

    let formatter = new google.visualization.NumberFormat({
        prefix: prefix,
        suffix: suffix,
        decimalSymbol: decimalPointType,
        groupingSymbol: thousandPointType,
        fractionDigits: decimalDigits
    });
    formatter.format(data, 1);
    formatter.format(data, 2);
}

// 3
let drawChart = (days) => {
    let salesChart = new google.visualization.ColumnChart(document.getElementById("chartSalesByDate"));
    salesChart.draw(data, chartOptions);

    $("#textTotalGrossSales").text(formatCurrency(totalGrossSales));
    $("#textTotalNetSales").text(formatCurrency(totalNetSales));
    $("#textAvgGrossSales").text(formatCurrency(totalGrossSales / days));
    $("#textAvgNetSales").text(formatCurrency(totalNetSales / days));
    $("#textTotalOrders").text(totalOrders);
}

// 3.1
let formatCurrency = (amount) => {
    let formatted = $.number(amount, decimalDigits, decimalPointType, thousandPointType);
    formatted = prefix + formatted + suffix;
    return formatted;
}