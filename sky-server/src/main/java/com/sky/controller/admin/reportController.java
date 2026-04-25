package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/report")
public class reportController {
    @Autowired
    private ReportService reportService;

//    /admin/report/userStatistics
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        TurnoverReportVO turnoverReportVO = reportService.turnoverStatistics(begin, end);

        return Result.success(turnoverReportVO);
    }

    @GetMapping("/userStatistics")
    public Result<UserReportVO> userReport(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                           @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){

        UserReportVO userReportVO = reportService.userReport(begin, end);
        return Result.success(userReportVO);
    }

    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> SalesTop10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        SalesTop10ReportVO salesTop10ReportVO = reportService.SalesTop10(begin, end);
        return Result.success(salesTop10ReportVO);
    }


    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> orderReportVOResult(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        OrderReportVO orderReportVO = reportService.orderReportVOResult(begin, end);
        return Result.success(orderReportVO);
    }

    @GetMapping("/export")
    public Result<String> export(HttpServletResponse httpResponse) throws IOException {
        reportService.export(httpResponse);
        return Result.success();
    }
}
