package seekLight.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 雪花算法（Snowflake）工具类，用于生成全局唯一的ID。
 * <p>
 * 结构：1位符号位 + 41位时间戳 + 10位机器ID + 12位序列号
 * </p>
 */
public class DateUtils {
    public static String dateToString(Date date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        String formattedDate = localDate.format(formatter);
        return formattedDate;
    }
}