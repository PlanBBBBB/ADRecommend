package com.planb.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class StringUtil {

    /**
     * 将时间转换成字符串
     *
     * @param date   日期对象
     * @param format 转换格式
     * @return 转换之后的时间字符串
     */
    public static String convertDateToStr(final Date date, final String format) {
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String getRandom(int min, int max) {
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return String.valueOf(s);
    }

    /**
     * 生成数据表id
     *
     * @return String
     */
    public static String getStrId() {
        String dateStr = convertDateToStr(new Date(), "yyyyMMddHHmmss SS");
        String[] strArr = dateStr.split(" ");
        String uid = "T" + strArr[0];
        String s = strArr[1];
        int anInt = Integer.parseInt(s);
        int i = anInt * 1000;
        String sTime = getRandom(10000, i);
        String ran = getRandom(100000, 999999);
        return uid + sTime + ran;
    }
}
