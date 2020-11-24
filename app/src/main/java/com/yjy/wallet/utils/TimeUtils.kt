package com.yjy.wallet.utils

import android.annotation.SuppressLint
import android.text.TextUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 *Created by weiweiyu
 *on 2019/5/20
 * 时间处理
 */
class TimeUtils {
    companion object {
        /**
         * 时间戳格式转换
         */
        private var dayNames = arrayOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")

        fun getNewChatTime(timesamp: Long): String {
            var result = ""
            val todayCalendar = Calendar.getInstance()
            val otherCalendar = Calendar.getInstance()
            otherCalendar.timeInMillis = timesamp

            var timeFormat = "M月d日 HH:mm"
            var yearTimeFormat = "yyyy年M月d日 HH:mm"
            var am_pm = ""
            val hour = otherCalendar.get(Calendar.HOUR_OF_DAY)//判断当前是不是星期日     如想显示为：周日 12:09 可去掉此判断
            //表示是同一周
            //表示是同一个月
            when {
                hour in 0..5 -> am_pm = "凌晨"
                hour in 6..11 -> am_pm = "早上"
                hour == 12 -> am_pm = "中午"
                hour in 13..17 -> am_pm = "下午"
                hour >= 18 -> am_pm = "晚上"
            }

            timeFormat = "M月d日 " + am_pm + "HH:mm"
            yearTimeFormat = "yyyy年M月d日 " + am_pm + "HH:mm"

            val yearTemp = todayCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR)
            if (yearTemp) {
                val todayMonth = todayCalendar.get(Calendar.MONTH)
                val otherMonth = otherCalendar.get(Calendar.MONTH)
                if (todayMonth == otherMonth) {//表示是同一个月
                    val temp = todayCalendar.get(Calendar.DATE) - otherCalendar.get(Calendar.DATE)
                    when (temp) {
                        0 -> result = am_pm + getHourAndMin(timesamp)
                        1 -> result = "昨天 " + am_pm + getHourAndMin(timesamp)
                        2, 3, 4, 5, 6 -> {
                            val dayOfMonth = otherCalendar.get(Calendar.WEEK_OF_MONTH)
                            val todayOfMonth = todayCalendar.get(Calendar.WEEK_OF_MONTH)
                            result = if (dayOfMonth == todayOfMonth) {//表示是同一周
                                val dayOfWeek = otherCalendar.get(Calendar.DAY_OF_WEEK)
                                if (dayOfWeek != 1) {//判断当前是不是星期日     如想显示为：周日 12:09 可去掉此判断
                                    dayNames[otherCalendar.get(Calendar.DAY_OF_WEEK) - 1] + getHourAndMin(timesamp)
                                } else {
                                    getTime(timesamp, timeFormat)
                                }
                            } else {
                                getTime(timesamp, timeFormat)
                            }
                        }
                        else -> result = getTime(timesamp, timeFormat)
                    }
                } else {
                    result = getTime(timesamp, timeFormat)
                }
            } else {
                result = getYearTime(timesamp, yearTimeFormat)
            }
            return result
        }

        /**
         * 当天的显示时间格式
         *
         * @param time
         * @return
         */
        fun getHourAndMin(time: Long): String {
            val format = SimpleDateFormat("HH:mm")
            return format.format(Date(time))
        }

        fun getDate(time: Long): String {
            val format = SimpleDateFormat("MM-dd")
            return format.format(Date(time))
        }
        fun getyear(time: Long): String {
            val format = SimpleDateFormat("yyyy-MM-dd")
            return format.format(Date(time))
        }
        /**
         * 不同一周的显示时间格式
         *
         * @param time
         * @param timeFormat
         * @return
         */
        fun getTime(time: Long, timeFormat: String): String {
            val format = SimpleDateFormat(timeFormat)
            return format.format(Date(time))
        }

        /**
         * 不同年的显示时间格式
         *
         * @param time
         * @param yearTimeFormat
         * @return
         */
        fun getYearTime(time: Long, yearTimeFormat: String): String {
            val format = SimpleDateFormat(yearTimeFormat)
            return format.format(Date(time))
        }

        /**
         * 将时间转换为时间戳
         */
        @Throws(ParseException::class)
        fun dateToStamp(s: String?): Long {
            if (TextUtils.isEmpty(s)) {
                return 0
            }
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = simpleDateFormat.parse(s)
            return date.time
        }

        /**
         * 将时间戳转换为时间
         */
        fun stampToDate(lt: Long): String {
            val res: String
            val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            val date = Date(lt)
            res = simpleDateFormat.format(date)
            return res
        }
        fun stampToDate3(lt: Long): String {
            val res: String
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val date = Date(lt)
            res = simpleDateFormat.format(date)
            return res
        }
            /**
         * 将时间戳转换为时间
         */
        fun stampToDate2(lt: Long): String {
            val res: String
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = Date(lt)
            res = simpleDateFormat.format(date)
            return res
        }

        fun stampToYYMMDD(lt: Long): String {
            val res: String
            val simpleDateFormat = SimpleDateFormat("yyyyMMdd")
            val date = Date(lt)
            res = simpleDateFormat.format(date)
            return res
        }

        fun strToTimeEos(s: String?): Long {
            if (TextUtils.isEmpty(s)) {
                return 0
            }
            return try {
                val datestr = s?.replace("Z", " UTC")
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                val date = simpleDateFormat.parse(datestr)
                date.time + 8 * 60 * 60 * 1000
            } catch (e: Exception) {
                0
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun strToTime(s: String?): Long {
            if (TextUtils.isEmpty(s)) {
                return 0
            }
            return try {
                val datestr = s?.replace("Z", " UTC")
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z")
                val date = simpleDateFormat.parse(datestr)
                date.time
            } catch (e: Exception) {
                try {
                    val datestr = s?.replace("Z", " UTC")
                    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z")
                    val date = simpleDateFormat.parse(datestr)
                    date.time
                } catch (e: Exception) {
                    0
                }
            }

        }
    }

}