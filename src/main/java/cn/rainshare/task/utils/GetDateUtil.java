package cn.rainshare.task.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetDateUtil {


    /**
     * 获取当前时间
     * @param format 时间格式化字符串
     * @return 格式化后的当前时间
     * @2021-03-23 00:10:13 721
     */
    public static String getDate(String format){
        //时间类型格式化
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }


}
