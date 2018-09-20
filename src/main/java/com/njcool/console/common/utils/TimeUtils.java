package com.njcool.console.common.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author xfe
 * @Date 2018/9/16
 * @Desc
 */
public class TimeUtils {

    /**
     * 获取当前时间到指定时刻前的毫秒数
     * @param hour   指定时刻的小时
     * @param min    指定时刻的分钟
     * @param sec    指定时刻的秒
     * @param mill   指定时刻的毫秒
     * @return
     */
    public static long getMillSecOnMoment(int hour,int min,int sec,int mill){
        return (getMoment(hour,min,sec,mill).getTime() - System.currentTimeMillis()) /1000;
    }

    /**
     * 获取当天的某一时刻Date
     * @param hour      24小时
     * @param min       分钟
     * @param sec       秒
     * @param mill      毫秒
     * @return
     */
    private static Date getMoment(int hour,int min,int sec,int mill){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,min);
        calendar.set(Calendar.SECOND,sec);
        calendar.set(Calendar.MILLISECOND,mill);
        return calendar.getTime();
    }

}
