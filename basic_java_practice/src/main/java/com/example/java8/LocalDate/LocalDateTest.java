package com.example.java8.LocalDate;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * @program: java8-practice
 * @description:
 * @author: Zhan bing
 * @create: 2019-03-25
 **/
@Slf4j
public class LocalDateTest {

    @Test
    public void test1() {
        final LocalDate of  = LocalDate.of(19, 3, 1);
        final LocalDate now = LocalDate.now();
        final LocalDate of1 = LocalDate.of(2019, Month.MARCH, 25);

        System.out.println(of.toString());
        System.out.println(now.toString());
        System.out.println(of1.toString());

        log.info("添加天  月  周  年");
        System.out.println(LocalDate.now().plusDays(22L).toString());
        System.out.println(LocalDate.now().plusMonths(5).toString());
        System.out.println(LocalDate.now().plusWeeks(2).toString());
        System.out.println(LocalDate.now().plusYears(2).toString());

        log.info("减少天  月  周  年");
        System.out.println(LocalDate.now().minusDays(10).toString());
        System.out.println(LocalDate.now().minusMonths(3).toString());
        System.out.println(LocalDate.now().minusWeeks(4).toString());
        System.out.println(LocalDate.now().minusYears(1).toString());

        log.info("添加一个Duration");
        System.out.println(LocalDate.now().plus(Period.ofYears(1)));

        log.info("减少一个Duration");
        System.out.println(LocalDate.now().minus(Period.ofMonths(3)).toString());


        log.info("将月份天数 年份天数 月份 年份修改为指定的值");
        System.out.println(LocalDate.now().withDayOfMonth(22).toString());
        System.out.println(LocalDate.now().withDayOfYear(2).toString());
        System.out.println(LocalDate.now().withMonth(11).toString());
        System.out.println(LocalDate.now().withYear(2014).toString());

        log.info("获取月份天数 年份天数  星期几  年份  月份枚举值  月份数字1~12");
        System.out.println(LocalDate.now().getDayOfMonth());
        System.out.println(LocalDate.now().getDayOfYear());
        System.out.println(LocalDate.now().getDayOfWeek());
        System.out.println(LocalDate.now().getYear());
        System.out.println(LocalDate.now().getMonth().toString());
        System.out.println(LocalDate.now().getMonthValue());

        log.info("两个日期的距离");
        final LocalDate birthday = LocalDate.of(2019, 1, 3);
        System.out.println(LocalDate.now().until(birthday, ChronoUnit.DAYS));

        log.info("日期校正器");
        System.out.println(LocalDate.of(2019, 3, 25).with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).toString());
        System.out.println(LocalDate.of(2019, 3, 25).with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY)).toString());

        System.out.println(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY)).toString());
        System.out.println(LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.FRIDAY)).toString());

        System.out.println(LocalDate.now().with(TemporalAdjusters.dayOfWeekInMonth(3, DayOfWeek.FRIDAY)).toString());

        System.out.println(LocalDate.now().with(TemporalAdjusters.lastInMonth(DayOfWeek.FRIDAY)).toString());

        log.info("月的第一天  下个月的第一天   年的第一天  月的最后一天  年的最后一天");
        System.out.println(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).toString());
        System.out.println(LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth()).toString());
        System.out.println(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()).toString());
        System.out.println(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).toString());
        System.out.println(LocalDate.now().with(TemporalAdjusters.lastDayOfYear()).toString());


        log.info("本地时间");
        System.out.println(LocalTime.now());
        System.out.println(LocalTime.of(22, 30));

        log.info("-------------------------------------------------");
        System.out.println(LocalTime.now().plusHours(2).toString());
        System.out.println(LocalTime.now().plusMinutes(39).toString());
        System.out.println(LocalTime.now().plusSeconds(88).toString());
        System.out.println(LocalTime.now().plusNanos(120).toString());

        log.info("-------------------------------------------------");
        System.out.println(LocalTime.now().minusHours(2).toString());
        System.out.println(LocalTime.now().minusMinutes(2).toString());
        System.out.println(LocalTime.now().minusSeconds(222).toString());
        System.out.println(LocalTime.now().minusNanos(22222222).toString());

        log.info("-------------------------------------------------");
        System.out.println(LocalTime.now().withHour(3).toString());
        System.out.println(LocalTime.now().withSecond(22).toString());
        System.out.println(LocalTime.now().withMinute(11).toString());
        System.out.println(LocalTime.now().withNano(333333333).toString());

        log.info("-------------------------------------------------");
        System.out.println(LocalTime.now().getHour());
        System.out.println(LocalTime.now().getMinute());
        System.out.println(LocalTime.now().getSecond());
        System.out.println(LocalTime.now().getNano());

        log.info("-------------------------------------------------");
        log.info("格式化和解析");
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("E yyyy-MM-dd HH:mm:dd");

        log.info("和遗留类的转换");
        System.out.println(Date.from(Instant.now()).toInstant().toString());

        final Instant instant = new Date().toInstant();



        System.out.println(Timestamp.from(Instant.now()).getTime());


    }
}
