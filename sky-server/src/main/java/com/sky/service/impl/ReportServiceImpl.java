package com.sky.service.impl;

import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.*;

import org.apache.http.HttpResponse;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private WorkSpaceServeImpl workSpaceServe;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ReportMapper reportMapper;
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        // 1. 查数据库（原封不动）
        List<Map<String, Object>> maps = reportMapper.turnoverStatistics(begin, end);

        // 2. 造出连续的时间轴（你的代码写得很好，稍微优化下避免改变入参 begin 的原始值）
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate currentDate = begin;
        dateList.add(currentDate);

        while (!currentDate.equals(end)) {
            currentDate = currentDate.plusDays(1);
            dateList.add(currentDate);
        }

        // 3. 【核心修改点】把你从数据库抄下来的“日期小抄”，全部强制转成 String，防类型比对失败！
        List<String> dateList1 = maps.stream()
                .map(m -> String.valueOf(m.get("dateList")))
                .collect(Collectors.toList());

        // 4. 开始遍历，找钱！
        List<String> turnoverList = new ArrayList<>();
        for (LocalDate l : dateList) {
            String dateStr = l.toString(); // "2026-04-17"

            if (!dateList1.contains(dateStr)) {
                turnoverList.add("0");
            } else {
                // 【核心解法】如果有，就回到原始账本 maps 里去翻！
                for (Map<String, Object> map : maps) {
                    // 如果这笔账的日期，等于我正在找的日期
                    if (String.valueOf(map.get("dateList")).equals(dateStr)) {
                        // 把这笔账的营业额提取出来，转成字符串塞进去
                        turnoverList.add(String.valueOf(map.get("turnoverList")));
                        break; // 找到了就不用往下翻了，跳出这层小循环
                    }
                }
            }
        }

        // 5. 解决方括号问题：直接用 Java 原生的 String.join，最干净最稳！
        // 因为 dateList 里是 LocalDate 对象，需要先转成 String 的集合
        List<String> finalDateStrList = dateList.stream()
                .map(LocalDate::toString)
                .collect(Collectors.toList());

        String dateJoin = String.join(",", finalDateStrList);
        String turnoverJoin = String.join(",", turnoverList);

        // 6. 拼装返回
        return TurnoverReportVO.builder()
                .dateList(dateJoin)
                .turnoverList(turnoverJoin)
                .build();
    }

    @Override
    public UserReportVO userReport(LocalDate begin, LocalDate end) {
        // 1. 制造一条绝对连续的时间轴（老套路了）
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate currentDate = begin;
        while (!currentDate.isAfter(end)) {
            dateList.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        // 2. 准备两个空箱子，用来装每一天的“新增数”和“总数”
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();

        // 3. 开始遍历每一天，按天去查数据库
        for (LocalDate date : dateList) {
            // 算出这一天的 00:00:00 和 23:59:59
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // -- 查询总用户数：条件是 注册时间 < 今天的 23:59:59 (不需要 beginTime 条件) --
            Map<String, Object> map = new HashMap<>();
            map.put("end", endTime);
            Integer totalUser = userMapper.countByMap(map); // 调用 Mapper 查总数

            // -- 查询新增用户：条件是 注册时间 在今天的 00:00:00 到 23:59:59 之间 --
            map.put("begin", beginTime); // 把 beginTime 也塞进 map 里
            Integer newUser = userMapper.countByMap(map);   // 调用 Mapper 查新增

            // 把查到的数字扔进我们在第 2 步准备好的箱子里
            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }

        // 4. 包装成前端非要的“逗号拼接字符串” (复习一下标准拼接法)
        // 把 List<LocalDate> 变成 "2026-04-23,2026-04-24"
        String dateJoin = String.join(",", dateList.stream().map(LocalDate::toString).collect(Collectors.toList()));

        // 把 List<Integer> 变成 "10,20" (因为原生态的 String.join 只能拼字符串，所以用 Stream 把数字转成字符串)
        String newUserJoin = String.join(",", newUserList.stream().map(String::valueOf).collect(Collectors.toList()));
        String totalUserJoin = String.join(",", totalUserList.stream().map(String::valueOf).collect(Collectors.toList()));

        // 5. 组装 VO 返回
        return UserReportVO.builder()
                .dateList(dateJoin)
                .newUserList(newUserJoin)
                .totalUserList(totalUserJoin)
                .build();
    }

    @Override
    public SalesTop10ReportVO SalesTop10(LocalDate begin, LocalDate end) {
        //根据时间区域获取limit10的菜品与销量
        //返回的是List<Map<>>，使用stream。map转化为list，在使用join方式分隔

        List<Map<String, Object>> maps = dishMapper.SalesTop10(begin, end);
        List<String> n =new ArrayList<>();
        List<String> s =new ArrayList<>();
        for(Map m : maps){
           n.add((String) m.get("name"));
           s.add(String.valueOf(m.get("sum")) );
        }
//        List<String> name = maps.stream().map(m -> m.get("name")).collect(Collectors.toList());
//        List<String> sum = maps.stream().map(m -> m.get("sum")).collect(Collectors.toList());

        String join = String.join(",", n);
        String join1 = String.join(",", s);
        return new SalesTop10ReportVO(join,join1) ;




    }

    @Override
    public OrderReportVO orderReportVOResult(LocalDate begin, LocalDate end) {
        List<String> dateList = new ArrayList<>();
        List<String> orderCountList = new ArrayList<>();
        List<String> validOrderCountList = new ArrayList<>();

        // 准备两个变量算总数
        int totalOrder = 0;
        int validOrder = 0;

        // 1. 开始一天一天循环
        while (!begin.isAfter(end)) {
            dateList.add(begin.toString()); // 把日期记下来

            // 算出当天的起点和终点
            LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(begin, LocalTime.MAX);

            // 派小弟去查今天的“总单数”和“有效单数”(5代表已完成)
            Integer dailyTotal = orderMapper.countByCondition(beginTime, endTime, null);
            Integer dailyValid = orderMapper.countByCondition(beginTime, endTime, 5);

            // 防空指针，没查到就是0
            dailyTotal = (dailyTotal == null) ? 0 : dailyTotal;
            dailyValid = (dailyValid == null) ? 0 : dailyValid;

            // 记入当天的列表
            orderCountList.add(String.valueOf(dailyTotal));
            validOrderCountList.add(String.valueOf(dailyValid));

            // 顺手把这两个数字加进总数里！
            totalOrder += dailyTotal;
            validOrder += dailyValid;

            begin = begin.plusDays(1); // 进入下一天
        }

        // 2. 计算完成率 (注意分母不能为0，强转 double 避免整数除法变成 0)
        Double rate = (totalOrder == 0) ? 0.0 : (double) validOrder / totalOrder;

        // 3. 一次性打包发货！
        return OrderReportVO.builder()
                .dateList(String.join(",", dateList))
                .orderCountList(String.join(",", orderCountList))
                .validOrderCountList(String.join(",", validOrderCountList))
                .totalOrderCount(totalOrder)
                .validOrderCount(validOrder)
                .orderCompletionRate(rate)
                .build();
    }

    @Override
    public void export(HttpServletResponse httpResponse) throws IOException {
        //查询数据库，获取营业数据
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);

        BusinessDataVO businessData = workSpaceServe.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));

        //通过poi写入到excel中
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        XSSFWorkbook excel = new XSSFWorkbook(resourceAsStream);

        XSSFSheet sheet1 = excel.getSheet("sheet1");
        sheet1.getRow(1).getCell(1).setCellValue("时间："+begin+"--"+end);


        XSSFRow row = sheet1.getRow(3);
        row.getCell(2).setCellValue(businessData.getTurnover());
        row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
        row.getCell(6).setCellValue(businessData.getNewUsers());
        for (int i = 0; i < 30; i++) {
            LocalDate plus = begin.plusDays(i);
            BusinessDataVO businessData30 = workSpaceServe.getBusinessData(LocalDateTime.of(plus, LocalTime.MIN), LocalDateTime.of(plus, LocalTime.MAX));


            //获得某一行
            row = sheet1.getRow(7 + i);
            row.getCell(1).setCellValue(plus.toString());
            row.getCell(2).setCellValue(businessData30.getTurnover());
            row.getCell(3).setCellValue(businessData30.getValidOrderCount());
            row.getCell(4).setCellValue(businessData30.getOrderCompletionRate());
            row.getCell(5).setCellValue(businessData30.getUnitPrice());
            row.getCell(6).setCellValue(businessData30.getNewUsers());
        }
        //获得第5行
        row = sheet1.getRow(4);
        row.getCell(2).setCellValue(businessData.getValidOrderCount());
        row.getCell(4).setCellValue(businessData.getUnitPrice());
        //输出流将excel下载到客户端
        ServletOutputStream out = httpResponse.getOutputStream();
        excel.write(out);



    }
}
