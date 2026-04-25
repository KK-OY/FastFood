package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.http.HttpResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ReportService {
    TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end);

    UserReportVO userReport(LocalDate begin, LocalDate end);

    SalesTop10ReportVO SalesTop10(LocalDate begin, LocalDate end);

    OrderReportVO orderReportVOResult(LocalDate begin, LocalDate end);

    void export(HttpServletResponse httpResponse) throws IOException;
}
