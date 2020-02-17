package net.cdsunrise.ztyg.acquisition.common.utils;

import lombok.extern.slf4j.Slf4j;
import net.cdsunrise.common.utility.StringUtil;
import net.cdsunrise.ztyg.acquisition.common.enums.ExceptionEnum;
import net.cdsunrise.ztyg.acquisition.common.exception.Asserts;
import net.cdsunrise.ztyg.acquisition.common.exception.BusinessException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

@Slf4j
public class DateUtil {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DTF_D = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DTF_T = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DTF_M = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final Pattern DATE_PATTERN = Pattern.compile("^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])");
    private static final Pattern DATE_TIME_PATTERN = Pattern.compile("^[1-9]\\d{3}-(0?[1-9]|1[0-2])-(0?[1-9]|[12]\\d|3[01])\\s*(0?[0-9]|1\\d|2[0-3])(\\:(0?[0-9]|[0-5]\\d))(\\:(0?[0-9]|[0-5]\\d))$");
    private static final Pattern YEAR_PATTERN = Pattern.compile("^[1-9]\\d{3}");

    private DateUtil() {}

    public static boolean checkYear(String year) {

        return Objects.nonNull(year) && YEAR_PATTERN.matcher(year).matches();
    }

    public static boolean isDatePattern(String s){
        return !StringUtil.isEmpty(s) && DATE_PATTERN.matcher(s).matches();
    }

    public static boolean isDateTimePattern(String dateTime){
        return !StringUtil.isEmpty(dateTime) && DATE_TIME_PATTERN.matcher(dateTime).matches();
    }


    public static Long stringToLong(String date) {
        return stringToDateTime(date).getTime();
    }

    public static LocalDate stringToLocalDate(String date){
        Asserts.assertFalse(StringUtil.isEmpty(date), () -> new BusinessException(ExceptionEnum.DATE_NIL));
        Asserts.assertTrue(DATE_PATTERN.matcher(date).matches(),() -> new BusinessException(ExceptionEnum.DATE_FORMAT_ERROR));
        return LocalDate.parse(date, DTF_D);
    }

    public static LocalDateTime stringToLocalDateTime(String dateTime){
        Asserts.assertFalse(StringUtil.isEmpty(dateTime), () -> new BusinessException(ExceptionEnum.DATE_NIL));
        return LocalDateTime.parse(dateTime, DTF);
    }

    public static String toDateTimeString(LocalDateTime localDateTime){
        return DTF.format(localDateTime);
    }

    public static String toDateString(LocalDateTime localDateTime){
        return DTF_D.format(localDateTime);
    }

    public static String toDateString(LocalDate localDate){
        return DTF_D.format(localDate);
    }


    public static Date stringToDateTime(String dateTime) {
        try {
            final LocalDateTime localDateTime = stringToLocalDateTime(dateTime);
            return Date.from(localDateTime.atZone(ZoneOffset.systemDefault()).toInstant());
        } catch (Exception e) {
            log.error("convert string to date error", e);
            throw new BusinessException(ExceptionEnum.CONVERT_STRING2DATE);
        }
    }

    public static String dateTimeToStringTime(Date dateTime) {
        LocalDateTime ldt = dateTime.toInstant().atZone(ZoneOffset.systemDefault()).toLocalDateTime();
        return ldt.format(DTF_T);
    }

    public static String dateTimeToString(Date dateTime) {
        LocalDateTime ldt = dateTime.toInstant().atZone(ZoneOffset.systemDefault()).toLocalDateTime();
        return ldt.format(DTF);
    }

    public static String dateTimeToStringDate(Date dateTime) {
        LocalDateTime ldt = dateTime.toInstant().atZone(ZoneOffset.systemDefault()).toLocalDateTime();
        return ldt.format(DTF_D);
    }

    public static String dateTimeToStringOfMonth(Date dateTime) {
        LocalDateTime ldt = dateTime.toInstant().atZone(ZoneOffset.systemDefault()).toLocalDateTime();
        return ldt.format(DTF_M);
    }

    public static String getDayOfWeekByDate(Date dateTime) {
        LocalDateTime ldt = dateTime.toInstant().atZone(ZoneOffset.systemDefault()).toLocalDateTime();
        return ldt.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.CHINA);
    }

    public static Date stringToDateBySimpleDateFormat(String str, SimpleDateFormat sdf) {
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            log.error("convert string to date error", e);
            throw new BusinessException(ExceptionEnum.CONVERT_STRING2DATE);
        }
    }

    public static LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static long localDateTimeToLong(LocalDateTime localDateTime){
        Objects.requireNonNull(localDateTime);
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

    }

    public static long truncateByMinute(LocalDateTime time) {
        Objects.requireNonNull(time,"time is null");
        final long l = localDateTimeToLong(time);
        return l - l % 60000;
    }


}
